package com.example.androidfinal.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.androidfinal.R
import com.example.androidfinal.data.CategoriesFilms
import com.example.androidfinal.databinding.FragmentHomeBinding
import com.example.androidfinal.ui.StateLoading
import com.example.androidfinal.ui.adapters.CategoryAdapter
import com.example.androidfinal.ui.allfilms.AllFilmsFragment



@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(20,{onClickShoAllButton(it)},{onClickFilm(it)})
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateLoadingListener()              // set loading listener
        getCategories()                     // set film list by categories
        binding.categoryList.adapter=categoryAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homePageList.collect {
                    categoryAdapter.update(it)
                }
            }
        }
    }

    private fun onClickFilm(filmId: Int) {
        Log.d(TAG, "onClickFilm: filmId = $filmId")
        val action = HomeFragmentDirections.actionFragmentHomeToFragmentFilmDetail(filmId)
        findNavController().navigate(action)
    }

    private fun onClickShoAllButton(category: CategoriesFilms) {
        findNavController().navigate(
            R.id.action_fragmentHome_to_fragmentAllFilms,
            bundleOf(AllFilmsFragment.KEY_FILM_CATEGORY to category)
        )
    }

    private fun stateLoadingListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadCategoryState.collect { state ->
                    when (state) {
                        is StateLoading.Loading -> {
                            binding.progressGroup.isVisible = true
                            binding.loadingProgress.isVisible = true
                            binding.loadingRefreshBtn.isVisible = false
                            binding.categoryList.isVisible = false
                        }
                        is StateLoading.Success -> {
                            binding.progressGroup.isVisible = false
                            binding.categoryList.isVisible = true
                        }
                        else -> {
                            binding.progressGroup.isVisible = true
                            binding.loadingProgress.isVisible = false
                            binding.loadingRefreshBtn.isVisible = true
                            binding.categoryList.isVisible = false
                            binding.loadingRefreshBtn.setOnClickListener { viewModel.getFilmsByCategories() }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "TAGa"
    }
}