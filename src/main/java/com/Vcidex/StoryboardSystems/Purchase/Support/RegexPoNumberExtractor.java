package com.Vcidex.StoryboardSystems.Purchase.Support;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Regex strategy, tolerant to formats like "PO No: ABC-123" or "PO# ABC-123". */
public class RegexPoNumberExtractor implements PoNumberExtractor {
    private static final Pattern P = Pattern.compile(
            "(PO\\s*(No|#|:)?\\s*[-:]?\\s*)([A-Za-z0-9/_-]+)", Pattern.CASE_INSENSITIVE
    );

    @Override
    public Optional<String> extract(String raw) {
        if (raw == null) return Optional.empty();
        Matcher m = P.matcher(raw);
        if (m.find()) return Optional.ofNullable(m.group(3)).map(String::trim);

        // Fallback: best-effort last token
        String[] parts = raw.trim().split("\\s+");
        if (parts.length == 0) return Optional.empty();
        String last = parts[parts.length - 1].replaceAll("[^A-Za-z0-9/_-]", "");
        return last.isEmpty() ? Optional.empty() : Optional.of(last);
    }
}