package com.test.cashi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform