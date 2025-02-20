package org.tec.findrecipe.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.tec.findrecipe.RecipeClass
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults


@Composable
fun FavoriteRecipeItem(recipe: RecipeClass,
                       onClick: () -> Unit,
                       onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image on the Left
        AsyncImage(
            model = recipe.ImageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp) // Square image
                .clip(RoundedCornerShape(12.dp)) // Same rounding as border
        )

        Spacer(modifier = Modifier.width(12.dp)) // Space between image & text

        // Title on the Right
        Text(
            text = recipe.Title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f) // Makes text take up remaining space
        )

        Button(onClick = {onRemove()},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)){
            Text("X", color = Color.White)
        }
    }
}

