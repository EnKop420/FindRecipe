package org.tec.findrecipe

import MinimalDropdownMenu
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.tec.findrecipe.ApiEngineAndDatabaseInstances.createHttpClient
import org.tec.findrecipe.views.FavoriteView
import org.tec.findrecipe.views.FeedView

import org.tec.findrecipe.views.RecipeView
import org.tec.findrecipe.views.SettingsView
import kotlin.coroutines.CoroutineContext

@Composable
@Preview
fun App(engine: HttpClientEngine, database: Database) {
    MaterialTheme {
        val client = createHttpClient(engine)
        val apiHandler = ApiHandler(client)
        val dataHandler = DataHandler(database)
        var selectedView by remember { mutableStateOf<ViewType>(ViewType.Feed) }

        Surface {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.End) {
                    MinimalDropdownMenu { selectedView = it }
                }

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                ){
//                    val loremIpsum = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
//                    val dummyIngredients = """
//    Flour: 2 cups
//    Sugar: 1 cup
//    Eggs: 3
//    Milk: 1 cup
//    Butter: 1/2 cup
//    Vanilla extract: 1 tsp
//    Baking powder: 2 tsp
//    Salt: 1/2 tsp
//    Cocoa powder: 1/4 cup
//    Chocolate chips: 1 cup
//""".trimIndent()

//                    RecipeView(RecipeClass(1, "Title", loremIpsum, "https://www.themealdb.com/images/media/meals/xusqvw1511638311.jpg", dummyIngredients))
                    when (selectedView) {
                        ViewType.Feed -> FeedView(apiHandler)
                        ViewType.Favorites -> FavoriteView()
                        ViewType.Settings -> SettingsView()
                    }
                }
            }
        }
    }
}

enum class ViewType {
    Feed, Favorites, Settings
}
