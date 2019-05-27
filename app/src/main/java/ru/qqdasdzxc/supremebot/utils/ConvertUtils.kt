package ru.qqdasdzxc.supremebot.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object ConvertUtils {

    fun getGsonConverter(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    fun <T> listToJsonString(list: List<T>): String? {
        return getGsonConverter()
            .toJsonTree(list)
            .toString()
    }
}