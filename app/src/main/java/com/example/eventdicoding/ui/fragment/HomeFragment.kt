package com.example.eventdicoding.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.data.response.EventRepository
import com.example.eventdicoding.data.response.Result
import com.example.eventdicoding.databinding.FragmentHomeBinding
import com.example.eventdicoding.retrofit.APIConfig
import com.example.eventdicoding.ui.detail.DetailActivity
import com.example.eventdicoding.vmodel.EventAdapter
import com.example.eventdicoding.vmodel.MainViewModel
import com.example.eventdicoding.vmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var activeAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = APIConfig.create()
        val repository = EventRepository.getInstance(apiService)
        val factory = ViewModelFactory.getInstance(repository)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        activeAdapter = EventAdapter(requireContext()) { clickedEvent ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EVENT_KEY, clickedEvent)
            startActivity(intent)
        }
        binding.recyclerViewActiveEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActiveEvents.adapter = activeAdapter

        finishedAdapter = EventAdapter(requireContext()) { clickedEvent ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EVENT_KEY, clickedEvent)
            startActivity(intent)
        }
        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFinishedEvents.adapter = finishedAdapter

        mainViewModel.activeEvents.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        activeAdapter.submitList(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        finishedAdapter.submitList(result.data.take(5))
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    mainViewModel.searchEvents(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    binding.tvActiveEvents.text = "Upcoming Events"
                    binding.recyclerViewFinishedEvents.visibility = View.VISIBLE
                    binding.tvFinishedEvents.visibility = View.VISIBLE
                    mainViewModel.fetchEvents(1)
                }
                return false
            }
        })

        mainViewModel.searchResults.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvFinishedEvents.visibility = View.GONE
                        binding.recyclerViewFinishedEvents.visibility = View.GONE
                        binding.tvActiveEvents.text = "Hasil Pencarian"
                        activeAdapter.submitList(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}