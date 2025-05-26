package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

public class UIEventListener implements WebDriverListener {
    @Override
    public void beforeGet(WebDriver driver, String url) {
        UIActionLogger.debug("Navigate ▶ " + url);
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        UIActionLogger.debug("Landed on ▶ " + url);
    }

    @Override
    public void beforeClick(WebElement element) {
        UIActionLogger.debug("Click ▶ " + describe(element));
    }

    @Override
    public void afterClick(WebElement element) {
        UIActionLogger.debug("Clicked ▶ " + describe(element));
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keys) {
        UIActionLogger.debug("Type ▶ " + describe(element) + " : '" + String.join("", keys) + "'");
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keys) {
        UIActionLogger.debug("Typed ▶ " + describe(element) + " : '" + String.join("", keys) + "'");
    }

    // ---- Use only ONE describe method! ----
    private String describe(WebElement el) {
        try {
            String tag = el.getTagName();
            String id = el.getAttribute("id");
            String name = el.getAttribute("name");
            String placeholder = el.getAttribute("placeholder");
            String text = el.getText();
            return String.format("%s%s%s%s%s",
                    tag != null ? tag : "",
                    (id != null && !id.isEmpty()) ? "#" + id : "",
                    (name != null && !name.isEmpty()) ? "[name=" + name + "]" : "",
                    (placeholder != null && !placeholder.isEmpty()) ? "[placeholder=" + placeholder + "]" : "",
                    (text != null && !text.isEmpty()) ? " text=\"" + text + "\"" : ""
            );
        } catch (Exception e) {
            return "<unknown>";
        }
    }
}