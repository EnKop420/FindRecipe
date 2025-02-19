package org.tec.findrecipe.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import chaintech.network.cmpshakedetection.rememberShakeDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.tec.findrecipe.ApiHandler
import org.tec.findrecipe.RecipeClass

@Composable
fun FeedView(apiHandler: ApiHandler){
    val snackbarHostState = remember { SnackbarHostState() }
    val defaultRecipe = RecipeClass("", "", "", emptyList())
    val shakeDetector = rememberShakeDetector()
    val currentRecipe = remember { mutableStateOf(defaultRecipe) }
    //region Shake Detection Functions
    LaunchedEffect(Unit) {
        shakeDetector.start()
        // Does the same as on launch function
        CoroutineScope(Dispatchers.IO).launch {
            val recipe = apiHandler.GetRecipeFromApi()
            val formattedRecipe = apiHandler.FormatResponse(recipe)
            withContext(Dispatchers.Main) {
                currentRecipe.value = formattedRecipe
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            shakeDetector.stop()
        }
    }

    val mutex = Mutex() // Ensures only one coroutine runs at a time
    shakeDetector.onShake {
        // Does the same as on launch function
        CoroutineScope(Dispatchers.IO).launch {
            if (mutex.isLocked) return@launch

            mutex.withLock {
                snackbarHostState.showSnackbar("Trying to find a new recipe....")
                val recipe = apiHandler.GetRecipeFromApi()
                if (recipe != "") {
                    val formattedRecipe = apiHandler.FormatResponse(recipe)
                    // Updates the UI with the recipe safely
                    withContext(Dispatchers.Main) {
                        currentRecipe.value = formattedRecipe
                    }
                }
            }
        }
    }
    //endregion

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Green) // Set color to check
        ) {
            Text("Feed Content", color = Color.White)
        }
    }
}