package org.tec.findrecipe

import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiHandler(private val client: HttpClient) {

    suspend fun GetRecipeFromApi(): String {
        try {
            val result: String = client.get("https://www.themealdb.com/api/json/v1/1/random.php").bodyAsText()
            return result;
        } catch (e: SocketTimeoutException){
            // Handle the timeout exception, e.g., show a message to the user
            println("Request timed out. Please try again.")
            return ""
        }
    }

    fun FormatResponse(responseJson: String): RecipeClass {
        val jsonObject = Json.parseToJsonElement(responseJson).jsonObject
        val mealsArray = jsonObject["meals"]?.jsonArray

        // If meals is not null, extract the first meal
        val meal = mealsArray?.get(0)?.jsonObject

        // Extract the Title and Instruction
        val title = meal?.get("strMeal")?.jsonPrimitive?.content ?: "Unknown Title"
        val instruction = meal?.get("strInstructions")?.jsonPrimitive?.content ?: "No Instructions"
        val imageUrl = meal?.get("strMealThumb")?.jsonPrimitive?.content ?: ""
        // Extract ingredients and measurements
        val ingredientsAndMeasurements = mutableListOf<String>()
        for (i in 1..20) { // assuming up to 20 ingredients
            val ingredient = meal?.get("strIngredient$i")?.jsonPrimitive?.content
            val measure = meal?.get("strMeasure$i")?.jsonPrimitive?.content
            if (!ingredient.isNullOrBlank() && !measure.isNullOrBlank()) {
                ingredientsAndMeasurements.add("$ingredient:    $measure")
            }
        }

        // Now you can create your RecipeClass manually
        val recipe = RecipeClass(
            Id = 0, // Dummy Id won't be used but is required to instantiate a RecipeClass
            Title = title,
            Instruction = instruction,
            ImageUrl = imageUrl,
            IngredientsAndMeasurements = ingredientsAndMeasurements.joinToString("\n")
        )
        return recipe
    }

    suspend fun getRecipe() : RecipeClass{
        return FormatResponse(GetRecipeFromApi())
    }
}