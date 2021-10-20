package com.example.incidentreport.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.incidentreport.databinding.EditVictimRowLayoutBinding
import com.example.incidentreport.utils.IncidentVictimsDiffUtil

class EditableVictimsAdapter : RecyclerView.Adapter<EditableVictimsAdapter.ViewHolder>() {

    private var oldList = emptyList<String>().toMutableList()

    class ViewHolder(val binding: EditVictimRowLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EditVictimRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = oldList[position]
        val binding = holder.binding

        binding.etVictimName.setText(item)
        binding.etVictimName.doAfterTextChanged {
            oldList[position] = it.toString()
        }

        binding.btnClearVictim.setOnClickListener {
            oldList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(0, oldList.size)
        }
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
    override fun getItemCount() = oldList.size

    fun setData(newList: MutableList<String>) {
        val diffUtil = IncidentVictimsDiffUtil(oldList, newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)

        oldList = newList

        diffResults.dispatchUpdatesTo(this)
    }

    fun getVictims(): MutableList<String> {
        return oldList
    }
}