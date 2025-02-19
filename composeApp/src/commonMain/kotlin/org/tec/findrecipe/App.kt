package org.tec.findrecipe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chaintech.network.cmpshakedetection.rememberShakeDetector
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import findrecipe.composeapp.generated.resources.Res
import findrecipe.composeapp.generated.resources.cheeseburger
import findrecipe.composeapp.generated.resources.compose_multiplatform
import findrecipe.composeapp.generated.resources.heart
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tec.findrecipe.networking.createHttpClient

@Composable
@Preview
fun App(engine: HttpClientEngine) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        val httpClient = remember { createHttpClient(engine) }
        val apiRepository = remember { ApiHandler(httpClient) }
        val shakeDetector = rememberShakeDetector()
        var refreshing by remember { mutableStateOf(false) }
        val defaultRecipe = RecipeClass(Title = "", Instruction = "", ImageUrl = "", IngredientsAndMeasurements = emptyList())
        val scaffoldState = rememberScaffoldState()

        val currentRecipe = remember { mutableStateOf(defaultRecipe) } // Initializing as null

        //Start detecting shakes in a LaunchedEffect block
        LaunchedEffect(Unit) {
            shakeDetector.start()
            kotlinx.coroutines.runBlocking {
                // Call the suspend function to get the recipe and update the state
                val recipe = apiRepository.GetRecipeFromApi() // Suspend function call
                val formattedRecipe = apiRepository.FormatResponse(recipe) // Process the result
                currentRecipe.value = formattedRecipe // Update the state using .value
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                shakeDetector.stop()
            }
        }

        shakeDetector.onShake {
            if (!refreshing)
            {
                refreshing = true
                // Perform your refresh action here
                kotlinx.coroutines.runBlocking {
                    // Call the suspend function to get the recipe and update the state
                    val recipe = apiRepository.GetRecipeFromApi() // Suspend function call
                    val formattedRecipe = apiRepository.FormatResponse(recipe) // Process the result
                    currentRecipe.value = formattedRecipe // Update the state using .value
                }
                refreshing = false
            }
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(currentRecipe.value.Title)
            Button(onClick = { showContent = !showContent }) {

            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
            Image(
                painterResource(Res.drawable.cheeseburger),
                contentDescription = "Example image"
            )
            Image(
                painterResource(Res.drawable.heart),
                contentDescription = "Example image"
            )
        }
    }
}