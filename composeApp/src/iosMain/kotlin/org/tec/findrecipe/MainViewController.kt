package org.tec.findrecipe

import androidx.compose.ui.window.ComposeUIViewController
import io.ktor.client.engine.darwin.Darwin

fun MainViewController() = ComposeUIViewController {
    val driverFactory = DriverFactory()  // Use the iOS-specific driver factory
    val db = createDatabase(driverFactory)  // Create the database instance
    // Uses Darwin for IOS
    App(Darwin.create(), database = db)
}