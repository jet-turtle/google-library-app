package kz.rusmen.googlelibrary

import android.app.Application
import kz.rusmen.googlelibrary.data.AppContainer
import kz.rusmen.googlelibrary.data.DefaultAppContainer

class BooksApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
