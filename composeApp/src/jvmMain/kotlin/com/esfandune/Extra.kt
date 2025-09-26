package com.esfandune

fun isDebugMode(): Boolean {
    return System.getProperty("java.class.path").contains("debug") ||
            System.getProperty("java.class.path").contains("test") ||
            System.getProperty("java.class.path").contains("gradle") ||
            System.getProperty("java.class.path").contains("build")
}