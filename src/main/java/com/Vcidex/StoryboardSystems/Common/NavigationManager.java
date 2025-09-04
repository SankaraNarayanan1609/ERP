package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;

public class NavigationManager extends BasePage {

    private static final Duration NAV_WAIT = Duration.ofSeconds(45);
    private static final Duration SHORT    = Duration.ofSeconds(3);

    // === Generic roots ===
    private static final By LEGACY_SIDEBAR = By.cssSelector(".sidebar, .side-menu, nav.sidebar, aside");
    private static final List<By> SIDEBAR_CANDIDATES = Arrays.asList(
            LEGACY_SIDEBAR,
            By.cssSelector(".sidenav"),
            By.cssSelector(".sidenav-nav"),
            By.cssSelector("nav[role='navigation'], [role='navigation']")
    );

    // NEW: sublevel panel lives outside the sidebar in your app
    private static final List<By> SUBLEVEL_CANDIDATES = Arrays.asList(
            By.cssSelector("layout-sublevel-menu ul.sublevel-nav"),
            By.cssSelector(".sublevel-nav")
    );

    // Header modules (“Systems / Sales / Purchase / Inventory …”)
    // FIX: don't require a `.head` ancestor
    private static final By TOP_MODULE_BTNS =
            By.cssSelector(".head-content-left .head-menu-item-name-btn, .head-menu-item-name-btn, button.head-menu-item-name-btn");

    // Sidebar togglers ...
    private static final List<By> TOGGLER_CANDIDATES = Arrays.asList(
            By.cssSelector(".sidenav .logo-container .logo"),
            By.cssSelector(".sidenav .btn-close"),
            By.cssSelector(".sidebar-toggle"),
            By.cssSelector(".hamburger"),
            By.cssSelector(".menu-toggle"),
            By.cssSelector(".navbar-toggler"),
            By.cssSelector("button[aria-label='Menu']"),
            By.cssSelector("i.bi-list, .bi.bi-list")
    );

    private static final By NAV_PROBE = By.xpath(
            "//*[self::nav or self::aside or @role='navigation' or " +
                    "contains(@class,'sidebar') or contains(@class,'side-menu') or " +
                    "contains(@class,'sidenav') or contains(@class,'menu') or contains(@class,'nav') or " +
                    "self::layout-sublevel-menu or contains(@class,'sublevel-nav')]"
    );

    // Scoped state
    private WebElement lastMenuRoot;       // left sidebar container
    private WebElement sublevelRoot;       // RIGHT sublevel container (ul.sublevel-nav)
    private WebElement lastExpandedModule; // last expanded node (li/a)

    public NavigationManager(WebDriver driver) { super(driver); }

    // ---------------- Public API ----------------

    public void goTo(String headerOrModule, String itemText) {
        final String nItem = normalizeLabel(headerOrModule, itemText);
        step(MasterLogger.Layer.UI, "Navigate ▶ " + headerOrModule + " → " + nItem, (Runnable) () -> {
            switchToAppContext(headerOrModule, nItem);
            ensureTopModuleIfPresent(headerOrModule);
            ensureSidebarOpen();
            // In 2-arg form, treat the first as the sidebar module when present (e.g., "Purchase")
            expandModule(headerOrModule);
            refreshSublevelRoot(); // <— important for your DOM
            clickMenuItem(nItem);
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
        });
    }

    public void goTo(String headerText, String moduleText, String itemText) {
        final String nSub  = normalizeLabel(headerText, moduleText);
        final String nItem = normalizeLabel(headerText, itemText);
        step(MasterLogger.Layer.UI, "Navigate ▶ " + headerText + " → " + nSub + " → " + nItem, (Runnable) () -> {
            switchToAppContext(headerText, nSub, nItem);
            ensureTopModuleIfPresent(headerText);
            ensureSidebarOpen();
            // FIX: expand the sidebar module (nSub), NOT the header
            expandModule(nSub);
            refreshSublevelRoot(); // <— pick up the rendered sublevel panel
            // keep tolerant extra step (no-op if not present)
            expandSubmenuIfPresent(nSub);
            clickMenuItem(nItem);
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
        });
    }

