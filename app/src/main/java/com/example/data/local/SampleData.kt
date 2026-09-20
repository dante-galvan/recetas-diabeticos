package com.example.data.local

import com.app.diabeticos.R
import com.example.data.model.Difficulty
import com.example.data.model.GroceryCategory
import com.example.data.model.Ingredient
import com.example.data.model.NutritionInfo
import com.example.data.model.Recipe
import com.example.data.model.RecipeCategory

object SampleData {
  val recipes: List<Recipe> = listOf(
    Recipe(
      id = "salmon_asparagus_quinoa",
      titleEs = "Salmón a las Hierbas con Espárragos y Quinoa",
      titleEn = "Herb-Grilled Salmon with Asparagus & Quinoa",
      descriptionEs = "Filete de salmón rico en omega-3 y grasas cardiosaludables, servido con espárragos crocantes y una porción controlada de quinoa integral.",
      descriptionEn = "Omega-3 rich wild salmon fillet with roasted asparagus spears and a controlled portion of low-GI golden quinoa.",
      category = RecipeCategory.FISH,
      secondaryCategories = listOf(RecipeCategory.DINNER, RecipeCategory.LUNCH, RecipeCategory.LOW_CARB, RecipeCategory.GLUTEN_FREE),
      prepTimeMinutes = 10,
      cookTimeMinutes = 15,
      difficulty = Difficulty.EASY,
      defaultServings = 2,
      nutrition = NutritionInfo(
        calories = 385,
        totalCarbsGrams = 18.0,
        sugarsGrams = 2.5,
        proteinGrams = 34.0,
        fatGrams = 19.0,
        saturatedFatGrams = 2.8,
        fiberGrams = 4.5,
        sodiumMg = 280.0
      ),
      ingredients = listOf(
        Ingredient("Filetes de salmón fresco", "Fresh wild salmon fillets", 2.0, "unidades", GroceryCategory.MEAT_PROTEIN),
        Ingredient("Espárragos verdes tiernos", "Tender green asparagus spears", 200.0, "g", GroceryCategory.VEGETABLES),
        Ingredient("Quinoa tricolor cocida", "Cooked tricolor quinoa", 100.0, "g", GroceryCategory.GRAINS),
        Ingredient("Aceite de oliva virgen extra", "Extra virgin olive oil", 1.5, "cucharadas", GroceryCategory.PANTRY),
        Ingredient("Limón fresco (jugo y rodajas)", "Fresh lemon (juice & wedges)", 1.0, "unidad", GroceryCategory.FRUITS),
        Ingredient("Diente de ajo picado", "Minced garlic clove", 1.0, "diente", GroceryCategory.VEGETABLES),
        Ingredient("Eneldo fresco picado", "Fresh chopped dill", 1.0, "cucharada", GroceryCategory.PANTRY)
      ),
      stepsEs = listOf(
        "Enjuagar los espárragos y cortar los extremos leñosos. Salpimentar con un toque de aceite de oliva.",
        "Sazonar los filetes de salmón con eneldo fresco, ajo picado, una pizca de sal marina y gotas de limón.",
        "Calentar una sartén antiadherente a fuego medio-alto y sellar el salmón durante 4 minutos por lado hasta que esté dorado pero jugoso.",
        "En la misma sartén o al horno a 190°C, saltear los espárragos de 6 a 8 minutos hasta que queden al dente.",
        "Servir cada filete sobre una base tibia de quinoa cocida y acompañar con los espárragos asados."
      ),
      stepsEn = listOf(
        "Rinse asparagus spears and trim the woody stems. Toss lightly with olive oil.",
        "Season salmon fillets with chopped fresh dill, minced garlic, a pinch of sea salt, and fresh lemon juice.",
        "Heat a non-stick skillet over medium-high heat and sear salmon for 4 minutes per side until golden yet succulent.",
        "Roast or sauté asparagus for 6-8 minutes until vibrant and tender-crisp.",
        "Plate each fillet over a base of fluffy warm quinoa and arrange asparagus alongside."
      ),
      tags = listOf("Omega-3", "Low Carb", "Gluten Free", "Dinner", "High Protein"),
      imageRes = R.drawable.img_recipe_salmon,
      isFeatured = true,
      isPopular = true,
      isNew = false
    ),

    Recipe(
      id = "mediterranean_chicken_bowl",
      titleEs = "Bowl Mediterráneo con Pollo y Aguacate",
      titleEn = "Mediterranean Chicken & Avocado Bowl",
      descriptionEs = "Plato completo y equilibrado con pechuga de pollo marinada, aguacate cremoso, espinacas frescas y tomates cherry.",
      descriptionEn = "Vibrant balanced bowl packed with lean grilled chicken breast, creamy ripe avocado, fresh spinach, and cherry tomatoes.",
      category = RecipeCategory.LUNCH,
      secondaryCategories = listOf(RecipeCategory.CHICKEN, RecipeCategory.SALADS, RecipeCategory.QUICK_MEALS, RecipeCategory.LOW_CARB),
      prepTimeMinutes = 12,
      cookTimeMinutes = 12,
      difficulty = Difficulty.EASY,
      defaultServings = 2,
      nutrition = NutritionInfo(
        calories = 410,
        totalCarbsGrams = 14.0,
        sugarsGrams = 3.0,
        proteinGrams = 38.0,
        fatGrams = 22.0,
        saturatedFatGrams = 3.5,
        fiberGrams = 6.5,
        sodiumMg = 320.0
      ),
      ingredients = listOf(
        Ingredient("Pechuga de pollo en tiras", "Skinless chicken breast strips", 300.0, "g", GroceryCategory.MEAT_PROTEIN),
        Ingredient("Aguacate maduro en láminas", "Ripe sliced avocado", 1.0, "unidad", GroceryCategory.FRUITS),
        Ingredient("Espinacas baby frescas", "Fresh baby spinach leaves", 120.0, "g", GroceryCategory.VEGETABLES),
        Ingredient("Tomates cherry cortados a la mitad", "Halved cherry tomatoes", 150.0, "g", GroceryCategory.VEGETABLES),
        Ingredient("Pepino en rodajas finas", "Crisp sliced cucumber", 1.0, "unidad", GroceryCategory.VEGETABLES),
        Ingredient("Aceite de oliva virgen extra", "Extra virgin olive oil", 1.0, "cucharada", GroceryCategory.PANTRY),
        Ingredient("Orégano seco y zumo de limón", "Dried oregano & fresh lemon juice", 1.0, "cucharadita", GroceryCategory.PANTRY)
      ),
      stepsEs = listOf(
        "Marinar las tiras de pechuga de pollo con limón, orégano, pimienta negra y una cucharadita de aceite de oliva.",
        "Cocinar el pollo en plancha caliente durante 5 a 6 minutos hasta dorar uniformemente.",
        "Lavar y secar las espinacas baby. Disponerlas como base en dos boles amplios.",
        "Distribuir los tomates cherry, el pepino y las láminas de aguacate alrededor.",
        "Colocar el pollo a la plancha en el centro y aderezar con una emulsión suave de aceite de oliva y limón."
      ),
      stepsEn = listOf(
        "Toss chicken strips with fresh lemon juice, oregano, black pepper, and half the olive oil.",
        "Sear chicken on a hot grill pan for 5-6 minutes until tender and caramelized.",
        "Wash and spin-dry baby spinach; arrange as a lush bed across two salad bowls.",
        "Top with halved cherry tomatoes, cucumber slices, and creamy avocado wedges.",
        "Crown with grilled chicken breast and drizzle with olive oil and lemon vinaigrette."
      ),
      tags = listOf("High Protein", "Low Carb", "Keto Friendly", "Lunch", "Quick"),
      imageRes = R.drawable.img_recipe_med_bowl,
      isFeatured = false,
      isPopular = true,
      isNew = true
    ),

    Recipe(
      id = "greek_yogurt_berry_parfait",
      titleEs = "Parfait de Yogur Griego y Frutos Rojos",
      titleEn = "Greek Yogurt & Wild Berry Parfait",
      descriptionEs = "Desayuno o snack saciante con yogur griego natural sin azúcar añadido, arándanos antioxidantes, nueces y semillas de chía.",
      descriptionEn = "Satisfying breakfast or snack made with zero-added-sugar Greek yogurt, antioxidant-rich blueberries, chia seeds, and crunchy walnuts.",
      category = RecipeCategory.BREAKFAST,
      secondaryCategories = listOf(RecipeCategory.SNACKS, RecipeCategory.DESSERTS, RecipeCategory.EASY, RecipeCategory.FAST),
      prepTimeMinutes = 5,
      cookTimeMinutes = 0,
      difficulty = Difficulty.EASY,
      defaultServings = 1,
      nutrition = NutritionInfo(
        calories = 210,
        totalCarbsGrams = 12.0,
        sugarsGrams = 6.0,
        proteinGrams = 18.0,
        fatGrams = 10.0,
        saturatedFatGrams = 2.0,
        fiberGrams = 5.0,
        sodiumMg = 65.0
      ),
      ingredients = listOf(
        Ingredient("Yogur griego natural sin azúcar (0% o 2%)", "Plain unsweetened Greek yogurt", 180.0, "g", GroceryCategory.DAIRY),
        Ingredient("Arándanos azules frescos", "Fresh wild blueberries", 50.0, "g", GroceryCategory.FRUITS),
        Ingredient("Frambuesas o moras", "Fresh raspberries or blackberries", 30.0, "g", GroceryCategory.FRUITS),
        Ingredient("Semillas de chía", "Chia seeds", 1.0, "cucharada", GroceryCategory.GRAINS),
        Ingredient("Nueces picadas crudas", "Raw chopped walnuts", 15.0, "g", GroceryCategory.PANTRY),
        Ingredient("Canela de Ceilán en polvo", "Ceylon cinnamon powder", 0.5, "cucharadita", GroceryCategory.PANTRY)
      ),
      stepsEs = listOf(
        "Colocar la mitad del yogur griego natural en una copa o tazón de cristal.",
        "Añadir una capa de arándanos frescos y la mitad de las semillas de chía.",
        "Cubrir con el resto del yogur griego.",
        "Decorar con las frambuesas, nueces picadas y espolvorear la canela en polvo para mejorar la sensibilidad a la insulina.",
        "Disfrutar de inmediato o refrigerar 15 minutos para que la chía aporte mayor cremosidad."
      ),
      stepsEn = listOf(
        "Spoon half of the unsweetened Greek yogurt into a glass parfait cup.",
        "Layer in half the blueberries and a sprinkle of raw chia seeds.",
        "Top with remaining Greek yogurt.",
        "Garnish with raspberries, crushed walnuts, and a dusting of Ceylon cinnamon.",
        "Serve immediately or chill for 15 minutes to let chia seeds soften nicely."
      ),
      tags = listOf("Breakfast", "No Added Sugar", "Low GI", "Quick 5min", "High Fiber"),
      imageRes = R.drawable.img_recipe_parfait,
      isFeatured = false,
      isPopular = true,
      isNew = false
    ),

    Recipe(
      id = "rustic_lentil_vegetable_stew",
      titleEs = "Guiso Casero de Lentejas y Vegetales",
      titleEn = "Rustic Lentil & Garden Vegetable Stew",
      descriptionEs = "Reconfortante sopa de lentejas pardinas de bajo índice glucémico con espinacas, calabacín, apio y cúrcuma antiinflamatoria.",
      descriptionEn = "Wholesome low-glycemic brown lentil soup enriched with baby spinach, zucchini, celery, and anti-inflammatory turmeric.",
      category = RecipeCategory.SOUPS,
      secondaryCategories = listOf(RecipeCategory.VEGETARIAN, RecipeCategory.VEGAN, RecipeCategory.LUNCH, RecipeCategory.DINNER),
      prepTimeMinutes = 15,
      cookTimeMinutes = 30,
      difficulty = Difficulty.EASY,
      defaultServings = 4,
      nutrition = NutritionInfo(
        calories = 240,
        totalCarbsGrams = 29.0,
        sugarsGrams = 3.8,
        proteinGrams = 13.0,
        fatGrams = 4.5,
        saturatedFatGrams = 0.6,
        fiberGrams = 9.5,
        sodiumMg = 290.0
      ),
      ingredients = listOf(
        Ingredient("Lentejas pardinas secas", "Brown lentils (rinsed)", 200.0, "g", GroceryCategory.GRAINS),
        Ingredient("Calabacín en cubos pequeños", "Diced green zucchini", 1.0, "unidad", GroceryCategory.VEGETABLES),
        Ingredient("Zanahoria mediana picada", "Finely diced carrot", 1.0, "unidad", GroceryCategory.VEGETABLES),
        Ingredient("Tallos de apio en rodajas", "Sliced celery stalks", 2.0, "tallos", GroceryCategory.VEGETABLES),
        Ingredient("Hojas de espinaca fresca", "Fresh spinach leaves", 100.0, "g", GroceryCategory.VEGETABLES),
        Ingredient("Caldo de verduras bajo en sodio", "Low-sodium vegetable broth", 800.0, "ml", GroceryCategory.PANTRY),
        Ingredient("Cúrcuma y laurel", "Ground turmeric & bay leaf", 1.0, "cucharadita", GroceryCategory.PANTRY),
        Ingredient("Aceite de oliva virgen extra", "Extra virgin olive oil", 1.0, "cucharada", GroceryCategory.PANTRY)
      ),
      stepsEs = listOf(
        "En una cazuela honda, calentar el aceite de oliva y sofreír suavemente el apio y la zanahoria durante 5 minutos.",
        "Agregar el calabacín, la cúrcuma y una hoja de laurel, removiendo durante 2 minutos.",
        "Incorporar las lentejas enjuagadas y cubrir con el caldo de verduras bajo en sodio.",
        "Llevar a ebullición, bajar el fuego y cocinar a fuego lento semi-tapado durante 25 minutos.",
        "Apagar el fuego, añadir las espinacas frescas para que se cocinen con el vapor residual y servir caliente."
      ),
      stepsEn = listOf(
        "Warm olive oil in a deep soup pot and gently sauté celery and carrot for 5 minutes.",
        "Add diced zucchini, ground turmeric, and bay leaf, stirring for 2 minutes to release aromatics.",
        "Add rinsed lentils and pour in the low-sodium vegetable broth.",
        "Bring to a gentle boil, reduce heat, cover partially, and simmer for 25 minutes.",
        "Fold in the fresh spinach off heat so it wilts gently in the fragrant warm stew."
      ),
      tags = listOf("High Fiber", "Vegan", "Comfort Food", "Low Glycemic", "Heart Healthy"),
      imageRes = R.drawable.img_recipe_soup,
      isFeatured = false,
      isPopular = false,
      isNew = true
    ),

    Recipe(
      id = "spinach_feta_omelet",
      titleEs = "Omelette de Espinacas, Champiñones y Feta",
      titleEn = "Spinach, Mushroom & Feta Omelet",
      descriptionEs = "Proteínas limpias y saciantes sin impacto en glucemia: huevos camperos, champiñones salteados y queso feta desmenuzado.",
      descriptionEn = "Zero glycemic impact high-protein breakfast with farm eggs, sautéed mushrooms, wilted spinach, and crumbled feta cheese.",
      category = RecipeCategory.BREAKFAST,
      secondaryCategories = listOf(RecipeCategory.EASY, RecipeCategory.FAST, RecipeCategory.LOW_CARB, RecipeCategory.VEGETARIAN),
      prepTimeMinutes = 5,
      cookTimeMinutes = 8,
      difficulty = Difficulty.EASY,
      defaultServings = 1,
      nutrition = NutritionInfo(
        calories = 265,
        totalCarbsGrams = 4.0,
        sugarsGrams = 1.2,
        proteinGrams = 19.0,
        fatGrams = 19.0,
        saturatedFatGrams = 5.0,
        fiberGrams = 1.8,
        sodiumMg = 310.0
      ),
      ingredients = listOf(
        Ingredient("Huevos grandes", "Large fresh eggs", 2.0, "unidades", GroceryCategory.MEAT_PROTEIN),
        Ingredient("Champiñones laminados", "Sliced button mushrooms", 60.0, "g", GroceryCategory.VEGETABLES),
        Ingredient("Espinacas frescas", "Fresh spinach leaves", 50.0, "g", GroceryCategory.VEGETABLES),
        Ingredient("Queso feta desmenuzado", "Crumbled feta cheese", 25.0, "g", GroceryCategory.DAIRY),
        Ingredient("Aceite de oliva virgen extra", "Extra virgin olive oil", 1.0, "cucharadita", GroceryCategory.PANTRY)
      ),
      stepsEs = listOf(
        "Batir los dos huevos en un cuenco con una pizca de sal marina y pimienta molida.",
        "Saltear los champiñones en una sartén antiadherente con unas gotas de aceite durante 3 minutos.",
        "Añadir las espinacas hasta que se reduzcan ligeramente.",
        "Verter los huevos batidos, cocinar a fuego medio hasta que los bordes cuajen.",
        "Espolvorear el queso feta, doblar a la mitad y servir tibio."
      ),
      stepsEn = listOf(
        "Whisk eggs in a bowl with a pinch of sea salt and freshly ground pepper.",
        "Sauté mushrooms in a non-stick pan with olive oil for 3 minutes until tender.",
        "Add spinach leaves and stir until just wilted.",
        "Pour in beaten eggs, cooking over medium heat until edges set cleanly.",
        "Scatter crumbled feta across one side, fold over, and transfer to plate."
      ),
      tags = listOf("Keto", "Ultra Low Carb", "Quick Breakfast", "Vegetarian"),
      imageRes = R.drawable.img_recipe_med_bowl,
      isFeatured = false,
      isPopular = true,
      isNew = false
    ),

    Recipe(
      id = "turkey_lettuce_tacos",
      titleEs = "Tacos de Lechuga con Pavo Especiado",
      titleEn = "Spiced Turkey Lettuce Wrap Tacos",
      descriptionEs = "Crujientes hojas de lechuga romana rellenas de carne magra de pavo sazonada con comino, pimentón y pico de gallo casero.",
      descriptionEn = "Crispy romaine boats loaded with lean ground turkey seasoned with cumin, smoked paprika, and fresh pico de gallo.",
      category = RecipeCategory.DINNER,
      secondaryCategories = listOf(RecipeCategory.QUICK_MEALS, RecipeCategory.LOW_CARB, RecipeCategory.GLUTEN_FREE),
      prepTimeMinutes = 10,
      cookTimeMinutes = 10,
      difficulty = Difficulty.EASY,
      defaultServings = 2,
      nutrition = NutritionInfo(
        calories = 290,
        totalCarbsGrams = 8.0,
        sugarsGrams = 2.8,
        proteinGrams = 32.0,
        fatGrams = 14.0,
        saturatedFatGrams = 2.5,
        fiberGrams = 3.2,
        sodiumMg = 340.0
      ),
      ingredients = listOf(
        Ingredient("Carne picada magra de pavo", "Lean ground turkey breast", 250.0, "g", GroceryCategory.MEAT_PROTEIN),
        Ingredient("Hojas de lechuga romana entera", "Crisp romaine lettuce leaves", 6.0, "hojas", GroceryCategory.VEGETABLES),
        Ingredient("Tomate maduro en cubitos", "Diced plum tomato", 1.0, "unidad", GroceryCategory.VEGETABLES),
        Ingredient("Cebolla morada picada", "Finely diced red onion", 0.5, "unidad", GroceryCategory.VEGETABLES),
        Ingredient("Cilantro fresco y lima", "Fresh cilantro & lime wedges", 1.0, "manojo", GroceryCategory.VEGETABLES),
        Ingredient("Comino y pimentón dulce", "Ground cumin & smoked paprika", 1.0, "cucharadita", GroceryCategory.PANTRY)
      ),
      stepsEs = listOf(
        "En una sartén dorar el pavo con comino, pimentón y sal hasta que esté bien cocido.",
        "Preparar un pico de gallo mezclando el tomate, cebolla morada, cilantro y zumo de lima.",
        "Lavar y secar cuidadosamente las hojas de lechuga para que actúen como barquitas.",
        "Rellenar cada hoja con el pavo caliente y coronar con el pico de gallo fresco."
      ),
      stepsEn = listOf(
        "Brown ground turkey in a skillet with cumin, smoked paprika, and a touch of salt.",
        "Toss diced tomatoes, red onion, chopped cilantro, and lime juice for a vibrant salsa.",
        "Rinse and pat dry whole crisp romaine leaves.",
        "Spoon seasoned turkey into the lettuce wraps and top with chilled pico de gallo."
      ),
      tags = listOf("Low Carb", "Gluten Free", "Dinner", "High Protein"),
      imageRes = R.drawable.img_recipe_salmon,
      isFeatured = false,
      isPopular = false,
      isNew = true
    ),

    Recipe(
      id = "zucchini_walnut_pesto_zoodles",
      titleEs = "Zoodles de Calabacín con Pesto de Nueces",
      titleEn = "Zucchini Zoodles with Walnut Basil Pesto",
      descriptionEs = "Espaguetis de calabacín 100% vegetales con pesto casero de albahaca fresca, nueces tostadas y parmesano curado.",
      descriptionEn = "100% vegetable spiralized zucchini noodles tossed in aromatic homemade basil-walnut pesto and grated parmesan.",
      category = RecipeCategory.DINNER,
      secondaryCategories = listOf(RecipeCategory.VEGETARIAN, RecipeCategory.LOW_CARB, RecipeCategory.GLUTEN_FREE),
      prepTimeMinutes = 10,
      cookTimeMinutes = 4,
      difficulty = Difficulty.EASY,
      defaultServings = 2,
      nutrition = NutritionInfo(
        calories = 230,
        totalCarbsGrams = 9.0,
        sugarsGrams = 4.0,
        proteinGrams = 7.0,
        fatGrams = 19.0,
        saturatedFatGrams = 3.2,
        fiberGrams = 3.5,
        sodiumMg = 210.0
      ),
      ingredients = listOf(
        Ingredient("Calabacines medianos espiralizados", "Medium fresh green zucchinis", 2.0, "unidades", GroceryCategory.VEGETABLES),
        Ingredient("Hojas frescas de albahaca", "Fresh basil leaves", 40.0, "g", GroceryCategory.VEGETABLES),
        Ingredient("Nueces del nogal", "Shelled walnuts", 30.0, "g", GroceryCategory.PANTRY),
        Ingredient("Queso parmesano rallado", "Grated parmesan cheese", 20.0, "g", GroceryCategory.DAIRY),
        Ingredient("Aceite de oliva virgen extra", "Extra virgin olive oil", 2.0, "cucharadas", GroceryCategory.PANTRY),
        Ingredient("Ajo", "Garlic clove", 1.0, "diente", GroceryCategory.VEGETABLES)
      ),
      stepsEs = listOf(
        "Espiralizar los calabacines para obtener zoodles largos.",
        "Triturar en procesadora la albahaca, nueces, ajo, parmesano y aceite de oliva hasta emulsionar.",
        "Saltear los zoodles en sartén amplia durante solo 2 minutos a fuego vivo para que no suelten agua.",
        "Mezclar con el pesto casero fuera del fuego y servir al momento."
      ),
      stepsEn = listOf(
        "Spiralize zucchini into long tender noodle strands.",
        "Pulse basil, walnuts, garlic, parmesan, and olive oil into a rich aromatic pesto.",
        "Flash-sauté zoodles in a hot pan for just 2 minutes so they stay pleasantly al dente.",
        "Toss off the heat with the fresh pesto and serve immediately."
      ),
      tags = listOf("Low Carb", "Vegetarian", "Gluten Free", "Quick"),
      imageRes = R.drawable.img_recipe_med_bowl,
      isFeatured = false,
      isPopular = true,
      isNew = false
    ),

    Recipe(
      id = "chia_almond_vanilla_pudding",
      titleEs = "Pudín de Chía con Leche de Almendras y Vainilla",
      titleEn = "Vanilla Chia Seed Almond Pudding",
      descriptionEs = "Concentrado en fibra soluble que estabiliza la absorción de glucosa. Cremoso, refrescante y sin azúcares agregados.",
      descriptionEn = "Soluble-fiber powerhouse that blunts post-meal glucose spikes. Creamy, refreshing, and naturally sweetened with vanilla.",
      category = RecipeCategory.DESSERTS,
      secondaryCategories = listOf(RecipeCategory.BREAKFAST, RecipeCategory.SNACKS, RecipeCategory.VEGAN, RecipeCategory.LACTOSE_FREE),
      prepTimeMinutes = 5,
      cookTimeMinutes = 0,
      difficulty = Difficulty.EASY,
      defaultServings = 2,
      nutrition = NutritionInfo(
        calories = 175,
        totalCarbsGrams = 11.0,
        sugarsGrams = 1.0,
        proteinGrams = 6.0,
        fatGrams = 11.0,
        saturatedFatGrams = 1.2,
        fiberGrams = 8.5,
        sodiumMg = 80.0
      ),
      ingredients = listOf(
        Ingredient("Semillas de chía negras", "Black chia seeds", 40.0, "g", GroceryCategory.GRAINS),
        Ingredient("Bebida de almendras sin azúcar", "Unsweetened almond milk", 300.0, "ml", GroceryCategory.DAIRY),
        Ingredient("Extracto natural de vainilla", "Pure vanilla extract", 1.0, "cucharadita", GroceryCategory.PANTRY),
        Ingredient("Frambuesas frescas para decorar", "Fresh raspberries for topping", 30.0, "g", GroceryCategory.FRUITS)
      ),
      stepsEs = listOf(
        "Mezclar las semillas de chía, bebida de almendras y extracto de vainilla en un frasco de vidrio.",
        "Remover vigorosamente con un tenedor durante 1 minuto para evitar que se formen grumos.",
        "Dejar reposar 10 minutos y volver a remover.",
        "Refrigerar durante al menos 2 horas (o toda la noche). Servir con frambuesas frescas."
      ),
      stepsEn = listOf(
        "Whisk chia seeds, unsweetened almond milk, and vanilla extract together in a jar.",
        "Stir briskly for 1 minute to prevent seeds from clumping.",
        "Let rest 10 minutes, stir once more, then cover and chill for at least 2 hours or overnight.",
        "Top with fresh raspberries before serving cold."
      ),
      tags = listOf("High Fiber", "Vegan", "Dessert", "Gluten Free", "No Added Sugar"),
      imageRes = R.drawable.img_recipe_parfait,
      isFeatured = false,
      isPopular = false,
      isNew = true
    )
  )
}
