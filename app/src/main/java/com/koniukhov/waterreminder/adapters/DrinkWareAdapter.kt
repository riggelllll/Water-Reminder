package com.koniukhov.waterreminder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.drinkware.DrinkWare
import com.koniukhov.waterreminder.data.drinkware.DrinkWareIcons
import com.koniukhov.waterreminder.databinding.DrinkWareItemBinding

class DrinkWareAdapter(private val context: Context, private val onClick: (DrinkWare) -> Unit) : ListAdapter<DrinkWare, DrinkWareAdapter.DrinkWareViewHolder>(DiffCallback) {

    class DrinkWareViewHolder(private var binding: DrinkWareItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: DrinkWare, context: Context){
            binding.icon.setImageResource(DrinkWareIcons.icons[item.iconName]!!)
            binding.text.text = context.getString(R.string.volume_format, item.volume)
            if (item.isActive == 1){
                binding.rootLayout.setBackgroundResource(R.drawable.rounded_active_background)
            }else{
                binding.rootLayout.setBackgroundResource(0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkWareViewHolder {
        return DrinkWareViewHolder(DrinkWareItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DrinkWareViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context)
        holder.itemView.setOnClickListener{
            onClick(item.copy())
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DrinkWare>() {
        override fun areItemsTheSame(oldItem: DrinkWare, newItem: DrinkWare): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DrinkWare, newItem: DrinkWare): Boolean {
            return oldItem == newItem
        }
    }
}