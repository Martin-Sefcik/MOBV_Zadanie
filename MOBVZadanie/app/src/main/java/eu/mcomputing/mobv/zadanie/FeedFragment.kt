package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private lateinit var viewModel: FeedViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<BottomBar>(R.id.bottom_bar).setActive(BottomBar.FEED)

//        view.findViewById<Button>(R.id.othersProfileButton).apply {
//            setOnClickListener {
//                findNavController().navigate(R.id.feed_to_othersProfile)
//            }
//        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.feed_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val feedAdapter = FeedAdapter()
        recyclerView.adapter = feedAdapter
//        feedAdapter.updateItems(listOf(
//            MyItem(1, R.drawable.ic_profil,"Prvy"),
//            MyItem(2, R.drawable.ic_profil,"Druhy"),
//            MyItem(3, R.drawable.ic_profil,"Treti"),
//        ))


//        val newItems = mutableListOf<MyItem>()
//        for(i in 1..100){
//            newItems.add(MyItem(i,R.drawable.ic_profil,"novy text: $i"))
//        }
        //feedAdapter.updateItems(newItems)

        viewModel = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

        viewModel.feed_items.observe(viewLifecycleOwner){new_items->
            feedAdapter.updateItems(new_items)
        }

        view.findViewById<Button>(R.id.btn_generate).setOnClickListener {
            viewModel.updateItems()
        }
        //viewModel.updateItems(newItems)

    }
}