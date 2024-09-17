package com.zello.sdk.example.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zello.sdk.ZelloAccountStatus
import com.zello.sdk.ZelloCredentials
import com.zello.sdk.ZelloState
import com.zello.sdk.example.app.databinding.ActivityMainBinding
import com.zello.sdk.example.app.repositories.ZelloRepository
import com.zello.sdk.example.app.ui.shared.ConnectDialog
import com.zello.sdk.example.app.ui.shared.StatusDialog
import com.zello.sdk.example.app.utils.PermissionsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	@Inject lateinit var repository: ZelloRepository

	private lateinit var binding: ActivityMainBinding

	private var showDialog = mutableStateOf(false)
	private var showStatusMenu = mutableStateOf(false)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val navView: BottomNavigationView = binding.navView

		val navController = findNavController(R.id.nav_host_fragment_activity_main)
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		val appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.navigation_recents,
				R.id.navigation_users,
				R.id.navigation_channels,
				R.id.navigation_group_conversations
			)
		)
		setupActionBarWithNavController(navController, appBarConfiguration)
		navView.setupWithNavController(navController)

		repository.zello.start()

		lifecycleScope.launch {
			repository.state.collect {
				invalidateOptionsMenu()
				updateUi()
			}
		}
		lifecycleScope.launch {
			repository.isConnected.collectLatest { _ ->
				invalidateOptionsMenu()
			}
		}
		lifecycleScope.launch {
			repository.isConnecting.collectLatest { _ ->
				invalidateOptionsMenu()
			}
		}
		lifecycleScope.launch {
			repository.accountStatus.collectLatest { _ ->
				invalidateOptionsMenu()
			}
		}

		val composeView = findViewById<ComposeView>(R.id.compose_view)
		composeView.setContent {
			if (showDialog.value) {
				ConnectDialog(
					onDismiss = {
						showDialog.value = false
					},
					onConnect = { username, password, network ->
						repository.zello.connect(ZelloCredentials(network, username, password))
						showDialog.value = false
					}
				)
			}

			val accountStatus = repository.accountStatus.asLiveData()
			if (showStatusMenu.value) {
				StatusDialog(
					selectedStatus = accountStatus.value ?: ZelloAccountStatus.AVAILABLE,
					onDismiss = {
						showStatusMenu.value = false
					},
					onSelectStatus = { status ->
						repository.zello.setAccountStatus(status)
						repository.zello.setAccountStatus(status)
						showStatusMenu.value = false
					}
				)
			}
		}

		PermissionsManager(this).requestPermissions()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		if (repository.zello.state != ZelloState.Started) {
			menu?.clear()
			return true
		}
		menuInflater.inflate(R.menu.menu_main, menu)
		val connectItem = menu?.findItem(R.id.connect_button)
		val isConnected = repository.isConnected.value
		val isConnecting = repository.isConnecting.value
		connectItem?.title = when {
			isConnected -> getString(R.string.disconnect)
			isConnecting -> getString(R.string.connecting)
			else -> getString(R.string.connect)
		}

		val statusItem = menu?.findItem(R.id.status_button)
		statusItem?.isVisible = isConnected
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.connect_button -> {
				val isConnected = repository.isConnected.value
				if (isConnected) {
					repository.zello.disconnect()
				} else {
					showDialog.value = true
				}
				true
			}

			R.id.status_button -> {
				showStatusMenu.value = true
				true
			}

			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun updateUi() {
		binding.root.visibility = when (repository.zello.state) {
			ZelloState.Started -> View.VISIBLE
			else -> View.INVISIBLE
		}
	}
}
