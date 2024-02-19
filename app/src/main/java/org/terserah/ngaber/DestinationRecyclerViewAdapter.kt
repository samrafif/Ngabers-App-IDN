package org.terserah.ngaber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DestinationAdapter(private val destinations: List<Destination>) :
    RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destination, parent, false)
        return DestinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val destination = destinations[position]
        // Bind destination data to the ViewHolder
        // Example: holder.bind(destination)
    }

    override fun getItemCount(): Int {
        return destinations.size
    }

    inner class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder implementation
        // Example: Bind destination data to views
    }
}
