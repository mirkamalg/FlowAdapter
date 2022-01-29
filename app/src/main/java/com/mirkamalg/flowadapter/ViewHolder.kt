package com.mirkamalg.flowadapter

import androidx.recyclerview.widget.RecyclerView
import com.mirkamalg.flowadapter.databinding.ItemSampleBinding

/**
 * Created by Mirkamal on 29 January 2022
 */

class ViewHolder(private val binding: ItemSampleBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: SampleData) {
        binding.apply {
            textViewTitle.text = data.title
            textViewBody.text = data.body
        }
    }
}