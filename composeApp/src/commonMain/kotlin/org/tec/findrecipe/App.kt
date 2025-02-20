package org.tec.findrecipe

// Importing necessary components for the UI
import MinimalDropdownMenu
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.ktor.client.engine.HttpClientEngine
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.tec.findrecipe.ApiEngineAndDatabaseInstances.createHttpClient
import org.tec.findrecipe.Handlers.ApiHandler
import org.tec.findrecipe.Handlers.DataHandler
import org.tec.findrecipe.views.FavoriteView
import org.tec.findrecipe.views.FeedView
import org.tec.findrecipe.views.RecipeView
import org.tec.findrecipe.views.SettingsView

// Main composable function for the app UI
@Composable
@Preview // This annotation makes it easy to preview the UI in Android Studio.
// The App will be called with a database and a HTTP client engine. This will be different for each platform as seen in either iosMain's or androidMain's mainActivity.kt
fun App(engine: HttpClientEngine, database: Database) {
    MaterialTheme { // A Material Design theme for the app
        // Create HTTP client using provided engine and API handler
        val client = createHttpClient(engine) // Using a custom function to create the HTTP client
        val apiHandler = ApiHandler(client) // Provides methods to interact with the API
        val dataHandler = DataHandler(database) // Handles database interactions

        // State variables to manage the selected view and the currently selected recipe
        var selectedView by remember { mutableStateOf(ViewType.Feed) } // Tracks which view is selected (Feed, Favorites, etc.)
        var selectedRecipe by remember { mutableStateOf<RecipeClass?>(null) } // Holds the current selected recipe (nullable)

        Surface { // A surface that applies a Material Design background to its child components
            Box(modifier = Modifier.fillMaxSize()) { // A Box layout that takes up the entire screen

                // Top bar with dropdown menu that allows the user to switch between views
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Ensures the row takes up the full width of the screen
                        .wrapContentHeight() // Height wraps the content inside the row
                        .padding(10.dp) // Padding around the row
                        .zIndex(1f), // Ensures that the dropdown stays above other UI components
                    horizontalArrangement = Arrangement.End // Aligns the content (dropdown) to the right
                ) {
                    // MinimalDropdownMenu is a custom composable that allows the user to select a view
                    MinimalDropdownMenu { selectedView = it } // Updates the selectedView when a new option is chosen
                }

                // Main content area that adjusts for the top bar (so it doesn't overlap)
                Box(
                    modifier = Modifier
                        .fillMaxSize() // Takes up the full remaining size of the screen
                        .padding(top = 60.dp) // Adds padding at the top to avoid overlapping with the dropdown menu
                ) {
                    // Displays different views based on the selectedView
                    when (selectedView) {
                        ViewType.Feed -> FeedView(apiHandler, dataHandler) { recipe, _ ->
                            selectedRecipe = recipe // Sets the selected recipe
                            selectedView = ViewType.Recipe // Switches to the Recipe view
                        }
                        ViewType.Favorites -> FavoriteView(
                            dataHandler = dataHandler,
                            onRecipeClick = { recipe ->
                                selectedRecipe = recipe // Sets the selected recipe
                                selectedView = ViewType.Recipe // Switches to the Recipe view
                            }
                        )
                        ViewType.Settings -> SettingsView() // Shows the Settings view
                        ViewType.Recipe -> selectedRecipe?.let { RecipeView(it, dataHandler) } // Displays RecipeView for the selected recipe
                    }
                }
            }
        }
    }
}

// Enum to define the different views in the app (Feed, Favorites, Settings, Recipe)
enum class ViewType {
    Feed, Favorites, Settings, Recipe
}
