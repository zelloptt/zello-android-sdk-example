package com.zello.sdk.example.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Draw behind the system bars (edge-to-edge is enforced on Android 15+) and apply
		// the insets manually so the app bar and bottom navigation are not covered.
		WindowCompat.setDecorFitsSystemWindows(window, false)
		setSupportActionBar(binding.toolbar)
		applyWindowInsets()

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
			repository.isSignedIn.collectLatest { signedIn ->
				// Only return to the pre-sign-in state on a final disconnect (not while the SDK is
				// auto-reconnecting): pop the tab back stack that was built while signed in so the
				// start destination (Recents) is the only entry again. With a single entry the
				// NavController stops intercepting Back, so Back closes the app the same way it did
				// before the first sign-in.
				if (!signedIn) {
					findNavController(R.id.nav_host_fragment_activity_main)
						.popBackStack(R.id.navigation_recents, false)
				}
				invalidateOptionsMenu()
				updateUi()
			}
		}
		lifecycleScope.launch {
			repository.isConnected.collectLatest { _ ->
				invalidateOptionsMenu()
				updateUi()
			}
		}
		lifecycleScope.launch {
			repository.isConnecting.collectLatest { _ ->
				invalidateOptionsMenu()
			}
		}
		lifecycleScope.launch {
			repository.isReconnecting.collectLatest { _ ->
				invalidateOptionsMenu()
				updateUi()
			}
		}
		lifecycleScope.launch {
			repository.accountStatus.collectLatest { _ ->
				invalidateOptionsMenu()
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
		val isSignedIn = repository.isSignedIn.value
		connectItem?.title = when {
			// A live session (connected, or temporarily offline while auto-reconnecting) can always
			// be disconnected, so surface Disconnect for the whole signed-in lifetime.
			isSignedIn -> getString(R.string.disconnect)
			// An initial connect is in flight; there is no session yet.
			isConnecting -> getString(R.string.connecting)
			else -> getString(R.string.connect)
		}
		// Disable the button only during an initial connect (no session yet, nothing to cancel).
		// A signed-in session stays actionable so the user can disconnect even mid-reconnect.
		connectItem?.isEnabled = isSignedIn || !isConnecting

		val statusItem = menu?.findItem(R.id.status_button)
		statusItem?.isVisible = isConnected
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.connect_button -> {
				// A signed-in session (connected or reconnecting) disconnects; with no session, offer
				// the login dialog. An initial connect is a no-op here (the item is disabled in that
				// state) so we never pop the login dialog while a connect/reconnect is in progress.
				if (repository.isSignedIn.value) {
					repository.zello.disconnect()
				} else if (!repository.isConnecting.value) {
					showConnectDialog()
				}
				true
			}

			R.id.status_button -> {
				showStatusDialog()
				true
			}

			else -> super.onOptionsItemSelected(item)
		}
	}

	// Dialogs are shown on demand in a throwaway ComposeView instead of one long-lived
	// composition. A freshly attached ComposeView always runs its initial composition, so the
	// dialog appears every time; reusing a single composition and toggling a flag on it is what
	// stopped recomposing after a connect/disconnect cycle.
	private fun showConnectDialog() {
		showDialogHost { dismiss ->
			ConnectDialog(
				onDismiss = dismiss,
				onConnect = { username, password, network ->
					repository.zello.connect(ZelloCredentials(network, username, password))
					dismiss()
				}
			)
		}
	}

	private fun showStatusDialog() {
		val currentStatus = repository.accountStatus.value ?: ZelloAccountStatus.AVAILABLE
		showDialogHost { dismiss ->
			StatusDialog(
				selectedStatus = currentStatus,
				onDismiss = dismiss,
				onSelectStatus = { status ->
					repository.zello.setAccountStatus(status)
					dismiss()
				}
			)
		}
	}

	// Attaches a throwaway ComposeView on top of the content to host a dialog. The dialog draws
	// in its own window, so the host is added at zero size; dismissing removes the host, which
	// disposes the composition and dismisses the dialog.
	private fun showDialogHost(content: @Composable (dismiss: () -> Unit) -> Unit) {
		val root = findViewById<ViewGroup>(android.R.id.content)
		val host = ComposeView(this)
		val dismiss: () -> Unit = {
			(host.parent as? ViewGroup)?.removeView(host)
		}
		host.setContent { content(dismiss) }
		root.addView(host, ViewGroup.LayoutParams(0, 0))
	}

	private fun applyWindowInsets() {
		// The status bar area is painted by the purple app bar, so keep its icons light;
		// the bottom navigation is light, so keep the navigation bar icons dark.
		WindowCompat.getInsetsController(window, window.decorView).run {
			isAppearanceLightStatusBars = false
			isAppearanceLightNavigationBars = true
		}

		// Pad the app bar down by the status bar height and the bottom navigation up by the
		// navigation bar height; also apply horizontal insets for landscape display cutouts.
		ViewCompat.setOnApplyWindowInsetsListener(binding.container) { _, insets ->
			val bars = insets.getInsets(
				WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
			)
			binding.container.updatePadding(left = bars.left, right = bars.right)
			binding.appBar.updatePadding(top = bars.top)
			binding.navView.updatePadding(bottom = bars.bottom)
			insets
		}
	}

	private fun updateUi() {
		// The toolbar hosts the Connect button, so it must stay reachable while disconnected.
		// Only hide it before the SDK has started.
		val started = repository.zello.state == ZelloState.Started
		binding.appBar.visibility = if (started) View.VISIBLE else View.INVISIBLE

		// The contact lists and bottom navigation belong to a signed-in session. Keep them visible
		// for the whole session - including while the SDK is auto-reconnecting after a network drop -
		// and only hide them once the session is fully signed out.
		val signedIn = repository.isSignedIn.value
		val contentVisibility = if (signedIn) View.VISIBLE else View.INVISIBLE
		findViewById<View>(R.id.nav_host_fragment_activity_main).visibility = contentVisibility
		binding.navView.visibility = contentVisibility

		// NavigationUI keeps the current tab's title (e.g. "Recents") in the action bar while signed
		// in - including while the SDK is auto-reconnecting, so the title keeps reflecting the active
		// tab rather than the connection state. While signed out the tabs are hidden, so show the app
		// name instead of a stale tab title.
		supportActionBar?.title = if (signedIn) {
			findNavController(R.id.nav_host_fragment_activity_main).currentDestination?.label
		} else {
			getString(R.string.app_name)
		}

		supportActionBar?.subtitle = if (repository.isReconnecting.value) {
			getString(R.string.reconnecting)
		} else {
			null
		}
	}
}
