package com.invariantlabs.fooddeliverynotifier.presentation

import android.animation.AnimatorInflater
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.invariantlabs.fooddeliverynotifier.domain.model.DeliveryStatus.*
import com.invariantlabs.fooddeliverynotifier.domain.model.Restaurant
import com.invariantlabs.fooddeliverynotifier.domain.model.isFavorite
import com.invariantlabs.fooddeliverynotifier.domain.model.isMonitored
import com.invariantlabs.fooddeliverynotifier.restaurants.R
import com.invariantlabs.fooddeliverynotifier.restaurants.databinding.ItemRestaurantBinding

class RestaurantsAdapter(private val callback: Callback) :
    ListAdapter<Restaurant, RestaurantsAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), callback)
    }

    class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var callback: Callback
        private lateinit var restaurant: Restaurant

        init {
            binding.root.setOnClickListener {
                callback.onClickRestaurant(restaurant)
            }
            binding.favorite.setOnClickListener {
                callback.onClickFavorite(restaurant)
            }
            binding.startMonitor.setOnClickListener {
                callback.onClickMonitor(restaurant)
            }
            AnimatorInflater.loadAnimator(binding.root.context, R.animator.alpha_animator)
                .apply {
                    setTarget(binding.currentlyMonitoringLabel)
                    start()
                }
        }

        fun bind(restaurant: Restaurant, callback: Callback) {
            this.restaurant = restaurant
            this.callback = callback
            binding.image.load(restaurant.imageUrl)
            binding.name.text = restaurant.name
            binding.deliveryIndicator.isSelected = restaurant.deliveryStatus == Online
            binding.deliveryIndicatorLabel.setText(
                when (restaurant.deliveryStatus) {
                    Online -> R.string.delivery_online
                    Offline -> R.string.delivery_offline_temporarily
                    NotInDeliveryHours -> R.string.delivery_offline_hours
                }
            )
            binding.currentlyMonitoringLabel.isVisible = restaurant.isMonitored
            binding.startMonitor.isVisible =
                restaurant.deliveryStatus != Online || restaurant.isMonitored
            binding.startMonitor.setText(if (restaurant.isMonitored) R.string.currently_monitored else R.string.start_monitor)
            binding.favorite.setImageResource(if (restaurant.isFavorite) R.drawable.ic_heart_24dp else R.drawable.ic_heart_outline_24dp)
        }
    }

    interface Callback {
        fun onClickRestaurant(restaurant: Restaurant)
        fun onClickFavorite(restaurant: Restaurant)
        fun onClickMonitor(restaurant: Restaurant)
    }
}

val diffCallback = object : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.slug == newItem.slug
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }
}
