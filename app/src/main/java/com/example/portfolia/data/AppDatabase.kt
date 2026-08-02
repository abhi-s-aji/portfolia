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
    version = 3,
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
                                        id = 1,
                                        name = "Julian Thorne",
                                        title = "Mobile & Web Engineer",
                                        bio = "Specializing in high-performance cross-platform applications and minimalist system design. I build digital tools that bridge the gap between industrial-grade reliability and seamless user experience. My focus lies in Flutter, React, and Rust-based backends, emphasizing structural integrity and absolute visual clarity in every pixel.",
                                        email = "j.thorne@dev.v1",
                                        githubUrl = "github.com/thorne-dev",
                                        linkedinUrl = "linkedin.com/in/thorne",
                                        experience = "8+ YRS",
                                        uptime = "99.9%",
                                        commits = "12.4k"
                                    )
                                )
                                database.projectDao().insertProject(
                                    ProjectEntity(
                                        title = "Portfolia App",
                                        description = "Material You Portfolio Manager built with Jetpack Compose & Room DB. Orchestrates complex data flows with functional reactive patterns.",
                                        category = "Android",
                                        techStack = "Kotlin, Compose, Room",
                                        githubUrl = "https://github.com",
                                        isFeatured = true
                                    )
                                )
                                database.projectDao().insertProject(
                                    ProjectEntity(
                                        title = "Rust Kernel Module",
                                        description = "High-performance memory management module optimized for low-latency kernel interactions and hardware-level abstractions.",
                                        category = "Systems",
                                        techStack = "Rust, C++",
                                        githubUrl = "https://github.com",
                                        isFeatured = true
                                    )
                                )
                                database.projectDao().insertProject(
                                    ProjectEntity(
                                        title = "Nexus CLI",
                                        description = "A command-line interface for rapid microservice scaffolding and automated environment configuration for full-stack teams.",
                                        category = "Web",
                                        techStack = "Go, Docker",
                                        githubUrl = "https://github.com",
                                        isFeatured = true
                                    )
                                )
                                database.referenceDao().insertReference(
                                    ReferenceEntity(
                                        title = "Jetpack Compose Docs",
                                        url = "https://developer.android.com/compose",
                                        category = "Docs",
                                        notes = "Official documentation for Android's modern toolkit for building native UI. Includes best practices, layout guides, and side-effect handling."
                                    )
                                )
                                database.referenceDao().insertReference(
                                    ReferenceEntity(
                                        title = "Tailwind CSS Core",
                                        url = "https://github.com/tailwindlabs/tailwindcss",
                                        category = "Repos",
                                        notes = "Reference for the engine behind utility-first styling. Useful for tracking upcoming PRs and architectural shifts in the framework."
                                    )
                                )
                                database.referenceDao().insertReference(
                                    ReferenceEntity(
                                        title = "JSON Crack Visualization",
                                        url = "https://jsoncrack.com",
                                        category = "Tools",
                                        notes = "Visualizing complex nested data structures. Essential for mapping out large-scale API responses during the discovery phase."
                                    )
                                )
                                database.referenceDao().insertReference(
                                    ReferenceEntity(
                                        title = "Next.js App Router API",
                                        url = "https://nextjs.org/docs",
                                        category = "Frameworks",
                                        notes = "The definitive guide to server actions, parallel routing, and data fetching strategies in the latest Next.js deployments."
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
