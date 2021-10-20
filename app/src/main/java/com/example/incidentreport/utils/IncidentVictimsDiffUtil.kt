package com.example.incidentreport.utils

import androidx.recyclerview.widget.DiffUtil

class IncidentVictimsDiffUtil(
    private val oldList: List<String>,
    private val newList: List<String>,
): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition] != newList[newItemPosition] -> false
            else -> true
        }
    }
}