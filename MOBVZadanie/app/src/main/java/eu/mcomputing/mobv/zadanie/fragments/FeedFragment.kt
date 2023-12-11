package eu.mcomputing.mobv.zadanie.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.mcomputing.mobv.zadanie.widgets.BottomBar
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.adapters.FeedAdapter
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.databinding.FragmentFeedBinding
import eu.mcomputing.mobv.zadanie.viewmodels.OtherProfileViewModel
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private lateinit var viewModel: FeedViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var otherProfileViewModel: OtherProfileViewModel
    private lateinit var binding: FragmentFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]

        profileViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

        otherProfileViewModel =
            ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OtherProfileViewModel(DataRepository.getInstance(requireContext())) as T
                }
            })[OtherProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val sharing = PreferenceData.getInstance().getSharing(requireContext())

            bnd.bottomBar.setActive(BottomBar.FEED)

            if (!sharing) {
                showRecordingDisabledAlert(view)
            }

            bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)
            val feedAdapter = FeedAdapter(otherProfileViewModel)
            bnd.feedRecyclerview.adapter = feedAdapter

            // Pozorovanie zmeny hodnoty
            viewModel.feed_items.observe(viewLifecycleOwner) { items ->
                Log.d("FeedFragment", "nove hodnoty $items")
                feedAdapter.updateItems(items ?: emptyList())
            }


            bnd.pullRefresh.setOnRefreshListener {
                viewModel.updateItems()
            }
            viewModel.loading.observe(viewLifecycleOwner) {
                bnd.pullRefresh.isRefreshing = it
            }
        }

    }

    private fun showRecordingDisabledAlert(view: View) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Sharing Disabled")
            .setMessage("Sharing is currently disabled. Do you want to enable it?")
            .setPositiveButton("Enable") { _, _ ->
                view.findNavController().navigate(R.id.feed_to_profile)
//                PreferenceData.getInstance().putSharing(requireContext(), true)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}