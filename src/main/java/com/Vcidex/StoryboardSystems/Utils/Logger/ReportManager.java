package com.Vcidex.StoryboardSystems.Utils.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReportManager v2
 * - Extent HTML + SLF4J mirroring to IDE console
 * - Safe to call when Extent isn't initialized
 * - Grouped sections with timings
 * - Rich attachments & helpers
 */
public final class ReportManager {

    // ---------- Configuration ----------
    /** Mirror all messages to SLF4J console logs. */
    private static volatile boolean MIRROR_TO_SLF4J =
            !"false".equalsIgnoreCase(System.getProperty("report.mirror", "true"));

    /** Include emoji in console mirror. */
    private static volatile boolean CONSOLE_EMOJI =
            !"false".equalsIgnoreCase(System.getProperty("report.emoji", "true"));

    /** Include HTML in console mirror (otherwise strip tags). */
    private static volatile boolean CONSOLE_HTML =
            "true".equalsIgnoreCase(System.getProperty("report.consoleHtml", "false"));

    // ---------- State ----------
    private static final Logger log = LoggerFactory.getLogger(ReportManager.class);
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();
    private static final ThreadLocal<Deque<SectionCtx>> STACK = ThreadLocal.withInitial(java.util.concurrent.ConcurrentLinkedDeque::new);
    private static ExtentReports extent;
    private static volatile boolean shutdownHookAdded = false;
    private static Path outputDir;
    private ReportManager(){}

    // ---------- Public configuration toggles ----------
    public static void setMirrorToSlf4j(boolean enabled){ MIRROR_TO_SLF4J = enabled; }
    public static void setConsoleEmoji(boolean enabled){ CONSOLE_EMOJI = enabled; }
    public static void setConsoleHtml(boolean enabled){ CONSOLE_HTML = enabled; }

    // ---------- Core API ----------
    public static ExtentTest getTest(){ return CURRENT.get(); }
    public static void setTest(ExtentTest t){ CURRENT.set(t); }