    public void open(String headerText, String itemText, Predicate<WebDriver> pageReady) {
        final String nItem = normalizeLabel(headerText, itemText);
        step(MasterLogger.Layer.UI, "Open ▶ " + headerText + " → " + nItem, (Runnable) () -> {
            switchToAppContext(headerText, nItem);
            ensureTopModuleIfPresent(headerText);
            ensureSidebarOpen();
            expandModule(headerText);
            refreshSublevelRoot();
            clickMenuItem(nItem);
            waitUntilPageMatches(pageReady); // Required type:String Provided:Predicate<org.openqa.selenium.WebDriver>
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
        });
    }

    public void open(String headerText, String moduleText, String itemText, Predicate<WebDriver> pageReady) {
        final String nSub  = normalizeLabel(headerText, moduleText);
        final String nItem = normalizeLabel(headerText, itemText);
        step(MasterLogger.Layer.UI, "Open ▶ " + headerText + " → " + nSub + " → " + nItem, (Runnable) () -> {
            switchToAppContext(headerText, nSub, nItem);
            ensureTopModuleIfPresent(headerText);
            ensureSidebarOpen();
            // FIX: expand the sidebar module (nSub)
            expandModule(nSub);
            refreshSublevelRoot();
            expandSubmenuIfPresent(nSub);
            clickMenuItem(nItem);
            waitUntilPageMatches(pageReady);
            waitForAngularRequestsToFinish();
            waitForOverlayClear();
        });
    }

    // ---------------- Frame/App context ----------------
    // (unchanged except the NAV_PROBE above)

    // ---------------- High-level steps ----------------

    private void ensureTopModuleIfPresent(String moduleText) {
        try {
            List<WebElement> btns = driver.findElements(TOP_MODULE_BTNS);
            if (btns.isEmpty()) return;

            WebElement active = null, candidate = null;
            String needle = moduleText == null ? "" : moduleText.trim().toLowerCase();

            for (WebElement b : btns) {
                String cls = (b.getAttribute("class") == null) ? "" : b.getAttribute("class").toLowerCase();
                boolean isActive = cls.contains("activeheader"); // covers "activeHeader"
                boolean matches   = textOrAttrs(b).contains(needle);
                if (isActive && matches) { active = b; break; }
                if (matches && candidate == null) candidate = b;
            }
            if (active != null) return; // already on desired header
            if (candidate != null) {
                safeClick(candidate);
                waitForOverlayClear();
                sleep(150);
            }
        } catch (Exception ignore) {}
    }

    // ---------------- Find & expand ----------------

    private void expandModule(String moduleText) {
        if (moduleText == null || moduleText.isBlank()) return;
        if (lastMenuRoot == null) lastMenuRoot = resolveMenuRootLenient();
        if (lastMenuRoot == null)
            throw new org.openqa.selenium.NoSuchElementException("Navigation container not found");

        String pred = labelPredicate(moduleText);

        By anyNodeInModule = By.xpath(
                // a <li> with icon/title match (your top icons are <a> with title)
                ".//li[contains(@class,'sidenav-nav-item')]//*[(" + pred + ")]" +
                        " | .//li[contains(@class,'sidenav-nav-item')]//*[self::a or self::button][.//*[(" + pred + ")]]" +
                        " | .//*[self::a or self::button or self::div or self::span or self::i][(" + pred + ")]"
        );

        WebElement hit = firstDisplayedWithin(lastMenuRoot, anyNodeInModule, NAV_WAIT);
        if (hit == null) {
            debugDumpMenuText("Module not found → '" + moduleText + "'");
            throw new org.openqa.selenium.NoSuchElementException("Module not found in sidebar: " + moduleText);
        }

        WebElement clickTarget = closestClickable(hit);
        if (clickTarget == null) clickTarget = hit;

        lastExpandedModule = ancestorLi(clickTarget);
        if (lastExpandedModule == null) lastExpandedModule = clickTarget;

        try { scrollWithin(lastMenuRoot, clickTarget); } catch (Exception ignore) {}
        try { clickTarget.click(); } catch (Exception e) { jsClick(clickTarget); }
        // give Angular a moment to paint the sibling sublevel panel
        waitChildrenAppearNear(lastExpandedModule, Duration.ofSeconds(1));
        refreshSublevelRoot();
    }

