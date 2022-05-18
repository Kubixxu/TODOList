package com.example.todolist.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolist.model.Task
import com.example.todolist.model.Topic
import com.example.todolist.task.Converters
import java.time.LocalDate


@Database(entities = [Topic::class, Task::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TodoDatabase:  RoomDatabase() {

    abstract fun topicDao(): TopicDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                ).addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    " CREATE TABLE IF NOT EXISTS tasks " +
                            "(id INTEGER NOT NULL, " +
                            "topic INTEGER NOT NULL," +
                            "name TEXT NOT NULL," +
                            "date INTEGER NOT NULL, " +
                            "flag INTEGER NOT NULL," +
                            "completed INTEGER NOT NULL," +
                            "dateCreation INTEGER NOT NULL, " +
                            "PRIMARY KEY(id), " +
                            "FOREIGN KEY (topic) REFERENCES topics(id) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE)");
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    " ALTER TABLE tasks ADD voiceRecordPath VARCHAR(100);")
            }
        }

//        var id: Int,
//        var topic: Int,
//        var name: String,
//        var date: LocalDate,
//        var flag: Boolean,
//        var completed: Boolean,
//        var dateCreation: LocalDate
    }
}