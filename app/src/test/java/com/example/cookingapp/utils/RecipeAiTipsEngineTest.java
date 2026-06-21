package com.example.cookingapp.utils;

import com.example.cookingapp.models.Recipe;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RecipeAiTipsEngineTest {

    @Test
    public void generateTips_forVeganRecipe_hasAtLeastThreeTips() {
        Recipe recipe = new Recipe(
                "1",
                "Dau hu xao nam",
                "Mon chay",
                Arrays.asList("mon chay", "xao"),
                20,
                250,
                "",
                Arrays.asList("Dau hu", "Nam", "Nuoc tuong"),
                Arrays.asList("So che", "Xao nhanh tren lua lon")
        );

        List<String> tips = RecipeAiTipsEngine.generateTips(recipe);

        assertTrue(tips.size() >= 3);
        assertTrue(tips.get(0).length() > 10);
    }

    @Test
    public void buildTipsText_forSoupRecipe_containsNumberedList() {
        Recipe recipe = new Recipe(
                "2",
                "Pho ga",
                "An sang",
                Arrays.asList("pho", "nuoc dung"),
                45,
                420,
                "",
                Arrays.asList("Ga", "Hanh", "Gung"),
                Arrays.asList("Ham xuong", "Nau nuoc dung")
        );

        String text = RecipeAiTipsEngine.buildTipsText(recipe);

        assertFalse(text.trim().isEmpty());
        assertTrue(text.contains("1) "));
    }

    @Test
    public void generateTips_supportsAccentedVietnameseInput() {
        Recipe recipe = new Recipe(
                "3",
                "Phở gà",
                "Ăn sáng",
                Arrays.asList("món nước"),
                40,
                400,
                "",
                Arrays.asList("Gà", "Hành", "Gừng"),
                Arrays.asList("Hầm nước dùng", "Nêm nếm")
        );

        List<String> tips = RecipeAiTipsEngine.generateTips(recipe);
        String joined = String.join(" ", tips).toLowerCase();

        assertTrue(joined.contains("nước dùng") || joined.contains("hầm") || joined.contains("đậm vị"));
    }

    @Test
    public void buildTipsText_usesVietnameseDiacritics() {
        Recipe recipe = new Recipe(
                "4",
                "Gà kho",
                "Món mặn",
                Arrays.asList("món mặn", "kho"),
                35,
                500,
                "",
                Arrays.asList("Thịt gà", "Tiêu", "Tỏi"),
                Arrays.asList("Ướp gà", "Kho nhỏ lửa")
        );

        String text = RecipeAiTipsEngine.buildTipsText(recipe);

        assertTrue(text.contains("Ướp") || text.contains("Giữ") || text.contains("nấu"));
    }

    @Test
    public void generateTips_differentRecipes_shouldNotBeIdentical() {
        Recipe vegan = new Recipe(
                "vegan-1",
                "Bun dau hu xao nam",
                "Mon chay",
                Arrays.asList("mon chay"),
                25,
                320,
                "",
                Arrays.asList("Dau hu", "Nam", "Bun"),
                Arrays.asList("Xao nam", "Tron bun")
        );

        Recipe dessert = new Recipe(
                "dessert-1",
                "Che dau do",
                "Trang mieng",
                Arrays.asList("trang mieng", "che"),
                30,
                280,
                "",
                Arrays.asList("Dau do", "Nuoc cot dua", "Duong"),
                Arrays.asList("Nau dau", "Them duong", "Them nuoc cot dua")
        );

        List<String> veganTips = RecipeAiTipsEngine.generateTips(vegan);
        List<String> dessertTips = RecipeAiTipsEngine.generateTips(dessert);

        assertNotEquals(veganTips, dessertTips);
    }
}
