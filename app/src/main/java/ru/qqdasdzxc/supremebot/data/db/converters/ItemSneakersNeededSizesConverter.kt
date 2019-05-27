package ru.qqdasdzxc.supremebot.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import ru.qqdasdzxc.supremebot.utils.ConvertUtils

object ItemSneakersNeededSizesConverter {

    @TypeConverter
    @JvmStatic
    fun fromSneakersNeededSizes(data: List<String>?): String? {
        return ConvertUtils.getGsonConverter().toJsonTree(data).toString()
    }

    @TypeConverter
    @JvmStatic
    fun toSneakersNeededSizes(data: String?): List<String>? {
        return ConvertUtils.getGsonConverter().fromJson(data, object : TypeToken<List<String>?>() {}.type)
    }
}