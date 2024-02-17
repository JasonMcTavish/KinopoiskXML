package com.example.androidfinal.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidfinal.R
import com.example.androidfinal.databinding.FragmentSearchBinding
import com.example.androidfinal.entity.ParamsFilterFilm
import com.example.androidfinal.ui.adapters.MyAdapterTypes
import com.example.androidfinal.ui.adapters.MyPagingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels()

    private val filmAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MyPagingAdapter { onFilmClick(it) }
    }
    private val personAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MyPagingAdapter { onPersonClick(it) }
    }
    private val myMergeAdapter = ConcatAdapter(personAdapter, filmAdapter)

    private var isEditFocused = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()                // set adapter
        setSearchString()           // set search string
        getFilmList()               // set film list

        binding.searchFilterBtn.setOnClickListener { setSearchMenu(it) }
    }

    private fun onFilmClick(filmId: Int) {
        val action = SearchFragmentDirections.actionFragmentSearchToFragmentFilmDetail(filmId)
        findNavController().navigate(action)
    }

    private fun onPersonClick(personId: Int) {
        val action = SearchFragmentDirections.actionFragmentSearchToFragmentPersonDetail(personId)
        findNavController().navigate(action)
    }

    private fun setAdapter() {
        filmAdapter.addLoadStateListener { state ->
            val currentState = state.refresh
            binding.searchFilmList.isVisible = currentState != LoadState.Loading
            binding.loadingProgress.isVisible = currentState == LoadState.Loading
            binding.searchProgressText.isVisible = currentState != LoadState.Loading
            binding.searchProgressImage.isVisible = currentState == LoadState.Loading
            when (currentState) {
                is LoadState.Loading -> {
                    binding.searchFilmList.isVisible = false
                    binding.searchProgressGroup.isVisible = true
                    binding.searchProgressText.isVisible = false
                    binding.searchProgressImage.isVisible = true
                }
                is LoadState.NotLoading -> {
                    binding.searchFilmList.isVisible = true
                    binding.loadingProgress.isVisible = false
                    binding.searchProgressText.isVisible = false
                    binding.searchProgressImage.isVisible = false

                }
                else -> {
                    binding.searchFilmList.isVisible = false
                    binding.loadingProgress.isVisible = false
                    binding.searchProgressText.isVisible = true
                    binding.searchProgressImage.isVisible = true
                }
            }
        }
        binding.searchFilmList.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.searchFilmList.adapter = myMergeAdapter
    }

    private fun setSearchString() {
        binding.searchMyField.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.searchGroup.background = if (hasFocus) {
                isEditFocused = true
                ResourcesCompat.getDrawable(
                    resources, R.drawable.search_input_field_select, null
                )
            } else {
                ResourcesCompat.getDrawable(resources, R.drawable.search_input_field, null)
            }
        }

        binding.searchClearBtn.setOnClickListener {
            binding.searchMyField.text?.clear()
            val newFilter = viewModel.getFiltersFull().copy(keyword = "")
            viewModel.updateFiltersFull(filterFilm = newFilter)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.searchClearBtn.isVisible = viewModel.getFiltersFull().keyword.isNotEmpty()
                binding.searchMyField.textChanges()
                    .collect { state ->
                        val keyword = binding.searchMyField.text.toString()
                        if (state) {
                            val newFilter = viewModel.getFiltersFull().copy(keyword = keyword)
                            viewModel.updateFiltersFull(filterFilm = newFilter)
                        }
                    }
            }
        }
    }

    private fun getFilmList() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filterFlow.collect {
                    personAdapter.refresh()
                    filmAdapter.refresh()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newFilms.collect {
                    filmAdapter.submitData(it.map { film -> MyAdapterTypes.ItemSearchFilms(film) })
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.persons.collect {
                    personAdapter.submitData(it.map { person -> MyAdapterTypes.ItemSearchPersons(person) })
                }
            }
        }
    }

    private fun setSearchMenu(view: View) {
        val popUpMenu = PopupMenu(requireContext(), view)
        popUpMenu.inflate(R.menu.search_menu)

        popUpMenu
            .setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search_settings_set -> {
                        findNavController()
                            .navigate(R.id.action_fragmentSearch_to_fragmentSearchSettings)
                        true
                    }
                    R.id.search_filters_clear -> {
                        viewModel.updateFiltersFull(ParamsFilterFilm())
                        binding.searchMyField.text?.clear()
                        Toast.makeText(
                            requireActivity().applicationContext,
                            getString(R.string.search_clear_message),
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    else -> false
                }
            }

        popUpMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun EditText.textChanges(): Flow<Boolean> {
        return callbackFlow {
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    trySend(false)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.searchClearBtn.isVisible = !s.isNullOrEmpty()
                    viewLifecycleOwner.lifecycleScope.launch {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            trySend(false)
                            delay(500)
                            trySend(true)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            }
            addTextChangedListener(watcher)
            awaitClose { removeTextChangedListener(watcher) }
        }.onStart { emit(false) }
    }
}