    public static synchronized void init(String env, String build){
        if (extent != null) return;

        String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        outputDir = Paths.get(System.getProperty("user.dir"), "test-output", "ExtentReport-" + ts);

        try { Files.createDirectories(outputDir); }
        catch (Exception e){ throw new RuntimeException("Cannot create report directory: " + outputDir, e); }

        ExtentSparkReporter spark = new ExtentSparkReporter(outputDir.resolve("index.html").toFile());
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Storyboard Systems – Automation");
        spark.config().setReportName("Storyboard Automation");
        spark.config().setTimelineEnabled(true);
        spark.config().setEncoding("utf-8");
        spark.config().thumbnailForBase64(true);
        spark.config().setCss("""
        :root{
          --sb-accent:#2599ff; --sb-pass:#16a34a; --sb-fail:#ef4444; --sb-warn:#d97706;
          --sb-muted:#94a3b8; --sb-bg:#0f172a; --sb-card:#111827;
        }
        body, .report-name { letter-spacing:.2px; }
        .navbar { position: sticky; top: 0; z-index: 999; }
        .test .name{ font-size:15px; font-weight:600 }
        .category { font-size:11px; padding:2px 6px; border-radius:8px; background:#0b1220; }
        .badge.pass{ background:var(--sb-pass)!important }
        .badge.fail{ background:var(--sb-fail)!important }
        .badge.warning{ background:var(--sb-warn)!important }
        .table, table, .table th, .table td { border-color:#1f2937!important }
        .code-block { border-radius:10px; }
        .brand-logo{font-weight:700;letter-spacing:.3px;margin:.25rem 0 .5rem;color:#e5e7eb}
        table.sb { border-collapse:collapse;width:100%; font-size:13px }
        table.sb td, table.sb th { border:1px solid #334155; padding:6px 8px }
        table.sb tr:nth-child(even){ background:#0b1220 }
        .sb-chip { display:inline-block; padding:2px 8px; border-radius:999px; font-size:12px; background:#0b1220; color:#e2e8f0 }
        .sb-chip.pass{ background:rgba(22,163,74,.15); color:#86efac }
        .sb-chip.fail{ background:rgba(239,68,68,.15); color:#fecaca }
        .sb-grid{ display:grid; grid-template-columns:repeat(auto-fit,minmax(260px,1fr)); gap:12px }
        .sb-card{ background:#0b1220; border:1px solid #1f2937; border-radius:12px; padding:12px }
        .sb-kv{ display:flex; justify-content:space-between; gap:8px; font-size:13px }
        .sb-kv b{ color:#cbd5e1 }
        """);
        spark.config().setJs("document.querySelectorAll('.test').forEach(t=>t.classList.add('open'));");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Env", Objects.toString(env, ""));
        extent.setSystemInfo("Build", Objects.toString(build, ""));

        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { flush(); } catch (Throwable ignored) {}
            }));
            shutdownHookAdded = true;
        }

        System.out.println("✅ Extent initialized: " + outputDir.resolve("index.html").toAbsolutePath());
        mirrorInfo("Extent initialized → " + outputDir.resolve("index.html").toAbsolutePath().toString());
    }

    public static void flush(){
        if (extent != null) {
            extent.flush();
            if (outputDir != null) {
                String p = outputDir.resolve("index.html").toAbsolutePath().toString();
                System.out.println("🧾 Extent flushed → " + p);
                mirrorInfo("Extent flushed → " + p);
            }
        }
    }

    public static ExtentReports extent(){ return extent; }

    public static ExtentTest createTest(String name, String... categories){
        requireExtent();
        ExtentTest t = extent.createTest(name);
        if (categories != null && categories.length > 0) t.assignCategory(categories);
        CURRENT.set(t);
        return t;
    }

    /** Create a nested node and make it current until scope is closed. */
    public static Scope with(ExtentTest node){ return new Scope(node); }

    public static void group(String title, Runnable body){
        ExtentTest parent = CURRENT.get();
        if (parent == null) {
            mirrorWarn("No current ExtentTest before group('" + title + "'). Creating a root on-the-fly.");
            if (extent != null) {
                parent = extent.createTest(title);
                CURRENT.set(parent);
            }
        }

        ExtentTest section = (parent != null) ? parent.createNode(title).assignCategory("Section") : null;
        SectionCtx ctx = new SectionCtx(title, System.nanoTime(), section);
        STACK.get().push(ctx);
        if (section != null) CURRENT.set(section);

        mirrorInfo(prefix("▶", "SECTION ") + title);

        long started = System.nanoTime();
        try {
            body.run();
            long dur = nanosToMillis(System.nanoTime() - started);
            if (section != null) section.log(Status.PASS, "✅ Completed in " + dur + " ms");
            mirrorPass("Completed: " + title + " (" + dur + " ms)");
        } catch (Throwable t) {
            long dur = nanosToMillis(System.nanoTime() - started);
            if (section != null) section.log(Status.FAIL, "❌ Failed in " + dur + " ms: " + escape(t.toString()));
            mirrorFail("Failed: " + title + " (" + dur + " ms) – " + t);
            throw t;
        } finally {
            STACK.get().poll();
            if (!STACK.get().isEmpty()) {
                ExtentTest top = STACK.get().peek().node();
                CURRENT.set(top != null ? top : parent);
            } else {
                CURRENT.set(parent);
            }
        }
    }

    public static Path outDir(){ return outputDir; }

    // ---------- Logging helpers ----------
    public static void info(String msg){
        ExtentTest t = getTest();
        if (t != null && msg != null) t.info(escape(msg));
    }
    private static String escape(String s){ return s == null ? "" : s.replace("<","&lt;").replace(">","&gt;"); }

    public static void warn(String msg){
        if (msg == null) return;
        ExtentTest t = CURRENT.get();
        if (t != null) t.warning(escape(msg));
        mirrorWarn(msg);
    }

    public static void pass(String msg){
        if (msg == null) return;
        ExtentTest t = CURRENT.get();
        if (t != null) t.pass(msg);
        mirrorPass(msg);
    }

    public static void fail(String msg){
        if (msg == null) return;
        ExtentTest t = CURRENT.get();
        if (t != null) t.fail(msg);
        mirrorFail(msg);
    }

    public static void skip(String msg){
        if (msg == null) return;
        ExtentTest t = CURRENT.get();
        if (t != null) t.skip(escape(msg));
        mirrorInfo(prefix("⏭", "SKIP ") + msg);
    }

    public static void infoHtml(String html){
        ExtentTest t = getTest();
        if (t != null && html != null) t.info(html);
    }
    public static void warnHtml(String html){
        ExtentTest t = getTest();
        if (t != null && html != null) t.warning(html);
    }
    public static void passHtml(String html){
        ExtentTest t = getTest();
        if (t != null && html != null) t.pass(html);
    }

    /** Attach a simple table (String[][]) to the current node. */
    public static void table(String[][] rows, String title){
        ExtentTest t = CURRENT.get();
        if (t == null || rows == null) { mirrorWarn("table() called but no current test or rows==null"); return; }
        if (title != null && !title.isBlank()) {
            t.info("<div class='brand-logo'>" + escape(title) + "</div>");
            mirrorInfo("[TABLE] " + title);
        }
        t.info(MarkupHelper.createTable(rows));
    }

    /** Attach a code block to the current node. */
    public static void codeBlock(String code){
        ExtentTest t = CURRENT.get();
        if (t == null || code == null) { mirrorWarn("codeBlock() called but no current test or code==null"); return; }
        t.info(MarkupHelper.createCodeBlock(code));
        mirrorInfo("[CODE]\n" + safeConsole(code));
    }

    /** Attach pretty JSON to the current node. */
    public static void json(String json){
        ExtentTest t = CURRENT.get();
        if (t == null || json == null) { mirrorWarn("json() called but no current test or json==null"); return; }
        t.info(MarkupHelper.createCodeBlock(json, com.aventstack.extentreports.markuputils.CodeLanguage.JSON));
        mirrorInfo("[JSON]\n" + safeConsole(json));
    }

    /** Attach an existing file path (screenshot, artifact). */
    public static void attachFile(String title, Path file){
        ExtentTest t = CURRENT.get();
        if (t == null || file == null) { mirrorWarn("attachFile() called but no current test or file==null"); return; }
        try {
            if (!Files.exists(file)) { warn("attachFile: file not found → " + file); return; }
            t.info(title == null ? "Attachment" : escape(title),
                    MediaEntityBuilder.createScreenCaptureFromPath(file.toAbsolutePath().toString()).build());
            mirrorInfo("[ATTACH] " + (title == null ? file.toString() : title) + " → " + file.toAbsolutePath());
        } catch (Exception e){
            warn("attachFile failed: " + e.getMessage());
        }
    }

    public static void screenshot(Path png){ attachFile("Screenshot", png); }

    public static final class Scope implements AutoCloseable {
        private final ExtentTest prev;
        public Scope(ExtentTest next){
            this.prev = CURRENT.get();
            CURRENT.set(next);
        }
        public void close(){ CURRENT.set(prev); }
    }
    private record SectionCtx(String title, long startedNanos, ExtentTest node){}

    private static void requireExtent(){
        if (extent == null) {
            mirrorWarn("Extent not initialized. Call ReportManager.init(...) first.");
            throw new IllegalStateException("Extent not initialized. Call ReportManager.init(...) first.");
        }
    }

    private static long nanosToMillis(long n){ return Math.max(0, n / 1_000_000L); }
    private static String stripHtml(String s){ return s == null ? "" : s.replaceAll("<[^>]*>", ""); }
    private static String safeConsole(String s){ return CONSOLE_HTML ? s : stripHtml(s); }
    private static String prefix(String emoji, String fallback){ return CONSOLE_EMOJI ? (emoji + " ") : fallback; }

    // ---------- SLF4J mirroring ----------
    private static void mirrorInfo(String msg){ if (MIRROR_TO_SLF4J && msg != null) log.info(safeConsole(msg)); }
    private static void mirrorWarn(String msg){ if (MIRROR_TO_SLF4J && msg != null) log.warn(safeConsole(msg)); }
    private static void mirrorPass(String msg){ if (MIRROR_TO_SLF4J && msg != null) log.info(safeConsole(prefix("✅","PASS ") + msg)); }
    private static void mirrorFail(String msg){ if (MIRROR_TO_SLF4J && msg != null) log.error(safeConsole(prefix("❌","FAIL ") + msg)); }
}