package me.liwenkun.demo

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Message
import androidx.annotation.RequiresApi
import androidx.room.Room.databaseBuilder

class App : Application() {
    val appDatabase: AppDatabase by lazy {
        databaseBuilder(this, AppDatabase::class.java, "ap-db").build()
    }

    lateinit var handler: Handler

    override fun onCreate() {
        super.onCreate()
        app = this
        handler = Handler(mainLooper)
        SourceInjector.init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun post(block: () -> Unit) =
        handler.sendMessage(Message.obtain(handler) {block()})


    companion object {
        private lateinit var app: App
        @JvmStatic
        fun get(): App {
            return app
        }
    }
}