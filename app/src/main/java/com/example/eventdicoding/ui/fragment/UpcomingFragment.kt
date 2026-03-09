package com.example.eventdicoding.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.data.response.EventRepository
import com.example.eventdicoding.data.response.Result
import com.example.eventdicoding.databinding.FragmentUpcomingBinding
import com.example.eventdicoding.retrofit.APIConfig
import com.example.eventdicoding.vmodel.EventAdapter
import com.example.eventdicoding.vmodel.MainViewModel
import com.example.eventdicoding.vmodel.ViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = EventAdapter(requireContext()) { clickedEvent ->
            val intent = android.content.Intent(requireContext(), com.example.eventdicoding.ui.detail.DetailActivity::class.java)
            intent.putExtra("event", clickedEvent)
            startActivity(intent)
        }

        binding.recycleApiUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleApiUpcoming.adapter = adapter

        val apiService = APIConfig.create()
        val repository = EventRepository.getInstance(apiService)
        val factory = ViewModelFactory.getInstance(repository)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        mainViewModel.activeEvents.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(result.data)
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