    /** Sublevel might not exist or might live outside the sidebar — update our pointer if it does. */
    private void refreshSublevelRoot() {
        sublevelRoot = resolveSublevelRoot();
    }

    private WebElement resolveSublevelRoot() {
        for (By by : SUBLEVEL_CANDIDATES) {
            try {
                for (WebElement el : driver.findElements(by)) {
                    if (el.isDisplayed()) return el;
                }
            } catch (Exception ignore) {}
        }
        return null;
    }

    // ---------------- Utils ----------------

    private WebElement findDisplayedGlobal(By by, Duration wait) {
        long end = System.currentTimeMillis() + wait.toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                for (WebElement el : driver.findElements(by)) {
                    try { if (el.isDisplayed()) return el; }
                    catch (StaleElementReferenceException ignored) {}
                }
            } catch (Exception ignore) {}
            sleep(120);
        }
        return null;
    }

    /** Normalize aliases per header context. */
    private String normalizeLabel(String header, String label) {
        if (label == null) return null;
        String l = label.replaceAll("\\s+", " ").trim();
        String h = header == null ? "" : header.trim().toLowerCase();

        if ("purchase".equals(h)) {
            if (equalsIgnoreCaseAny(l, "Indent List", "Indent", "Purchase Indents", "Indent  List")) return "Purchase Indent";
            // (optional) map common invoice aliases -> "Invoice"
            if (equalsIgnoreCaseAny(l, "Receive Invoice", "Invoice Entry", "Purchase Invoice")) return "Invoice";
            if (equalsIgnoreCaseAny(l, "Single Payment", "Payment Entry")) return "Payment";
        }
        return l;
    }

    // debugDumpMenuText(): include sublevel root too
    private void debugDumpMenuText(String reason) {
        try {
            MasterLogger.warn("NAV DEBUG: " + reason);
            WebElement root = (lastMenuRoot != null) ? lastMenuRoot : resolveMenuRootLenient();
            if (root == null) { MasterLogger.warn(" - sidebar root is null"); }
            WebElement sub  = (sublevelRoot != null) ? sublevelRoot : resolveSublevelRoot();
            if (sub == null) { MasterLogger.warn(" - sublevel root is null"); }

            int shown = 0, limit = 15;
            for (WebElement r : new WebElement[]{root, sub}) {
                if (r == null) continue;
                for (WebElement n : r.findElements(By.xpath(".//*[self::a or self::button or self::div or self::span or self::i]"))) {
                    if (!n.isDisplayed()) continue;
                    String t = n.getText();
                    String title = n.getAttribute("title");
                    String aria  = n.getAttribute("aria-label");
                    if ((t != null && !t.isBlank()) || (title != null && !title.isBlank()) || (aria != null && !aria.isBlank())) {
                        MasterLogger.warn(" - item: text='" + (t==null?"":t.trim()) + "' title='" + (title==null?"":title) + "' aria='" + (aria==null?"":aria) + "'");
                        if (++shown >= limit) { MasterLogger.warn(" - … truncated …"); break; }
                    }
                }
            }
        } catch (Exception ignore) {}
    }

    /** Try default content, then scan iframes for a nav container or any provided labels. */
    private void switchToAppContext(String... hintLabels) {
        // Always start from top
        try { driver.switchTo().defaultContent(); } catch (Throwable ignore) {}

        // 1) Quick resolve in default context
        lastMenuRoot = resolveMenuRootLenient();
        sublevelRoot = resolveSublevelRoot();
        if (lastMenuRoot != null || sublevelRoot != null) return;

        // 2) Hop into frames that contain any nav-like container
        if (switchToFrameContaining(NAV_PROBE, 0, 3)) {
            lastMenuRoot = resolveMenuRootLenient();
            sublevelRoot = resolveSublevelRoot();
            if (lastMenuRoot != null || sublevelRoot != null) return;
        }

        // 3) Fallback: search frames using any label hints (module/submenu/item text)
        if (hintLabels != null) {
            for (String hint : hintLabels) {
                if (hint == null || hint.isBlank()) continue;
                String pred = labelPredicate(hint);
                By labelProbe = By.xpath(
                        "//*[(self::a or self::button or self::div or self::span or self::i) and (" + pred + ")]"
                );
                try { driver.switchTo().defaultContent(); } catch (Throwable ignore) {}
                if (switchToFrameContaining(labelProbe, 0, 3)) {
                    lastMenuRoot = resolveMenuRootLenient();
                    sublevelRoot = resolveSublevelRoot();
                    return;
                }
            }
        }

        // 4) Last attempt: back to default and resolve again
        try { driver.switchTo().defaultContent(); } catch (Throwable ignore) {}
        lastMenuRoot = resolveMenuRootLenient();
        sublevelRoot = resolveSublevelRoot();
    }

    /** Depth-first search for a frame that contains an element matching `probe`. */
    private boolean switchToFrameContaining(By probe, int depth, int maxDepth) {
        if (depth > maxDepth) return false;
        if (!driver.findElements(probe).isEmpty()) return true;

        List<WebElement> frames = driver.findElements(By.cssSelector("iframe, frame"));
        for (int i = 0; i < frames.size(); i++) {
            driver.switchTo().frame(i);
            if (switchToFrameContaining(probe, depth + 1, maxDepth)) return true;
            driver.switchTo().parentFrame();
        }
        return false;
    }

    // ========== MISSING HELPERS: paste inside NavigationManager ==========

    private void ensureSidebarOpen() {
        try {
            lastMenuRoot = resolveMenuRootLenient();
            // if visible & has any labels/icons, we're good
            if (lastMenuRoot != null && lastMenuRoot.isDisplayed() && hasVisibleMenuLabelsOrIcons(lastMenuRoot)) return;

            // try common togglers (collapsed menu)
            WebElement toggler = firstDisplayed(TOGGLER_CANDIDATES);
            if (toggler != null) {
                scrollIntoView(toggler);
                for (int i = 0; i < 3; i++) {
                    safeClick(toggler);
                    sleep(200);
                    lastMenuRoot = resolveMenuRootLenient();
                    if (lastMenuRoot != null && hasVisibleMenuLabelsOrIcons(lastMenuRoot)) break;
                }
            }
        } catch (Exception ignore) {}
    }

    // NavigationManager.waitUntilPageMatches(...)
    public void waitUntilPageMatches(String expectedUrlPart,
                                     By pageH1orH3,        // e.g., By.xpath("//h3[normalize-space()='Purchase Indent']")
                                     By cheapProbe) {      // something that appears early, e.g., the page container

        wait.until(ExpectedConditions.urlContains(expectedUrlPart));

        // header first (appears early)
        wait.until(ExpectedConditions.presenceOfElementLocated(pageH1orH3));

        // probe (appears before forms)
        wait.until(ExpectedConditions.presenceOfElementLocated(cheapProbe));

        // then ensure Angular settled (see helper below)
        waitForAngularAndIdle();
    }


    private String textOrAttrs(WebElement el) {
        try {
            String txt = (el.getText() == null ? "" : el.getText()).trim().toLowerCase();
            if (!txt.isBlank()) return txt;
            String title = val(el.getAttribute("title"));
            String aria  = val(el.getAttribute("aria-label"));
            String dataT = val(el.getAttribute("data-title"));
            String tip   = val(el.getAttribute("data-tooltip"));
            return (title + " " + aria + " " + dataT + " " + tip).trim().toLowerCase();
        } catch (Exception e) { return ""; }
    }
    private String val(String s) { return s == null ? "" : s; }

    private WebElement resolveMenuRootLenient() {
        for (By by : SIDEBAR_CANDIDATES) {
            try {
                for (WebElement el : driver.findElements(by)) {
                    if (el.isDisplayed()) return el;
                }
            } catch (Exception ignore) {}
        }
        return null;
    }

    private WebElement firstDisplayedWithin(WebElement root, By by, Duration wait) {
        long end = System.currentTimeMillis() + wait.toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                for (WebElement el : root.findElements(by)) {
                    try { if (el.isDisplayed()) return el; }
                    catch (StaleElementReferenceException ignored) { /* continue */ }
                }
            } catch (StaleElementReferenceException ignored) {
                // root went stale; let caller re-resolve
            } catch (Exception ignore) {}
            sleep(120);
        }
        return null;
    }

    /** Case-insensitive predicate matching visible text OR common label attributes. */
    private String labelPredicate(String raw) {
        String low = raw == null ? "" : raw.trim().toLowerCase();
        String lit = escapeXpathLiteral(low);
        String tr = "translate(%s,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
        return "contains(" + String.format(tr, "normalize-space(.)") + "," + lit + ")" +
                " or contains(" + String.format(tr, "@title") + "," + lit + ")" +
                " or contains(" + String.format(tr, "@aria-label") + "," + lit + ")" +
                " or contains(" + String.format(tr, "@data-title") + "," + lit + ")" +
                " or contains(" + String.format(tr, "@data-tooltip") + "," + lit + ")";
    }

    private WebElement ancestorLi(WebElement node) {
        WebElement cur = node;
        for (int i = 0; i < 6 && cur != null; i++) {
            if ("li".equalsIgnoreCase(cur.getTagName())) return cur;
            cur = parentOf(cur);
        }
        return null;
    }
    private WebElement parentOf(WebElement node) {
        try { return (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].parentElement;", node); }
        catch (Exception e) { return null; }
    }

    private void scrollWithin(WebElement scrollContainer, WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "const c=arguments[0], e=arguments[1];" +
                            "if(!c||!e) return;" +
                            "const cr=c.getBoundingClientRect();" +
                            "const er=e.getBoundingClientRect();" +
                            "if(er.top<cr.top) c.scrollTop -= (cr.top - er.top) + 20;" +
                            "else if(er.bottom>cr.bottom) c.scrollTop += (er.bottom - cr.bottom) + 20;", scrollContainer, el);
        } catch (Exception ignore) {}
        scrollIntoView(el);
    }

    private void waitChildrenAppearNear(WebElement parent, Duration dur) {
        long end = System.currentTimeMillis() + dur.toMillis();
        while (System.currentTimeMillis() < end) {
            try {
                if (!parent.isDisplayed()) break;
                if (!parent.findElements(By.cssSelector("ul.sublevel-nav, layout-sublevel-menu ul.sublevel-nav")).isEmpty()) return;
                if (!parent.findElements(By.xpath(".//*[self::ul or self::ol or contains(@class,'submenu') or self::div[contains(@class,'collapse')]]")).isEmpty()) return;
            } catch (StaleElementReferenceException ignored) { return; }
            sleep(80);
        }
    }

    private WebElement closestClickable(WebElement node) {
        WebElement cur = node;
        for (int i = 0; i < 6 && cur != null; i++) {
            String tag = cur.getTagName();
            if ("a".equalsIgnoreCase(tag) || "button".equalsIgnoreCase(tag)) return cur;
            cur = parentOf(cur);
        }
        return null;
    }

    private void bringToFrontIfCovered(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "const el=arguments[0];" +
                            "const r=el.getBoundingClientRect();" +
                            "const x=Math.floor(r.left + r.width/2), y=Math.floor(r.top + r.height/2);" +
                            "const a=document.elementFromPoint(x,y);" +
                            "if(a && a!==el && !el.contains(a)){" +
                            "  el.scrollIntoView({block:'center'});" +
                            "}", el);
        } catch (Exception ignore) {}
    }

    private boolean equalsIgnoreCaseAny(String s, String... options) {
        if (s == null) return false;
        for (String o : options) if (s.equalsIgnoreCase(o)) return true;
        return false;
    }

    // --- helpers used by ensureSidebarOpen() ---

    private boolean hasVisibleMenuLabelsOrIcons(WebElement root) {
        try {
            // text labels
            for (WebElement s : root.findElements(By.cssSelector(".sublevel-link-text, .sidenav-link-text"))) {
                if (s.isDisplayed() && !s.getText().isBlank()) return true;
            }
            // icon-only items with title/aria/data-* hints
            for (WebElement i : root.findElements(By.cssSelector("i[title], [aria-label], [data-title], [data-tooltip]"))) {
                if (i.isDisplayed()) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private WebElement firstDisplayed(List<By> candidates) {
        for (By by : candidates) {
            try {
                for (WebElement el : driver.findElements(by)) {
                    if (el.isDisplayed()) return el;
                }
            } catch (Exception ignore) {}
        }
        return null;
    }
    private void expandSubmenuIfPresent(String subMenuText) {
        if (subMenuText == null || subMenuText.isBlank()) return;
        if (lastMenuRoot == null) lastMenuRoot = resolveMenuRootLenient();

        String pred = labelPredicate(subMenuText);
        By subBy = By.xpath(
                // a real submenu under an expanded LI
                ".//ul[contains(@class,'sublevel-nav')]//a[.//span[(" + pred + ")] or (" + pred + ")]" +
                        // or an icon/button/div/span/i anywhere (e.g., sibling module like Payable)
                        " | .//*[self::a or self::button or self::div or self::span or self::i][(" + pred + ")]"
        );

        WebElement scope = (lastExpandedModule != null ? lastExpandedModule : lastMenuRoot);
        WebElement subNode = firstDisplayedWithin(scope, subBy, Duration.ofSeconds(8));
        if (subNode == null && lastMenuRoot != null)
            subNode = firstDisplayedWithin(lastMenuRoot, subBy, Duration.ofSeconds(5));

        if (subNode == null) {
            MasterLogger.warn("Submenu/module not found → '" + subMenuText + "' (continuing)");
            return;
        }

        WebElement clickTarget = closestClickable(subNode);
        if (clickTarget == null) clickTarget = subNode;

        if (!isExpanded(clickTarget)) { // Cannot resolve method 'isExpanded' in 'NavigationManager'
            try { scrollWithin(lastMenuRoot != null ? lastMenuRoot : clickTarget, clickTarget); } catch (Exception ignore) {}
            try { clickTarget.click(); } catch (Exception e) { jsClick(clickTarget); }
            waitChildrenAppearNear(ancestorLi(clickTarget) != null ? ancestorLi(clickTarget) : clickTarget, Duration.ofSeconds(1));
        }
        lastExpandedModule = ancestorLi(clickTarget) != null ? ancestorLi(clickTarget) : clickTarget;
    }

    private void clickMenuItem(String itemText) {
        if (lastMenuRoot == null) lastMenuRoot = resolveMenuRootLenient();

        String pred = labelPredicate(itemText);
        WebElement scope = (lastExpandedModule != null) ? lastExpandedModule : lastMenuRoot;

        By candidate = By.xpath(
                ".//ul[contains(@class,'sublevel-nav')]//a[.//span[(" + pred + ")] or (" + pred + ")]" +
                        " | .//*[self::a or self::button][(" + pred + ")]" +
                        " | .//*[self::a or self::button][.//*[(" + pred + ")]]"
        );

        WebElement target = firstDisplayedWithin(scope, candidate, Duration.ofSeconds(8));
        if (target == null && lastMenuRoot != null)
            target = firstDisplayedWithin(lastMenuRoot, candidate, Duration.ofSeconds(6));

        // NEW: search the detached container(s)
        if (target == null) {
            List<WebElement> subContainers = driver.findElements(By.cssSelector("layout-sublevel-menu, .sublevel-nav"));
            for (WebElement c : subContainers) {
                target = firstDisplayedWithin(c, candidate, Duration.ofSeconds(2));
                if (target != null) break;
            }
        }

        // Fuzzy fallback
        if (target == null) target = fuzzyFindInNav(itemText); // Cannot resolve method 'fuzzyFindInNav' in 'NavigationManager'

        if (target == null) {
            debugDumpMenuText("Menu item not found → '" + itemText + "'");
            throw new org.openqa.selenium.NoSuchElementException("Menu item not found: " + itemText);
        }

        try { if (lastMenuRoot != null) scrollWithin(lastMenuRoot, target); else scrollIntoView(target); } catch (Exception ignore) {}
        bringToFrontIfCovered(target);
        try { target.click(); } catch (WebDriverException e) { jsClick(target); }
    }

    // Checks if a menu node is visually expanded (aria/class or visible sub-UL)
    private boolean isExpanded(WebElement el) {
        try {
            String aria = el.getAttribute("aria-expanded");
            if (aria != null && "true".equalsIgnoreCase(aria)) return true;

            WebElement li = ancestorLi(el);
            if (li != null) {
                String cls = li.getAttribute("class");
                if (cls != null && (cls.contains("open") || cls.contains("expanded") || cls.contains("show"))) return true;

                List<WebElement> subs = li.findElements(By.cssSelector("ul.sublevel-nav, layout-sublevel-menu ul.sublevel-nav"));
                for (WebElement s : subs) if (s.isDisplayed()) return true;
            }
        } catch (Exception ignore) {}
        return false;
    }

    /** Fuzzy fallback when an exact/structural match fails (works across sidebar & sublevel panel) */
    private WebElement fuzzyFindInNav(String label) {
        if (label == null || label.isBlank()) return null;

        // Try scopes in order of likelihood
        List<WebElement> scopes = new ArrayList<>();
        if (lastExpandedModule != null) scopes.add(lastExpandedModule);
        if (lastMenuRoot != null)      scopes.add(lastMenuRoot);
        if (sublevelRoot != null)      scopes.add(sublevelRoot);

        // Last resort: search whole document
        try { scopes.add(driver.findElement(By.tagName("body"))); } catch (Exception ignore) {}

        String target = label.trim().toLowerCase();
        Set<String> tokens = tokenize(target);

        // First pass: exact (case-insensitive) match on visible text
        for (WebElement scope : scopes) {
            List<WebElement> anchors;
            try { anchors = scope.findElements(By.cssSelector("a, button")); }
            catch (StaleElementReferenceException e) { continue; }

            for (WebElement a : anchors) {
                if (!a.isDisplayed()) continue;
                String text = (a.getText() == null ? "" : a.getText()).trim();
                if (!text.isEmpty() && text.equalsIgnoreCase(label.trim())) return a;
            }
        }

        // Second pass: score by token overlap against text + title + aria-label
        int bestScore = 0;
        WebElement best = null;

        for (WebElement scope : scopes) {
            List<WebElement> anchors;
            try { anchors = scope.findElements(By.cssSelector("a, button")); }
            catch (StaleElementReferenceException e) { continue; }

            for (WebElement a : anchors) {
                if (!a.isDisplayed()) continue;

                String hay = (a.getText() == null ? "" : a.getText()).toLowerCase();
                hay += " " + val(a.getAttribute("title")).toLowerCase();
                hay += " " + val(a.getAttribute("aria-label")).toLowerCase();
                hay = hay.replaceAll("\\s+", " ").trim();
                if (hay.isBlank()) continue;

                int score = scoreTokens(tokens, hay);
                if (score > bestScore) { bestScore = score; best = a; }
            }

            // If we found something in this scope, prefer it over searching globally
            if (bestScore > 0) break;
        }
        return bestScore > 0 ? best : null;
    }

    private Set<String> tokenize(String s) {
        Set<String> t = new LinkedHashSet<>();
        for (String p : s.split("[^a-z0-9]+")) {
            if (p.length() >= 3) t.add(p);
        }
        return t;
    }

    private int scoreTokens(Set<String> tokens, String hay) {
        int sc = 0;
        String low = hay.toLowerCase();
        for (String tk : tokens) if (low.contains(tk)) sc++;
        return sc;
    }
    /** Page-ready by predicate (used by navigators with custom checks). */
    public void waitUntilPageMatches(java.util.function.Predicate<WebDriver> pageReady) {
        new WebDriverWait(driver, NAV_WAIT)
                .until(d -> {
                    try { return pageReady != null && pageReady.test(d); }
                    catch (Throwable t) { return false; }
                });
        // settle after it "looks right"
        waitForAngularAndIdle();
    }
}