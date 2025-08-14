package com.bilty.generator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform