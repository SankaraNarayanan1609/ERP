package com.Vcidex.StoryboardSystems.Utils;

public enum ToastType {

    SUCCESS("toast-success", "rgba(92, 184, 92, 1)"),
    ERROR("toast-error", "rgba(217, 83, 79, 1)"),
    INFO("toast-info", "rgba(91, 192, 222, 1)"),
    WARNING("toast-warning", "rgba(248, 148, 6, 1)");

    public final String cssClass;
    public final String expectedBgColor;

    ToastType(String cssClass, String expectedBgColor) {
        this.cssClass = cssClass;
        this.expectedBgColor = expectedBgColor;
    }

    public static ToastType fromClass(String classAttr) {
        if (classAttr == null) return null;
        if (classAttr.contains("toast-success")) return SUCCESS;
        if (classAttr.contains("toast-error")) return ERROR;
        if (classAttr.contains("toast-info")) return INFO;
        if (classAttr.contains("toast-warning")) return WARNING;
        return null;
    }
}