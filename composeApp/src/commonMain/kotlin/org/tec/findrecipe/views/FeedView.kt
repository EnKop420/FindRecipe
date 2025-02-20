package org.tec.findrecipe.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chaintech.network.cmpshakedetection.rememberShakeDetector
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material.Button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tec.findrecipe.ApiHandler
import org.tec.findrecipe.RecipeClass


@Composable
fun FeedView(apiHandler: ApiHandler, onRecipeClick: (RecipeClass) -> Unit){
    val snackbarHostState = remember { SnackbarHostState() }
    val defaultRecipe = RecipeClass(null, "", "", "", "") // Empty instance of Recipe Class to set state of.
    val shakeDetector = rememberShakeDetector()
    val currentRecipe = remember { mutableStateOf(defaultRecipe) }
    //region Shake Detection Functions
    LaunchedEffect(Unit) {
        shakeDetector.start()
        // Does the same as on launch function
        CoroutineScope(Dispatchers.IO).launch {
            val formattedRecipe = apiHandler.getRecipe()
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = currentRecipe.value.ImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {onRecipeClick(currentRecipe.value)},
                    contentScale = ContentScale.Crop
                )

                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                )

                Text(
                    text = currentRecipe.value.Title,
                    fontSize = 24.sp, // Larger text
                    fontWeight = FontWeight.Bold, // Bold text
                )
                Text(
                    text = "click image to view recipe",
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier
                        .height(100.dp)
                )
            }
            // Button placed at the bottom
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val formattedRecipe = apiHandler.getRecipe()
                        withContext(Dispatchers.Main) {
                            currentRecipe.value = formattedRecipe
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Press or shake for next recipe")
            }
        }
    }
}