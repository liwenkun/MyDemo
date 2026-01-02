package me.liwenkun.demo.demoframework

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import me.liwenkun.demo.demoframework.DemoBook.DemoItem

@Dao
interface DemoItemDao {
    @Query("select * from demo_items where path = :path")
    fun get(path: String): LiveData<DemoItem>
    @Query("update demo_items set is_starred = :star where path = :demoId")
    fun star(demoId: String, star: Boolean): Int
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(demoItem: DemoItem)
    @Query("select * from demo_items where is_starred = 1")
    fun getStarred(): LiveData<List<DemoItem>>
    @Query("select is_starred from demo_items where path = :path")
    fun isStarred(path: String): LiveData<Boolean>
}

class DemoItemConverters {
    @TypeConverter
    fun fromClass(clazz: Class<*>): String {
        return clazz.name
    }

    @TypeConverter
    fun toClass(clazzName: String): Class<*>? {
        return try {
            Class.forName(clazzName)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}