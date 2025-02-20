package org.tec.findrecipe.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tec.findrecipe.RecipeClass
import org.tec.findrecipe.components.FavoriteRecipeItem
import androidx.compose.foundation.lazy.items // ✅ Import this!
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tec.findrecipe.DataHandler


@Composable
fun FavoriteView(dataHandler: DataHandler,
                 onRecipeClick: (RecipeClass) -> Unit,
                 onRemoveFavorite: (RecipeClass) -> Unit) {
    val listOfRecipeClass = remember { mutableStateOf<List<RecipeClass>>(emptyList()) }

    LaunchedEffect(Unit) {
        val result = withContext(Dispatchers.IO) { dataHandler.GetFavoriteRecipes() }
        listOfRecipeClass.value = result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Favorite Recipes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp)  // Spaces each item
        ) {
            items(listOfRecipeClass.value) { recipe ->
                FavoriteRecipeItem(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe) },
                    onRemove = { onRemoveFavorite(recipe) }
                )
            }
        }
    }
}