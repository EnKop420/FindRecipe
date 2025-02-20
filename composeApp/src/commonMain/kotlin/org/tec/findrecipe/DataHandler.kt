package org.tec.findrecipe

import app.cash.sqldelight.db.SqlDriver
import org.tec.findrecipe.FavoriteTableQueries

class DataHandler(database: Database) {
    val queries: FavoriteTableQueries = database.favoriteTableQueries // This is the generated query interface

    suspend fun GetFavoriteRecipes(): List<RecipeClass> {
        // Execute the query and map the results to RecipeClass
        return queries.selectAll().executeAsList().map { row ->
            RecipeClass(
                Id = row.Id,                // Assuming your table has id, name, and ingredients
                Title = row.Title.toString(),
                Instruction = row.Instruction.toString(),
                ImageUrl = row.ImageUrl.toString(),
                IngredientsAndMeasurements = row.Ingredients.toString()
            )
        }
    }

    suspend fun AddFavoriteRecipeToDatabase(recipe: RecipeClass): Boolean {
        return try {
            // Use the insert query to add the recipe to the table
            queries.addRecipe(
                recipe.Title,          // Title of the recipe
                recipe.Instruction,    // Instructions
                recipe.ImageUrl,       // Image URL
                recipe.IngredientsAndMeasurements     // Ingredients
            )
            true
        } catch (e: Exception) {
            // Handle error (e.g., show error message)
            e.printStackTrace()
            false
        }
    }

    suspend fun RemoveFavoriteRecipeFromDatabase(id: Long): Boolean {
        return try {
            // Execute the delete query using the provided ID
            queries.removeRecipe(id)
            true
        } catch (e: Exception) {
            // Handle error (e.g., show error message)
            e.printStackTrace()
            false
        }
    }
}