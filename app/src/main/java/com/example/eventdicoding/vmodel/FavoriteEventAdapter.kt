package com.example.eventdicoding.vmodel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.FavoriteEventEntity

class FavoriteEventAdapter(
    private val context: Context,
    private val itemClickListener: (FavoriteEventEntity) -> Unit
) : RecyclerView.Adapter<FavoriteEventAdapter.FavoriteEventViewHolder>() {

    private var favoriteEvents: List<FavoriteEventEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_row_image, parent, false) // Pastikan layout ini sesuai
        return FavoriteEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        val event = favoriteEvents[position]
        holder.itemText.text = event.name

        Glide.with(context)
            .load(event.imageLogo)
            .into(holder.itemImage)

        holder.itemView.setOnClickListener {
            itemClickListener(event) // Memanggil listener saat item diklik
        }
    }

    override fun getItemCount(): Int = favoriteEvents.size

    fun submitList(newEvents: List<FavoriteEventEntity>) {
        val diffCallback = FavoriteEventsDiffCallback(favoriteEvents, newEvents)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        favoriteEvents = newEvents
        diffResult.dispatchUpdatesTo(this)
    }

    class FavoriteEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.item_Image)
        val itemText: TextView = itemView.findViewById(R.id.item_Text)
    }

    class FavoriteEventsDiffCallback(
        private val oldList: List<FavoriteEventEntity>,
        private val newList: List<FavoriteEventEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
