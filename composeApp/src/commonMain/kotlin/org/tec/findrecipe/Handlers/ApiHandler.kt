package org.tec.findrecipe.Handlers

import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.tec.findrecipe.RecipeClass

/**
 * ApiHandler is responsible for making HTTP requests and parsing responses
 * from the MealDB API. This class uses Ktor's HttpClient for network requests.
 */
class ApiHandler(private val client: HttpClient) {

    /**
     * Fetches a random recipe from the MealDB API.
     * Uses Kotlin's `suspend` function, meaning it must be called from a coroutine
     * (similar to `async`/`await` in C#).
     */
    suspend fun GetRecipeFromApi(): String {
        try {
            // Performs an HTTP GET request and returns the response as text.
            val result: String = client.get("https://www.themealdb.com/api/json/v1/1/random.php").bodyAsText()
            return result
        } catch (e: SocketTimeoutException) {
            // Catches timeout exceptions (similar to try-catch in C#)
            println("Request timed out. Please try again.")
            return ""
        }
    }

    /**
     * Parses a JSON response string and converts it into a RecipeClass object.
     * This function manually extracts and processes the JSON fields.
     */
    suspend fun FormatResponse(responseJson: String): RecipeClass {
        // Parse JSON into a Kotlin JsonElement object
        val jsonObject = Json.parseToJsonElement(responseJson).jsonObject
        val mealsArray = jsonObject["meals"]?.jsonArray

        // Extract the first meal object if it exists
        val meal = mealsArray?.get(0)?.jsonObject

        // Extract basic recipe information (Title, Instructions, and Image URL)
        val title = meal?.get("strMeal")?.jsonPrimitive?.content ?: "Unknown Title"
        val instruction = meal?.get("strInstructions")?.jsonPrimitive?.content ?: "No Instructions"
        val imageUrl = meal?.get("strMealThumb")?.jsonPrimitive?.content ?: ""

        // Extract ingredients and measurements (up to 20 ingredients)
        val ingredientsAndMeasurements = mutableListOf<String>()
        for (i in 1..20) { // Ranges in Kotlin use ".." (similar to `for(int i = 1; i <= 20; i++)` in C#)
            val ingredient = meal?.get("strIngredient$i")?.jsonPrimitive?.content
            val measure = meal?.get("strMeasure$i")?.jsonPrimitive?.content
            if (!ingredient.isNullOrBlank() && !measure.isNullOrBlank()) {
                ingredientsAndMeasurements.add("$ingredient:    $measure")
            }
        }

        // Construct and return a RecipeClass instance
        return RecipeClass(
            Id = 0, // Dummy Id (not used, but required in RecipeClass)
            Title = title,
            Instruction = instruction,
            ImageUrl = imageUrl,
            IngredientsAndMeasurements = ingredientsAndMeasurements.joinToString("\n") // Converts list to a formatted string
        )
    }

    /**
     * A coroutine function that fetches a recipe from the API and formats it.
     * This is the main function to call when retrieving a recipe.
     */
    suspend fun getRecipe(): RecipeClass {
        val response = GetRecipeFromApi()
        if (response == ""){
            throw Exception("No response from API")
        }
        else{
            return FormatResponse(response)
        }
    }
}
