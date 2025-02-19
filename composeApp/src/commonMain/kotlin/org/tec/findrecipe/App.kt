package org.tec.findrecipe

import MinimalDropdownMenu
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.tec.findrecipe.views.FavoriteView
import org.tec.findrecipe.views.FeedView
import org.tec.findrecipe.views.SettingsView

@Composable
@Preview
fun App() {
    MaterialTheme {
        var selectedView by remember { mutableStateOf<ViewType>(ViewType.Feed) }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                MinimalDropdownMenu {selectedView = it }
            }
            when (selectedView) {
                ViewType.Feed -> FeedView()
                ViewType.Favorites -> FavoriteView()
                ViewType.Settings -> SettingsView()
            }
        }
    }
}

// Enum class to track which view is selected
enum class ViewType {
    Feed, Favorites, Settings
}