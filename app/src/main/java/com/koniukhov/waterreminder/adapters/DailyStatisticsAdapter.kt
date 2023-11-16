package com.koniukhov.waterreminder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.dailywater.DailyWater
import com.koniukhov.waterreminder.data.drinkware.DrinkWareIcons
import com.koniukhov.waterreminder.databinding.DailyStatisticsItemBinding
import com.koniukhov.waterreminder.viewmodels.MainViewModel

class DailyStatisticsAdapter(private val context: Context, private val viewModel: MainViewModel) : ListAdapter<DailyWater, DailyStatisticsAdapter.DailyViewHolder>(DiffCallback) {

    class DailyViewHolder(var binding: DailyStatisticsItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: DailyWater, context: Context){
            binding.imageView.setImageResource(DrinkWareIcons.icons[item.iconName]!!)
            binding.timeText.text = item.time
            binding.volumeText.text = context.getString(R.string.volume_format, item.volume)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        return DailyViewHolder(DailyStatisticsItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context)

        holder.binding.delete.setOnClickListener{
            viewModel.deleteWater(item)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DailyWater>(){
        override fun areItemsTheSame(oldItem: DailyWater, newItem: DailyWater): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DailyWater, newItem: DailyWater): Boolean {
            return oldItem == newItem
        }

    }
}