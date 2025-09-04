package com.Vcidex.StoryboardSystems.Utils;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        context.get().put(key, value);
    }

    public static <T> T get(String key, Class<T> type, T defaultValue) {
        Object v = context.get().get(key);
        return type.isInstance(v) ? type.cast(v) : defaultValue;
    }


    public static void clear() {
        context.remove();
    }

    public static String getCurrentTestName() {
        return (String) context.get().getOrDefault("testName", "UnnamedTest");
    }
}