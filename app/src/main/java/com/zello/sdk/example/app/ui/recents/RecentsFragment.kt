package com.zello.sdk.example.app.ui.recents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zello.sdk.ZelloGroupConversation
import com.zello.sdk.ZelloRecentEntry
import com.zello.sdk.example.app.R
import com.zello.sdk.example.app.databinding.FragmentRecentsBinding
import com.zello.sdk.example.app.utils.TimeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentsFragment : Fragment() {

	private val viewModel: RecentsViewModel by viewModels()

	private var _binding: FragmentRecentsBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentRecentsBinding.inflate(inflater, container, false)
		val root: View = binding.root

		val composeView: ComposeView = binding.composeView

		composeView.setContent {
			Recents()
		}

		return root
	}

	@Composable
	private fun Recents() {
		val recents = viewModel.recents.observeAsState().value ?: emptyList()
		LazyColumn(
			modifier = Modifier.fillMaxSize()
		) {
			items(recents.size) { index ->
				if (index != 0) {
					Spacer(modifier = Modifier.height(8.dp))
				}
				Recent(recent = recents[index])
			}
		}
	}

	@Composable
	private fun Recent(recent: ZelloRecentEntry) {
		val contactName = when (val contact = recent.contact) {
			is ZelloGroupConversation -> contact.displayName
			else -> contact.name
		}
		val title = if (recent.channelUser != null) "${recent.channelUser?.displayName} : $contactName" else contactName
		Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
			Image(
				painter = painterResource(id = if (recent.incoming) R.drawable.ic_incoming_24dp else R.drawable.ic_outgoing_24dp),
				contentDescription = "Incoming or outgoing message"
			)
			Spacer(modifier = Modifier.width(8.dp))
			Column {
				Text(text = title)
				Text(text = recent.type.toString())
				Text(text = TimeUtils.timestampToString(recent.timestamp))
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
