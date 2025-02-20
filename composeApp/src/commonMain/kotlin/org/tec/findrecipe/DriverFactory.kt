package org.tec.findrecipe
import app.cash.sqldelight.db.SqlDriver
import org.tec.findrecipe.Database

// The 'expect' keyword is used in Kotlin Multiplatform to define a function or class
// that has platform-specific implementations in each target (e.g., Android, iOS, JVM, etc.).
// In this case, it's defining a class 'DriverFactory' that will be implemented separately for each platform.
expect class DriverFactory {
    // This function will return a SqlDriver, which is specific to each platform.
    // A SqlDriver is used to create a connection to the SQLite database.
    fun createDriver(): SqlDriver
}

// This is a simple function that takes a DriverFactory object and returns a Database instance.
fun createDatabase(driverFactory: DriverFactory): Database {
    // Call the 'createDriver' function of DriverFactory to obtain a platform-specific SqlDriver
    val driver = driverFactory.createDriver()

    // Create a new instance of the 'Database' class by passing the SqlDriver to its constructor.
    // The 'Database' class is most likely using SQLDelight to handle interactions with an SQLite database.
    val database = Database(driver)

    // Here you could perform other setup operations on the database if needed (e.g., migrations, etc.).
    // This part of the code is simply returning the created 'Database' instance.
    return database
}