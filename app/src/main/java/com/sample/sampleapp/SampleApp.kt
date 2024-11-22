package com.sample.sampleapp

import android.app.Application


class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: SampleApp? = null
        fun get(): SampleApp? {
            return instance
        }
    }
}