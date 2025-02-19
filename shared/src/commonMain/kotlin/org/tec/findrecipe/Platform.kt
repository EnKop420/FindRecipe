package org.tec.findrecipe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform