// File: src/main/java/com/Vcidex/StoryboardSystems/Utils/UIEventListener.java
package com.Vcidex.StoryboardSystems.Utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIEventListener implements WebDriverListener {
    private static final Logger logger = LoggerFactory.getLogger(UIEventListener.class);

    @Override
    public void beforeGet(WebDriver driver, String url) {
        logger.debug("Navigate ▶ {}", url);
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        logger.debug("Landed on ▶ {}", url);
    }

    @Override
    public void beforeClick(WebElement element) {
        logger.debug("Click ▶ {}", describe(element));
    }

    @Override
    public void afterClick(WebElement element) {
        logger.debug("Clicked ▶ {}", describe(element));
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keys) {
        logger.debug("Type ▶ {} : '{}'", describe(element), String.join("", keys));
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keys) {
        logger.debug("Typed ▶ {} : '{}'", describe(element), String.join("", keys));
    }

    // ---- Describe one element consistently ----
    private String describe(WebElement el) {
        try {
            String tag         = el.getTagName();
            String id          = el.getAttribute("id");
            String name        = el.getAttribute("name");
            String placeholder = el.getAttribute("placeholder");
            String text        = el.getText();

            var sb = new StringBuilder(tag != null ? tag : "");
            if (id != null && !id.isEmpty())          sb.append("#").append(id);
            if (name != null && !name.isEmpty())      sb.append("[name=").append(name).append("]");
            if (placeholder != null && !placeholder.isEmpty())
                sb.append("[placeholder=").append(placeholder).append("]");
            if (text != null && !text.isEmpty())      sb.append(" text=\"").append(text).append("\"");

            return sb.toString();
        } catch (Exception e) {
            return "<unknown>";
        }
    }
}
