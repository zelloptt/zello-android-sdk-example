package com.zello.sdk.example.app

import android.app.Application
import com.zello.sdk.Zello
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class Application : Application() {

	@Inject lateinit var zello: Zello

	override fun onCreate() {
		super.onCreate()
		zello.start()
	}

}
