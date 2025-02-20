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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tec.findrecipe.Handlers.ApiHandler
import org.tec.findrecipe.Handlers.DataHandler
import org.tec.findrecipe.RecipeClass
import org.tec.findrecipe.components.DialogBox


@Composable
fun FeedView(apiHandler: ApiHandler, dataHandler: DataHandler, onRecipeClick: (RecipeClass, DataHandler) -> Unit) {
    // SnackbarHostState used for displaying snackbars at the bottom of the screen
    val snackbarHostState = remember { SnackbarHostState() }

    // Creating an empty recipe instance to set the initial state
    val defaultRecipe = RecipeClass(null, "", "", "", "")

    // Detecting shake gestures to fetch a new recipe
    val shakeDetector = rememberShakeDetector()

    // Current recipe to display in the UI, remembered across recompositions
    val currentRecipe = remember { mutableStateOf(defaultRecipe) }

    // State variables for error handling and dialog display
    var showErrorDialogBox by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // region: Shake Detection Functions
    // The LaunchedEffect function runs code when the composable is first displayed (similar to `OnCreate` in C#)
    LaunchedEffect(Unit) {
        shakeDetector.start() // Start detecting shake gestures
        // Fetch a new recipe in a background coroutine when the screen is first loaded
        CoroutineScope(Dispatchers.IO).launch {
            val formattedRecipe = apiHandler.getRecipe()
            withContext(Dispatchers.Main) {
                // Updating the UI safely on the main thread with the fetched recipe
                currentRecipe.value = formattedRecipe
            }
        }
    }

    // DisposableEffect cleans up side-effects when the composable is removed
    DisposableEffect(Unit) {
        onDispose {
            // Stops shake detection when the composable is no longer in use
            shakeDetector.stop()
        }
    }

    // Mutex ensures that only one coroutine runs at a time (prevents race conditions)
    val mutex = Mutex()

    // When a shake is detected, launch a coroutine to fetch a new recipe
    shakeDetector.onShake {
        CoroutineScope(Dispatchers.IO).launch {
            if (mutex.isLocked) return@launch // Exit if the mutex is locked

            // Lock the mutex to ensure only one coroutine executes at a time
            mutex.withLock {
                // Show a snackbar indicating a new recipe is being fetched
                snackbarHostState.showSnackbar("Trying to find a new recipe....")

                // Call the API to fetch a recipe and safely update the UI on the main thread
                val recipe = apiHandler.GetRecipeFromApi()
                if (recipe != "") {
                    val formattedRecipe = apiHandler.FormatResponse(recipe)
                    withContext(Dispatchers.Main) {
                        currentRecipe.value = formattedRecipe
                    }
                }

                try {
                    // Fetch a recipe again in case of errors
                    val formattedRecipe = apiHandler.getRecipe()
                    withContext(Dispatchers.Main) {
                        currentRecipe.value = formattedRecipe
                    }
                } catch (e: Exception) {
                    // If an error occurs, set the error message and show the error dialog
                    errorMessage = e.message.toString()
                    showErrorDialogBox = true
                }
            }
        }
    }
    //endregion

    // Scaffold layout provides a top-level container for Material Design components like snackbars
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Displays the snackbar
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize() // Fills the available screen space
                .padding(paddingValues) // Applies padding to avoid content being cut off
        ) {
            // Column arranges children vertically and applies scroll for overflow
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Center children horizontally
                modifier = Modifier
                    .fillMaxWidth() // Takes the full width of the screen
                    .fillMaxHeight() // Takes the full height of the screen
                    .align(Alignment.TopCenter) // Aligns content at the top of the box
                    .verticalScroll(rememberScrollState()) // Enables vertical scrolling for long content
            ) {
                // Image representing the current recipe, with click functionality to view the recipe
                AsyncImage(
                    model = currentRecipe.value.ImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp) // Set image size
                        .clip(RoundedCornerShape(10.dp)) // Rounded corners for the image
                        .clickable { onRecipeClick(currentRecipe.value, dataHandler) }, // Trigger onRecipeClick when clicked
                    contentScale = ContentScale.Crop // Crop the image to fit the bounds
                )

                Spacer(modifier = Modifier.height(16.dp)) // Space between image and title

                // Displaying recipe title and instructions for the user
                Text(
                    text = currentRecipe.value.Title,
                    fontSize = 24.sp, // Larger font size for the title
                    fontWeight = FontWeight.Bold, // Bold text for the title
                )
                Text(
                    text = "Click image to view recipe", // Instructions for the user to click the image
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(100.dp)) // Adding space below the text
            }

            // Button placed at the bottom of the screen to trigger fetching a new recipe
            Button(
                onClick = {
                    // When clicked, fetch a new recipe in the background
                    CoroutineScope(Dispatchers.IO).launch {
                        val formattedRecipe = apiHandler.getRecipe()
                        withContext(Dispatchers.Main) {
                            currentRecipe.value = formattedRecipe
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Aligns the button at the bottom
                    .padding(16.dp) // Adds padding around the button
            ) {
                Text("Press or shake for next recipe") // Button text
            }
        }
    }

    // Display the error dialog if an error occurs during recipe fetching
    DialogBox(
        showDialog = showErrorDialogBox,
        title = "An Error Occured", // Dialog title
        message = errorMessage, // Error message to display
        onDismiss = { showErrorDialogBox = false } // Dismiss dialog on close
    )
}
