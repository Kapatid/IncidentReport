package com.example.incidentreport.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.incidentreport.models.IncidentReport

class IncidentReportDiffUtil(
    private val oldIncidentReportList: List<IncidentReport>,
    private val newIncidentReportList: List<IncidentReport>
): DiffUtil.Callback() {

    override fun getOldListSize() = oldIncidentReportList.size
    override fun getNewListSize() = newIncidentReportList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldIncidentReportList[oldItemPosition].id == newIncidentReportList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldIncidentReportList[oldItemPosition].id !=
                    newIncidentReportList[newItemPosition].id -> false
            oldIncidentReportList[oldItemPosition].reporter !=
                    newIncidentReportList[newItemPosition].reporter -> false
            oldIncidentReportList[oldItemPosition].incidentType !=
                    newIncidentReportList[newItemPosition].incidentType -> false
            oldIncidentReportList[oldItemPosition].dateTime !=
                    newIncidentReportList[newItemPosition].dateTime -> false
            oldIncidentReportList[oldItemPosition].location !=
                    newIncidentReportList[newItemPosition].location -> false
            oldIncidentReportList[oldItemPosition].victims !=
                    newIncidentReportList[newItemPosition].victims -> false
            oldIncidentReportList[oldItemPosition].description !=
                    newIncidentReportList[newItemPosition].description -> false
            oldIncidentReportList[oldItemPosition].updatedAt !=
                    newIncidentReportList[newItemPosition].updatedAt -> false
            oldIncidentReportList[oldItemPosition].createdAt !=
                    newIncidentReportList[newItemPosition].createdAt -> false
            else -> true
        }
    }
}