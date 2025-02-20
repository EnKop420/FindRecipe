package org.tec.findrecipe

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
import org.tec.findrecipe.networking.createHttpClient
import org.tec.findrecipe.views.FavoriteView
import org.tec.findrecipe.views.FeedView
import org.tec.findrecipe.views.RecipeView
import org.tec.findrecipe.views.SettingsView

@Composable
@Preview
fun App(engine: HttpClientEngine) {
    MaterialTheme {
        val client = createHttpClient(engine)
        val apiHandler = ApiHandler(client)

        var selectedView by remember { mutableStateOf<ViewType>(ViewType.Feed) }
        var selectedRecipe by remember { mutableStateOf<RecipeClass?>(null)}
        var favoritesList by remember { mutableStateOf<List<RecipeClass>>(emptyList()) }

        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top bar with dropdown menu aligned to the right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp)
                        .zIndex(1f), // Ensures the menu stays above other content
                    horizontalArrangement = Arrangement.End // Aligns to the right
                ) {
                    MinimalDropdownMenu { selectedView = it }
                }

                // Main content with top padding to avoid overlap
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp) // Adjust padding to avoid overlapping dropdown
                ) {
                    when (selectedView) {
                        ViewType.Feed -> FeedView(apiHandler) { recipe ->
                            selectedRecipe = recipe
                            selectedView = ViewType.Recipe
                        }
                        ViewType.Favorites -> FavoriteView(favoritesList) { recipe ->
                            selectedRecipe = recipe
                            selectedView = ViewType.Recipe
                        }
                        ViewType.Settings -> SettingsView()
                        ViewType.Recipe -> selectedRecipe?.let { RecipeView(it) }
                    }
                }
            }
        }
    }
}

enum class ViewType {
    Feed, Favorites, Settings, Recipe
}
