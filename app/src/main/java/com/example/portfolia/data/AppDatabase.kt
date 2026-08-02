package com.example.portfolia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ProjectEntity::class, UserProfileEntity::class, ReferenceEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun profileDao(): ProfileDao
    abstract fun referenceDao(): ReferenceDao

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
                                database.profileDao().saveUserProfile(
                                    UserProfileEntity(
                                        name = "Developer Name",
                                        title = "Mobile & Web Engineer",
                                        bio = "Welcome to my portfolio! Customize your profile in settings or profile tab."
                                    )
                                )
                                database.referenceDao().insertReference(
                                    ReferenceEntity(
                                        title = "Jetpack Compose Docs",
                                        url = "https://developer.android.com/compose",
                                        category = "Docs",
                                        notes = "Official Android UI framework documentation"
                                    )
                                )
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
