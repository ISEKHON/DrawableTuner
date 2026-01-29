package xyz.isekhon.drawabletuner

import android.app.Application
import xyz.isekhon.drawabletuner.data.repository.DrawableSpecRepository

class App : Application() {
    
    companion object {
        lateinit var instance: App
            private set
    }
    
    val repository: DrawableSpecRepository by lazy {
        DrawableSpecRepository.getInstance(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
