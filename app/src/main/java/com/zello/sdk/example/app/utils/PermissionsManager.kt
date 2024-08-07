package com.zello.sdk.example.app.utils

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Sequentially requests permissions needed for the app to function.
 */
class PermissionsManager(activity: ComponentActivity) {

	private var recordAudioGranted: Boolean? = null
	private var postNotificationsGranted: Boolean? = null
	private var locationGranted: Boolean? = null

	private val recordAudioProvider = activity.registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) {
		recordAudioGranted = it
		checkPermissionsKnown()
	}

	private val postNotificationsPermissionProvider = activity.registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) {
		postNotificationsGranted = it
		checkPermissionsKnown()
	}

	private val locationPermissionProvider = activity.registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) {
		locationGranted = it
		checkPermissionsKnown()
	}

	fun requestPermissions() {
		requestRecordAudio()
	}

	private fun checkPermissionsKnown() {
		if (recordAudioGranted == null) {
			requestRecordAudio()
			return
		}
		if (postNotificationsGranted == null) {
			requestPostNotifications()
			return
		}
		if (locationGranted == null) {
			requestLocation()
			return
		}
	}

	private fun requestPostNotifications() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			postNotificationsPermissionProvider.launch(Manifest.permission.POST_NOTIFICATIONS)
		} else {
			postNotificationsGranted = true
			checkPermissionsKnown()
		}
	}

	private fun requestRecordAudio() {
		recordAudioProvider.launch(Manifest.permission.RECORD_AUDIO)
	}

	private fun requestLocation() {
		locationPermissionProvider.launch(Manifest.permission.ACCESS_FINE_LOCATION)
	}
}
