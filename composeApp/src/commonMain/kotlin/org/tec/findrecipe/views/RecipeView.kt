package org.tec.findrecipe.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import findrecipe.composeapp.generated.resources.Res
import findrecipe.composeapp.generated.resources.heart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.tec.findrecipe.Handlers.DataHandler
import org.tec.findrecipe.RecipeClass
import org.tec.findrecipe.components.DialogBox

@Composable
fun RecipeView(recipe: RecipeClass, dataHandler: DataHandler) {
    // Hold the current recipe in a mutable state variable
    val currentRecipe = remember { mutableStateOf(recipe) }

    // Create a scroll state to track scroll position (similar to ScrollView in other frameworks)
    val scrollState = rememberScrollState()

    // Surface is used to apply background and padding for the view (like a Card or Container)
    Surface(
        modifier = Modifier
            .fillMaxSize() // Fill the available screen space
            .padding(10.dp) // Apply padding around the edges of the Surface
            .verticalScroll(scrollState) // Make the Surface scrollable
    ) {
        Column(
            modifier = Modifier.padding(5.dp)  // Add general padding for the entire Column
        ) {
            // Display the recipe title with a larger, bold font
            Text(currentRecipe.value.Title, fontSize = 26.sp, fontWeight = FontWeight.Bold)

            // Display the recipe image with specific size and rounded corners
            AsyncImage(
                model = currentRecipe.value.ImageUrl,
                contentDescription = null, // No need for content description since it's decorative
                modifier = Modifier
                    .size(300.dp) // Set a fixed size for the image
                    .clip(RoundedCornerShape(10.dp)), // Apply rounded corners
                contentScale = ContentScale.Crop // Crop the image to fill the size without distortion
            )

            // Display Ingredients section with the corresponding content
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text("Ingredients", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(currentRecipe.value.IngredientsAndMeasurements, fontSize = 12.sp)
            }

            // Display Instructions section with the corresponding content
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text("Instruction", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(currentRecipe.value.Instruction, fontSize = 12.sp)
            }

            // Column to contain the "favorite" button and show success dialog when clicked
            Column(
                Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
                horizontalAlignment = Alignment.End, // Align content to the right side
            ) {
                // State to handle the size of the heart image (whether it's grown or not)
                var sizeState by remember { mutableStateOf(40.dp) }

                // State to handle the visibility of the dialog box
                var showDialog by remember { mutableStateOf(false) }

                // Animate the size of the heart image when clicked (smooth transition)
                val imageSize by animateDpAsState(
                    targetValue = sizeState, // Target size for the image (either grown or original)
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300) // Duration of the animation
                )

                // Handle button click to grow the image size and then shrink it back
                Button(
                    onClick = {
                        // Grow the image size when clicked
                        sizeState = 50.dp

                        // After a delay, shrink the image back to the original size
                        CoroutineScope(Dispatchers.Default).launch {
                            delay(300) // Wait for the duration of the "grow" animation
                            sizeState = 40.dp // Shrink back to the original size

                            // Add the current recipe to the favorites database
                            val result = dataHandler.AddFavoriteRecipeToDatabase(currentRecipe.value)

                            // Show the dialog if the recipe was successfully added to favorites
                            if (result) {
                                showDialog = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent), // Set button background as transparent
                    elevation = null // No shadow/elevation for the button
                ) {
                    // Display a heart image inside the button, resizing it based on animation
                    Image(
                        painter = painterResource(Res.drawable.heart), // Your drawable resource (heart icon)
                        contentDescription = "Like Button", // Description for accessibility
                        modifier = Modifier.size(imageSize) // Apply the animated size here
                    )

                    // DialogBox to show the "Added to your favorite list" message
                    DialogBox(
                        showDialog = showDialog,
                        title = "Added Favorite", // Dialog title
                        message = "Added to your favorite list", // Dialog message
                        onDismiss = { showDialog = false } // Dismiss dialog when user interacts
                    )
                }
            }
        }
    }
}
