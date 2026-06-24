package br.edu.ifpb.pdm.medlembrete

import android.app.Application
import br.edu.ifpb.pdm.medlembrete.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MedLembreteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MedLembreteApplication)
            modules(appModule)
        }
    }
}