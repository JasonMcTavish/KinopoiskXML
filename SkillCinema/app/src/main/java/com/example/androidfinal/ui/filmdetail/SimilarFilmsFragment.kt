package com.example.androidfinal.ui.filmdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidfinal.R
import com.example.androidfinal.databinding.FragmentAllFilmsBinding
import com.example.androidfinal.ui.StateLoading
import com.example.androidfinal.ui.adapters.MyAdapterTypes
import com.example.androidfinal.ui.adapters.MyListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SimilarFilmsFragment: Fragment() {
    private var _binding: FragmentAllFilmsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SimilarFilmsViewModel by viewModels()
    private lateinit var adapter: MyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllFilmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: SimilarFilmsFragmentArgs by navArgs()
        viewModel.getSimilarFilms(args.filmId)

        binding.allFilmsCategoryTv.text = resources.getString(R.string.label_film_similar)
        binding.allFilmsToHomeBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        stateLoadingListener(args.filmId)       // set loading listener
        setAdapter()                            // set adapter
        setFilmList()                           // set film list
    }

    private fun setAdapter() {
        adapter = MyListAdapter(null, {}) { onClickFilm(it) }
        binding.allFilmsList.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.allFilmsList.adapter = adapter
    }

    private fun setFilmList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentFilmSimilar.collect {
                    adapter.submitList(it.map { similar -> MyAdapterTypes.ItemFilmSimilar(similar) })
                }
            }
        }
    }

    private fun stateLoadingListener(filmId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadCategoryState.collect { state ->
                    when (state) {
                        is StateLoading.Loading -> {
                            binding.progressGroup.isVisible = true
                            binding.loadingProgress.isVisible = true
                            binding.loadingRefreshBtn.isVisible = false
                            binding.allFilmsList.isVisible = false
                        }
                        is StateLoading.Success -> {
                            binding.progressGroup.isVisible = false
                            binding.allFilmsList.isVisible = true
                        }
                        else -> {
                            binding.progressGroup.isVisible = true
                            binding.loadingProgress.isVisible = false
                            binding.loadingRefreshBtn.isVisible = true
                            binding.allFilmsList.isVisible = false
                            binding.loadingRefreshBtn.setOnClickListener { viewModel.getSimilarFilms(filmId) }
                        }
                    }
                }
            }
        }
    }

    private fun onClickFilm(filmId: Int) {
        val action =
            SimilarFilmsFragmentDirections.actionFragmentSimilarFilmsToFragmentFilmDetail(filmId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}