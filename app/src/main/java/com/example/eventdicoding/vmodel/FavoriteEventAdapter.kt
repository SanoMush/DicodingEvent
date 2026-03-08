package com.example.eventdicoding.vmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.data.local.FavoriteEventEntity
import com.example.eventdicoding.databinding.ItemRowImageBinding

class FavoriteEventAdapter(private val onItemClick: (FavoriteEventEntity) -> Unit) :
    ListAdapter<FavoriteEventEntity, FavoriteEventAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    class MyViewHolder(private val binding: ItemRowImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: FavoriteEventEntity) {
            binding.itemText.text = event.name

            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(binding.itemImage)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEventEntity>() {
            override fun areItemsTheSame(oldItem: FavoriteEventEntity, newItem: FavoriteEventEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavoriteEventEntity, newItem: FavoriteEventEntity): Boolean {
                return oldItem.name == newItem.name && oldItem.imageLogo == newItem.imageLogo
            }
        }
    }
}