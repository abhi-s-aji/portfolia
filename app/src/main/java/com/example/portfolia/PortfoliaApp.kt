package com.example.portfolia

import android.app.Application
import com.example.portfolia.data.AppDatabase

class PortfoliaApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
