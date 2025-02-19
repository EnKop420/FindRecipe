package org.tec.findrecipe

import MinimalDropdownMenu
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chaintech.network.cmpshakedetection.rememberShakeDetector
import org.jetbrains.compose.ui.tooling.preview.Preview
import findrecipe.composeapp.generated.resources.Res
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.*

import org.tec.findrecipe.networking.createHttpClient
import org.tec.findrecipe.views.FavoriteView
import org.tec.findrecipe.views.FeedView
import org.tec.findrecipe.views.SettingsView

@Composable
@Preview
fun App(engine: HttpClientEngine) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var selectedView by remember { mutableStateOf<ViewType>(ViewType.Feed) }

        val httpClient = remember { createHttpClient(engine) }
        val apiRepository = remember { ApiHandler(httpClient) }
        val shakeDetector = rememberShakeDetector()
        var refreshing by remember { mutableStateOf(false) }
        val defaultRecipe = RecipeClass(Title = "", Instruction = "", ImageUrl = "", IngredientsAndMeasurements = emptyList())
        val scaffoldState = rememberScaffoldState()
        val currentRecipe = remember { mutableStateOf(defaultRecipe) }

        // Start detecting shakes
        LaunchedEffect(Unit) {
            shakeDetector.start()
            kotlinx.coroutines.runBlocking {
                val recipe = apiRepository.GetRecipeFromApi()
                val formattedRecipe = apiRepository.FormatResponse(recipe)
                currentRecipe.value = formattedRecipe
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                shakeDetector.stop()
            }
        }

        shakeDetector.onShake {
            println("Shake detected")
            refreshing = true
            CoroutineScope(Dispatchers.IO).launch {
                val recipe = apiRepository.GetRecipeFromApi()
                val formattedRecipe = apiRepository.FormatResponse(recipe)
                currentRecipe.value = formattedRecipe
            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                MinimalDropdownMenu { selectedView = it }
            }

            when (selectedView) {
                ViewType.Feed -> FeedView()
                ViewType.Favorites -> FavoriteView()
                ViewType.Settings -> SettingsView()
            }

            Text(currentRecipe.value.Title)
            Button(onClick = { showContent = !showContent }) {
                Text("Toggle Content")
            }
        }
    }
}

enum class ViewType {
    Feed, Favorites, Settings
}
