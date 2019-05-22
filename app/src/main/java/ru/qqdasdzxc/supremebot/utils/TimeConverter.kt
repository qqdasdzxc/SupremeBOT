package ru.qqdasdzxc.supremebot.utils

object TimeConverter {

    fun seconds(startTime: Long, stopTime: Long): String {
        return ((stopTime - startTime) / 1000).toFloat().toString().substring(0, 1)
    }
}