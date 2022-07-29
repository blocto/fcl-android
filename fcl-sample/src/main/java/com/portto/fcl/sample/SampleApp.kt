package com.portto.fcl.sample

import android.app.Application
import com.portto.fcl.sample.ui.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import timber.log.Timber

class SampleApp : Application() {
    private val appModule = module {
        viewModel { MainViewModel() }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@SampleApp)
            modules(appModule)
        }
    }
}