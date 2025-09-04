package com.Vcidex.StoryboardSystems.Purchase.Support;

import java.util.Optional;

/** SRP: just parses a PO number from text. */
public interface PoNumberExtractor {
    Optional<String> extract(String raw);
}