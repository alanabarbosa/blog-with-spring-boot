package io.github.alanabarbosa.util;

import java.text.Normalizer;

public class NormalizeSlug {
    public static String normalizeString(String titleFormatted) {
        var title = titleFormatted;
        
        var slugFormatted = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^a-zA-Z0-9\\s-]", "")
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        
        if (!slugFormatted.isEmpty()) return slugFormatted;
        
        return "";
    }
}