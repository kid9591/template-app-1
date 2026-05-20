package com.example.todoist.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.todoist.data.local.datastore.DataStoreDataSource
import com.example.todoist.data.local.room.AppDatabase
import com.example.todoist.data.local.room.RoomTaskDataSource
import com.example.todoist.data.remote.HttpClientFactory
import com.example.todoist.data.remote.KtorTaskDataSource
import com.example.todoist.data.repository.OfflineFirstTaskRepository
import com.example.todoist.domain.KeyValueRepository
import com.example.todoist.domain.TaskRepository
import com.example.todoist.presentation.addtask.AddTaskViewModel
import com.example.todoist.presentation.home.HomeViewModel
import com.example.todoist.presentation.taskdetail.TaskDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create() }
    single { KtorTaskDataSource(get()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "todo_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().taskDao() }
    single { RoomTaskDataSource(get()) }
}

val datastoreModule = module {
    single {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("app_prefs") }
        )
    }
    single<KeyValueRepository> { DataStoreDataSource(get()) }
}

val dataModule = module {
    single<TaskRepository> { OfflineFirstTaskRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { AddTaskViewModel(get()) }
    viewModel { TaskDetailViewModel(get(), get()) }
}

val appModule = listOf(networkModule, databaseModule, datastoreModule, dataModule, viewModelModule)
