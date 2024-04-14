package com.example.androidfinal.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfinal.R
import com.example.androidfinal.app.prepareToShow
import com.example.androidfinal.databinding.CentralSheetBinding
import com.example.androidfinal.databinding.FragmentProfileBinding
import com.example.androidfinal.ui.adapters.profile.CollectionDB
import com.example.androidfinal.ui.adapters.profile.ProfileAdapter
import com.example.androidfinal.ui.adapters.profile.ProfileAdapterTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterFilmsViewed: ProfileAdapter
    private lateinit var adapterFilmsCache: ProfileAdapter
    private lateinit var adapterCollections: ProfileAdapter

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapters()
        setCollectionsList()

        binding.profileGroupAddCollection.setOnClickListener {
            createNewCollectionDialog(requireContext())
        }
    }

    private fun setAdapters() {
        adapterFilmsViewed =
            ProfileAdapter(COLLECTION_SIZE, { onClickClearHistory("VIEWED") }, { onFilmClick(it) })
        binding.profileListViewed.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.profileListViewed.adapter = adapterFilmsViewed

        adapterFilmsCache =
            ProfileAdapter(COLLECTION_SIZE, { onClickClearHistory("CACHE") }, { onFilmClick(it) })
        binding.profileListFavorite.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.profileListFavorite.adapter = adapterFilmsCache

        adapterCollections = ProfileAdapter(COLLECTION_SIZE, {}, {})
        binding.profileListCollections.layoutManager =
            GridLayoutManager(
                requireActivity().applicationContext,
                2,
                GridLayoutManager.HORIZONTAL,
                false
            )
        binding.profileListCollections.adapter = adapterCollections
    }

    private fun onClickClearHistory(collectionName: String) {
        viewModel.clearCacheCollection(collectionName)
    }

    private fun setCollectionsList() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmsViewed.collect { films ->
                    if (films.isNotEmpty()) {
                        binding.profileLabelViewed.isVisible = true
                        binding.profileListViewed.isVisible = true
                        adapterFilmsViewed.submitList(
                            films.map { ProfileAdapterTypes.ItemDB(it) }
                                .prepareToShow(COLLECTION_SIZE)
                        )
                    } else {
                        binding.profileLabelViewed.isVisible = false
                        binding.profileListViewed.isVisible = false
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFilms.collect { films ->
                    if (films.isNotEmpty()) {
                        binding.profileLabelFavorite.isVisible = true
                        binding.profileListFavorite.isVisible = true
                        adapterFilmsCache.submitList(
                            films.map { ProfileAdapterTypes.ItemDB(it) }
                                .prepareToShow(COLLECTION_SIZE)
                        )
                    } else {
                        binding.profileLabelFavorite.isVisible = false
                        binding.profileListFavorite.isVisible = false
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectionsList.collect { collections ->
                    if (collections.isNotEmpty()) {
                        val newCollectionList = mutableListOf<ProfileAdapterTypes.ItemCollection>()
                        collections.forEach {
                            val icon = when (it.collectionName) {
                                getString(R.string.profile_collection_name_favorite) ->
                                    R.drawable.ic_favorite
                                getString(R.string.profile_collection_name_bookmark) ->
                                    R.drawable.ic_bookmark
                                else ->
                                    R.drawable.ic_user_collection
                            }
                            newCollectionList.add(ProfileAdapterTypes.ItemCollection(
                                CollectionDB(it.collectionName, it.size, icon)
                            ))
                        }
                        adapterCollections.submitList(newCollectionList.toList())
                    }
                }
            }
        }
    }

    private fun createNewCollectionDialog(context: Context) {
        val addDialog = Dialog(requireActivity())
        val addBinding = CentralSheetBinding.inflate(layoutInflater)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(addBinding.root)
        addDialog.show()
        addDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addDialog.window?.setGravity(Gravity.CENTER)
        addDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val editTextCollectionName = addBinding.editText
        val btnAddCollection = addBinding.buttonSave


        btnAddCollection.setOnClickListener {
            val text = editTextCollectionName.text.toString()
            viewModel.addNewCollection(text)
            Toast
                .makeText(context, "Добавлена новая коллекция: $text", Toast.LENGTH_SHORT)
                .show()
            addDialog.dismiss()
        }
        addDialog.show()
    }

    private fun onFilmClick(filmId: Int) {
        val action =
            ProfileFragmentDirections.actionFragmentProfileToFragmentFilmDetail(filmId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val COLLECTION_SIZE = 10
    }
}