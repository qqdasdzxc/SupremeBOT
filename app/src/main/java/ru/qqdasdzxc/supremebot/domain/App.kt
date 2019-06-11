package ru.qqdasdzxc.supremebot.domain

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import ru.qqdasdzxc.supremebot.data.db.Database


class App : Application() {

    companion object {
        private lateinit var database: Database
        fun getDatabase() = database
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, Database::class.java, Database.DATABASE_NAME).build()
        FirebaseApp.initializeApp(this)
    }
}