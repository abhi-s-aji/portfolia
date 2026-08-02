package com.example.portfolia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ProjectEntity::class, UserProfileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "portfolia_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                // Default initial profile (Fully editable by user)
                                database.profileDao().saveUserProfile(
                                    UserProfileEntity(
                                        name = "Developer Name",
                                        title = "Mobile & Web Engineer",
                                        bio = "Welcome to my portfolio! Tap the edit icon to customize your profile info."
                                    )
                                )
                                // Default starter project
                                database.projectDao().insertProject(
                                    ProjectEntity(
                                        title = "Portfolia App",
                                        description = "Material You Portfolio Manager built with Jetpack Compose & Room DB.",
                                        category = "Android",
                                        techStack = "Kotlin, Compose, Room",
                                        githubUrl = "https://github.com",
                                        isFeatured = true
                                    )
                                )
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
