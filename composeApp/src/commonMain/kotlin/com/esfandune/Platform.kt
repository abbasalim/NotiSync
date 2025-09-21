package com.esfandune

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform