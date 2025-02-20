package org.tec.findrecipe.views

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


@Composable
fun FavoriteView(favoritesList: List<RecipeClass>, onRecipeClick: (RecipeClass) -> Unit) {
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

        LazyColumn {
//            items(favoritesList) { recipe ->
//                FavoriteRecipeItem(recipe) { onRecipeClick(recipe) }
//            }
        }
    }
}