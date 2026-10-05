package com.whitbread.premierinn.ciol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.databinding.ViewTitleBinding

class TitleSelectionListAdapter(
    private val titles: List<String>,
    private val onTitleSelected: (String) -> Unit
) :
    RecyclerView.Adapter<TitleSelectionListAdapter.TitleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val binding = ViewTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TitleViewHolder(binding)
    }

    override fun getItemCount() = titles.size

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        val title = titles[position]
        holder.bind(title, position, titles.size)
    }

    inner class TitleViewHolder(
        private val binding: ViewTitleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, position: Int, listSize: Int) {
            binding.title.text = title
            if (position == listSize - 1) {
                binding.divider.isVisible = false
            }
            binding.title.setOnClickListener {
                onTitleSelected.invoke(title)
            }
        }
    }
}
