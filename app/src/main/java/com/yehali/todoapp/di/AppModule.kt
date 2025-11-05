package com.yehali.todoapp.di
import android.content.Context
import androidx.room.Room
import com.yehali.todoapp.data.local.TaskDao
import com.yehali.todoapp.data.local.TaskDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context
    ): TaskDB {
        return Room.databaseBuilder(
            context,
            TaskDB::class.java,
            TaskDB.DATABASE_NAME
        ).build()
    }


     // Provide the TaskDao from database

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDB): TaskDao {
        return database.taskDao()
    }
}