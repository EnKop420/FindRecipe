package org.tec.findrecipe

import MinimalDropdownMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chaintech.network.cmpshakedetection.rememberShakeDetector
import io.ktor.client.engine.HttpClientEngine
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tec.findrecipe.networking.createHttpClient

import org.tec.findrecipe.views.FavoriteView
import org.tec.findrecipe.views.FeedView
import org.tec.findrecipe.views.SettingsView

@Composable
@Preview
fun App(engine: HttpClientEngine) {
    MaterialTheme {
        val client = createHttpClient(engine)
        val apiHandler = ApiHandler(client)

        var selectedView by remember { mutableStateOf<ViewType>(ViewType.Feed) }

        Surface {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.End) {
                    MinimalDropdownMenu { selectedView = it }
                }

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                ){
                    when (selectedView) {
                        ViewType.Feed -> FeedView(apiHandler)
                        ViewType.Favorites -> FavoriteView()
                        ViewType.Settings -> SettingsView()
                    }
                }
            }
        }
    }
}

enum class ViewType {
    Feed, Favorites, Settings
}
