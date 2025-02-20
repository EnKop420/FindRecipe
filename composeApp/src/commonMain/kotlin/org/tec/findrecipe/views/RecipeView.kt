package org.tec.findrecipe.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import findrecipe.composeapp.generated.resources.Res
import findrecipe.composeapp.generated.resources.cheeseburger
import findrecipe.composeapp.generated.resources.heart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.tec.findrecipe.RecipeClass

@Composable
fun RecipeView(recipe: RecipeClass){

    var currentRecipe = remember { mutableStateOf(recipe) }
    // Create a scroll state to track scroll position
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier.padding(5.dp)  // You can also add a general padding for the whole column
        ) {
            Text(currentRecipe.value.Title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            AsyncImage(
                model = currentRecipe.value.ImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .align(Alignment.Start),
                contentScale = ContentScale.Crop
            )
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text("Ingredients", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(currentRecipe.value.IngredientsAndMeasurements, fontSize = 12.sp)
            }
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text("Instruction", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(currentRecipe.value.Instruction, fontSize = 12.sp)
            }
            Column(
                Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
                horizontalAlignment = Alignment.End,
            ) {
                // State to handle the size of the image (whether it's grown or not)
                var sizeState by remember { mutableStateOf(40.dp) }

                // State to handle the dialog visibility
                var showDialog by remember { mutableStateOf(false) }

                // Animate the size of the heart image
                val imageSize by animateDpAsState(
                    targetValue = sizeState, // Target size for the image (either grown or original)
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300) // Duration of the animation
                )

                // Handle button click to grow the image and then shrink it back
                Button(
                    onClick = {
                        // Grow the image size when clicked
                        sizeState = 50.dp

                        // After a delay, shrink the image back to the original size
                        CoroutineScope(Dispatchers.Default).launch {
                            delay(300) // Wait for the duration of the "grow" animation
                            sizeState = 40.dp // Shrink back to the original size
                        }

                        // Show the dialog
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    elevation = null
                ) {
                    Image(
                        painter = painterResource(Res.drawable.heart), // Your drawable resource
                        contentDescription = "Like Button",
                        modifier = Modifier.size(imageSize) // Apply the animated size here
                    )

                    // Dialog to show the "Added to your favorite list" message
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDialog = false
                            }, // Dismiss the dialog when clicked outside
                            title = { Text("Added Favorite") },
                            text = { Text("Added to your favorite list") },
                            confirmButton = {
                                Button(
                                    onClick = { showDialog = false } // Close dialog when clicked
                                ) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                }
            }
        }
        Surface() {
        }
    }
}