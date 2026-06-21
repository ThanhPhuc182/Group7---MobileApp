package com.example.cookingapp.utils;

import com.example.cookingapp.models.Recipe;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Lightweight on-device "AI" tips engine using recipe context.
 * This avoids network/API key dependencies while still giving smart guidance.
 */
public final class RecipeAiTipsEngine {

    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final String[] GENERIC_TIPS = {
            "Chuẩn bị sẵn nguyên liệu theo từng nhóm trước khi nấu để tiết kiệm thời gian và tránh quên bước.",
            "Nếu món bị mặn, thêm chút nước ấm hoặc rau củ để cân bằng vị thay vì thêm đường.",
            "Nấu thử vị từng bước, điều chỉnh nhẹ từng ít một để tránh vượt ngưỡng mặn/ngọt.",
            "Giúp món đẹp mắt hơn bằng cách giữ lại một ít rau thơm và thêm ngay trước khi dùng."
    };

    private RecipeAiTipsEngine() {
    }

    public static String buildTipsText(Recipe recipe) {
        List<String> tips = generateTips(recipe);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tips.size(); i++) {
            builder.append(i + 1).append(") ").append(tips.get(i));
            if (i < tips.size() - 1) {
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }

    public static List<String> generateTips(Recipe recipe) {
        Set<String> tips = new LinkedHashSet<>();
        String context = toContext(recipe);

        // Method-based hints
        if (context.contains("chien") || context.contains("xao") || context.contains("nuong") || context.contains("ran")) {
            tips.add("Làm nóng chảo trước 1-2 phút, sau đó mới thêm dầu để giảm dính và giữ độ giòn.");
        }
        if (context.contains("ham") || context.contains("kho") || context.contains("rim")) {
            tips.add("Giữ lửa nhỏ ở giai đoạn cuối để nước sốt sánh lại tự nhiên và ngấm vị tốt hơn.");
        }
        if (context.contains("sup") || context.contains("canh") || context.contains("pho") || context.contains("nuoc dung")) {
            tips.add("Hầm nước dùng nhỏ lửa và vớt bọt thường xuyên để nước trong, vị thanh hơn.");
        }

        // Category/tag/ingredient hints
        if (context.contains("chay") || context.contains("dau hu") || context.contains("nam")) {
            tips.add("Thêm nấm hương hoặc dầu mè để tăng vị umami tự nhiên cho món chay.");
        }
        if (context.contains("trang mieng") || context.contains("banh") || context.contains("che")) {
            tips.add("Giảm 10-15% lượng đường so với công thức ban đầu, sau đó điều chỉnh theo khẩu vị.");
        }
        if (context.contains("bo") || context.contains("ga") || context.contains("heo") || context.contains("suon")) {
            tips.add("Ướp trước 15-30 phút với chút muối + tiêu + tỏi băm để món đậm vị hơn.");
        }

        int seed = stableSeed(recipe);
        int start = seed % GENERIC_TIPS.length;
        for (int i = 0; i < GENERIC_TIPS.length && tips.size() < 4; i++) {
            tips.add(GENERIC_TIPS[(start + i) % GENERIC_TIPS.length]);
        }

        List<String> result = new ArrayList<>(tips);
        return result.subList(0, Math.min(4, result.size()));
    }

    private static int stableSeed(Recipe recipe) {
        if (recipe == null) {
            return 0;
        }
        String key = "";
        if (recipe.getId() != null) {
            key = recipe.getId();
        } else if (recipe.getName() != null) {
            key = recipe.getName();
        }
        return Math.abs(normalizeText(key).hashCode());
    }

    private static String toContext(Recipe recipe) {
        if (recipe == null) {
            return "";
        }
        StringBuilder context = new StringBuilder();
        append(context, recipe.getName());
        append(context, recipe.getCategory());

        if (recipe.getTags() != null) {
            for (String tag : recipe.getTags()) {
                append(context, tag);
            }
        }

        if (recipe.getIngredients() != null) {
            for (String ingredient : recipe.getIngredients()) {
                append(context, ingredient);
            }
        }

        if (recipe.getSteps() != null) {
            for (String step : recipe.getSteps()) {
                append(context, step);
            }
        }

        return context.toString();
    }

    private static void append(StringBuilder builder, String value) {
        if (value != null && !value.trim().isEmpty()) {
            builder.append(' ').append(normalizeText(value));
        }
    }

    private static String normalizeText(String input) {
        String lower = input.toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return COMBINING_MARKS.matcher(normalized).replaceAll("")
                .replace('đ', 'd');
    }
}
