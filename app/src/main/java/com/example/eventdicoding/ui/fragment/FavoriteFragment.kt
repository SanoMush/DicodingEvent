package com.example.eventdicoding.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.FavoriteEventRepository
import com.example.eventdicoding.ui.detail.DetailActivity
import com.example.eventdicoding.vmodel.FavoriteEventAdapter

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteEventAdapter: FavoriteEventAdapter
    private lateinit var messageTextView: TextView
    private lateinit var favoriteEventRepository: FavoriteEventRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_favorite)
        messageTextView = view.findViewById(R.id.message)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        favoriteEventAdapter = FavoriteEventAdapter(requireContext()) { favoriteEvent ->
            Log.d("FavoriteFragment", "Navigating to DetailActivity with: $favoriteEvent")
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", favoriteEvent)
            }
            startActivity(intent)
        }


        recyclerView.adapter = favoriteEventAdapter

        // Initialize repository
        favoriteEventRepository = FavoriteEventRepository(requireContext())

        // Load favorite events from Room Database
        loadFavoriteEvents()

        return view
    }

    private fun loadFavoriteEvents() {
        favoriteEventRepository.getAllFavoriteEvent().observe(viewLifecycleOwner, Observer { events ->
            favoriteEventAdapter.submitList(events)

            // Show message if there are no favorite events
            if (events.isEmpty()) {
                messageTextView.visibility = View.VISIBLE
            } else {
                messageTextView.visibility = View.GONE
            }
        })
    }
}
