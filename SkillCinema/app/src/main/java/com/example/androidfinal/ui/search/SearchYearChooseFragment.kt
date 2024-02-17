package com.example.androidfinal.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.androidfinal.databinding.FragmentSearchYearChooseBinding


class SearchYearChooseFragment: Fragment() {
    private var _binding: FragmentSearchYearChooseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchYearChooseBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rangeFrom.setDateListener = { year ->
            viewModel.updateFiltersFull(viewModel.getFiltersFull().copy(yearFrom = year))
        }
        binding.rangeTo.setDateListener = { year ->
            viewModel.updateFiltersFull(viewModel.getFiltersFull().copy(yearTo = year))
        }

        binding.searchSettingsBackBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnChooseRangeYears.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}