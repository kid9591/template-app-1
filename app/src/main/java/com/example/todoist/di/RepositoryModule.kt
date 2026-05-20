package com.example.todoist.di

import com.example.todoist.data.cache.datastore.DataStoreSource
import com.example.todoist.data.repository.OfflineFirstTaskRepository
import com.example.todoist.domain.SimpleKVRepository
import com.example.todoist.domain.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: OfflineFirstTaskRepository): TaskRepository

    @Binds
    @Singleton
    abstract fun bindKeyValueRepository(impl: DataStoreSource): SimpleKVRepository
}
