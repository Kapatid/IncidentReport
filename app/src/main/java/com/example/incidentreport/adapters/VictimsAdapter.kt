package com.example.incidentreport.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incidentreport.databinding.VictimRowLayoutBinding

class VictimsAdapter : RecyclerView.Adapter<VictimsAdapter.ViewHolder>() {

    private lateinit var oldVictimsList : List<String>

    class ViewHolder(val binding: VictimRowLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            VictimRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = oldVictimsList[position]
        val binding = holder.binding

        binding.tvRowVictim.text = item
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
    override fun getItemCount() = oldVictimsList.size

    fun setData(newVictimsList: List<String>) {
        oldVictimsList = newVictimsList
    }
}