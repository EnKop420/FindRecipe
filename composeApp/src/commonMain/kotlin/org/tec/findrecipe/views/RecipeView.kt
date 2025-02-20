package org.tec.findrecipe.views

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import org.tec.findrecipe.RecipeClass
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun RecipeView(recipe: RecipeClass){
    val testRecipe = RecipeClass("Test Title", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.", "Image Url", emptyList())

    var currentRecipe = remember { mutableStateOf(recipe) }

    Surface(){
    }
}