package org.tec.findrecipe

import MinimalDropdownMenu
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import findrecipe.composeapp.generated.resources.Res
import findrecipe.composeapp.generated.resources.cheeseburger
import findrecipe.composeapp.generated.resources.heart

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) { MinimalDropdownMenu() }
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
        }
    }
}