package com.example.todoist

import android.app.Application
import com.example.todoist.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TodoApp)
            modules(appModule)
        }
    }
}
