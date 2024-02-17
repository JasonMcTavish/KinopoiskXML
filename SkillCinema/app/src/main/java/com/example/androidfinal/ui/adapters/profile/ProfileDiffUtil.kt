package com.example.androidfinal.ui.adapters.profile

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class ProfileDiffUtil : DiffUtil.ItemCallback<ProfileAdapterTypes>() {
    override fun areItemsTheSame(oldItem: ProfileAdapterTypes, newItem: ProfileAdapterTypes) =
        oldItem == newItem

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: ProfileAdapterTypes,
        newItem: ProfileAdapterTypes
    ) = oldItem == newItem
}