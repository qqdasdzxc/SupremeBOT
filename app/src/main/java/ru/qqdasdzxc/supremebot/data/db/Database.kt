package ru.qqdasdzxc.supremebot.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.qqdasdzxc.supremebot.data.db.converters.ItemTitleKeyWordsConverter
import ru.qqdasdzxc.supremebot.data.db.dao.UserProfileDao
import ru.qqdasdzxc.supremebot.data.dto.UserProfile

@Database(
    entities = [UserProfile::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(
    value = [ItemTitleKeyWordsConverter::class]
)
abstract class Database : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "hello_supreme_db"
    }

    abstract fun getUserProfileDao(): UserProfileDao
}