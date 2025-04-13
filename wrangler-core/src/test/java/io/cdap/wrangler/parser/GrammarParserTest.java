@Test
public void testByteSizeInGrammar() throws Exception {
    String recipe = "set-column :size \"10MB\";";
    // Should parse without errors
    TestingRig.execute(recipe, Collections.emptyList());
}

@Test
public void testTimeDurationInGrammar() throws Exception {
    String recipe = "filter :time > \"500ms\";";
    // Should parse without errors
    TestingRig.execute(recipe, Collections.emptyList());
}

@Test(expected = RecipeParserException.class)
public void testInvalidByteSizeSyntax() throws Exception {
    String recipe = "set-column :size \"10XB\";"; // Invalid unit
    TestingRig.execute(recipe, Collections.emptyList());
}
