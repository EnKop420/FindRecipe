import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import findrecipe.composeapp.generated.resources.Res
import findrecipe.composeapp.generated.resources.cheeseburger
import org.jetbrains.compose.resources.painterResource
import org.tec.findrecipe.ViewType


@Composable
fun MinimalDropdownMenu(onSelectedItem: (ViewType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(
    ) {
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            elevation = null,
        ) {
            Image(
                painter = painterResource(Res.drawable.cheeseburger),
                contentDescription = "Burger button",
                modifier = Modifier
                    .size(40.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = true),
        ) {
            DropdownMenuItem(onClick = { onSelectedItem(ViewType.Feed); expanded = false })
                {Text("Feed")}
            DropdownMenuItem(onClick = { onSelectedItem(ViewType.Favorites); expanded = false })
                {Text("Favorites")}
            DropdownMenuItem(onClick = { onSelectedItem(ViewType.Settings); expanded = false })
                {Text("Settings")}
        }
    }
}
