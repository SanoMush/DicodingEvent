package com.example.eventdicoding.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.databinding.FragmentFinishedBinding
import com.example.eventdicoding.ui.detail.DetailActivity
import com.example.eventdicoding.vmodel.EventAdapter
import com.example.eventdicoding.vmodel.MainViewModel

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be accessed when it is null")

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: EventAdapter
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventAdapter(requireContext()) { clickedEvent ->
            val intent = Intent(requireContext(), DetailActivity::class.java)


            intent.putExtra(DetailActivity.EVENT_KEY, clickedEvent)

            startActivity(intent)
        }

        binding.recycleApiFinish.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleApiFinish.adapter = adapter

        val apiService = com.example.eventdicoding.retrofit.APIConfig.create()
        val repository = com.example.eventdicoding.data.response.EventRepository.getInstance(apiService)
        val factory = com.example.eventdicoding.vmodel.ViewModelFactory.getInstance(repository)
        mainViewModel = androidx.lifecycle.ViewModelProvider(this, factory)[com.example.eventdicoding.vmodel.MainViewModel::class.java]

        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is com.example.eventdicoding.data.response.Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is com.example.eventdicoding.data.response.Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(result.data)
                    }
                    is com.example.eventdicoding.data.response.Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        android.widget.Toast.makeText(requireContext(), result.error, android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(intent)
        }
        binding.recycleApiFinish.layoutManager = LinearLayoutManager(context)
        binding.recycleApiFinish.adapter = eventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
