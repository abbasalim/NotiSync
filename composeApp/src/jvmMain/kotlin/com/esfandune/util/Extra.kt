package com.esfandune.util

import java.net.Inet4Address
import java.net.NetworkInterface

 fun getDeviceIp(): String = NetworkInterface.getNetworkInterfaces()
    .toList()
    .find { it.name == "wlan0" || it.name.startsWith("en") }?.inetAddresses
    ?.toList()
    ?.firstOrNull { !it.isLoopbackAddress && it is Inet4Address }?.hostAddress ?: "Not Found Ip"
