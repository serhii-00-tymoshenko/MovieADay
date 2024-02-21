package com.awesomepetprojects.movieaday.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesomepetprojects.movieaday.databinding.FragmentHomeBinding
import com.awesomepetprojects.movieaday.ui.home.adapters.MovieLoadStateAdapter
import com.awesomepetprojects.movieaday.ui.home.adapters.MoviesAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by inject<HomeViewModel>()

    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var movieLoadStateAdapter: MovieLoadStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclers(requireActivity())
        initObservers()
        setListeners()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.getTopRatedMovies().collectLatest { data ->
                moviesAdapter.submitData(data)
            }
        }
    }

    private fun setupRecyclers(activity: FragmentActivity) {
        initAdapters(activity)

        binding.apply {
            recyclerMovies.apply {
                adapter = moviesAdapter.withLoadStateFooter(
                    footer = movieLoadStateAdapter
                )
                layoutManager = LinearLayoutManager(activity)
            }
        }
    }

    private fun initAdapters(activity: FragmentActivity) {
        moviesAdapter = MoviesAdapter { movie ->

        }
        movieLoadStateAdapter = MovieLoadStateAdapter {
            moviesAdapter.retry()
        }
    }

    private fun setListeners() {
        binding.apply {
            refresh.setOnClickListener {
                viewModel.refresh()
                moviesAdapter.refresh()
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}