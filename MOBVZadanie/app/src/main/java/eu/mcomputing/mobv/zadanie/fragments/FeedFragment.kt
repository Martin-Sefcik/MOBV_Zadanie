package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.mcomputing.mobv.zadanie.widgets.BottomBar
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.adapters.FeedAdapter
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentFeedBinding

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private lateinit var viewModel: FeedViewModel
    private var binding: FragmentFeedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[FeedViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFeedBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.FEED)

            bnd.feedRecyclerview.layoutManager = LinearLayoutManager(context)
            val feedAdapter = FeedAdapter()
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

//            bnd.btnGenerate.setOnClickListener {
//                viewModel.updateItems()
//            }
        }

//        view.findViewById<BottomBar>(R.id.bottom_bar).setActive(BottomBar.FEED)

//        val recyclerView = view.findViewById<RecyclerView>(R.id.feed_recyclerview)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        val feedAdapter = FeedAdapter()
//        recyclerView.adapter = feedAdapter


//        val newItems = mutableListOf<MyItem>()
//        for(i in 1..100){
//            newItems.add(MyItem(i,R.drawable.ic_profil,"novy text: $i"))
//        }
        //feedAdapter.updateItems(newItems)

//        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

//        viewModel.feed_items.observe(viewLifecycleOwner){new_items->
//            feedAdapter.updateItems(new_items)
//        }

//        view.findViewById<Button>(R.id.btn_generate).setOnClickListener {
//            viewModel.updateItems()
//        }
        //viewModel.updateItems(newItems)

    }
}