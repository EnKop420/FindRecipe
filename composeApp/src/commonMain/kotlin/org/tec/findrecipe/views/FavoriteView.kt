package org.tec.findrecipe.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tec.findrecipe.RecipeClass
import org.tec.findrecipe.components.FavoriteRecipeItem
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tec.findrecipe.Handlers.DataHandler
import org.tec.findrecipe.components.DialogBox


@Composable
fun FavoriteView(dataHandler: DataHandler,
                 onRecipeClick: (RecipeClass) -> Unit) {
    // Remembering the list of favorite recipes in the UI state.
    // Remember is a special function in Jetpack Compose used to persist state across recompositions.
    val listOfRecipeClass = remember { mutableStateOf<List<RecipeClass>>(emptyList()) }

    // Remembering the coroutine scope, which will allow us to launch coroutines within this composable.
    val coroutineScope = rememberCoroutineScope()

    // Boolean state to control the visibility of the dialog.
    var showDialog by remember { mutableStateOf(false) }

    // The LaunchedEffect is used to trigger side-effects (like loading data) when the Composable first enters the composition.
    LaunchedEffect(Unit) {
        // Launching a coroutine in the IO dispatcher to fetch favorite recipes from the database.
        val result = withContext(Dispatchers.IO) { dataHandler.GetFavoriteRecipes() }

        // Updating the UI state with the result after fetching from the database.
        listOfRecipeClass.value = result
    }

    // Function to remove a recipe from the list of favorites.
    // It performs a coroutine-based removal, ensuring the database interaction doesn't block the main thread.
    fun removeFavorite(recipeId: Long?) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                // Removing the recipe from the database using the dataHandler.
                dataHandler.RemoveFavoriteRecipeFromDatabase(recipeId)

                // Updating the UI state by filtering out the removed recipe.
                listOfRecipeClass.value = listOfRecipeClass.value.filter { it.Id != recipeId }
            }
        }
    }

    // The main layout of the FavoriteView composable.
    Column(
        modifier = Modifier
            .fillMaxSize() // Takes the full available size
            .padding(16.dp) // Adds padding around the content
    ) {
        // Title of the screen displaying "Favorite Recipes"
        Text(
            text = "Favorite Recipes",
            fontSize = 24.sp, // Font size
            fontWeight = FontWeight.Bold, // Bold text style
            modifier = Modifier.padding(bottom = 16.dp) // Adds space below the title
        )

        // LazyColumn is used for displaying a list of items efficiently in Compose.
        // It only renders items that are currently visible on the screen, improving performance.
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp)  // Space between each item in the list
        ) {
            // Iterating over the list of recipes to display each one in the list.
            items(listOfRecipeClass.value) { recipe ->
                // For each recipe, display a `FavoriteRecipeItem`
                FavoriteRecipeItem(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe) }, // Trigger the click action when the item is clicked
                    onRemove = {
                        // Remove the recipe from the favorites and show the dialog
                        removeFavorite(recipe.Id)
                        showDialog = true
                    }
                )
            }
        }
    }

    // DialogBox composable that appears when a recipe is removed from favorites.
    // This component displays a simple message and provides an "OK" button to close the dialog.
    DialogBox(
        showDialog = showDialog,
        title = "Removed Favorite", // Title of the dialog
        message = "The recipe has been removed from your favorites", // Message content
        onDismiss = { showDialog = false } // Close the dialog when the user clicks "OK"
    )
}
