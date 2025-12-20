package me.liwenkun.demo

import androidx.room.Database
import androidx.room.RoomDatabase
import me.liwenkun.demo.demoframework.DemoBook
import me.liwenkun.demo.demoframework.DemoItemDao

@Database(entities = [DemoBook.DemoItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun demoItemDao(): DemoItemDao
}
