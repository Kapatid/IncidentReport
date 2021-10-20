package com.example.incidentreport.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.incidentreport.databinding.IncidentReportRowLayoutBinding
import com.example.incidentreport.models.IncidentReport
import com.example.incidentreport.ui.main.PagerFragmentDirections
import com.example.incidentreport.ui.main.ReportsFragment
import com.example.incidentreport.utils.IncidentReportDiffUtil
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class IncidentReportAdapter(
    private val fragment: Fragment,
    private val context: Context
) : RecyclerView.Adapter<IncidentReportAdapter.ViewHolder>() {

    private var oldIncidentReportList = emptyList<IncidentReport>()
    private lateinit var incidentReportFilterList: List<IncidentReport>

    /**
     * A [ViewHolder] describes an item view and metadata about
     * its place within the [RecyclerView].
     */
    class ViewHolder(val binding: IncidentReportRowLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the item views which is designed in the XML layout file.
     *
     * Create a new [ViewHolder] and initializes some private fields
     * to be used by [RecyclerView].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            IncidentReportRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Binds each item in the [ArrayList] to a view.
     *
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     * This new [ViewHolder] should be constructed with a new [View] that can represent the items
     * of the given type. You can either create a new [View] manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = incidentReportFilterList[position]
        val binding = holder.binding

        binding.tvIncidentType.text = item.incidentType
        (SimpleDateFormat("hh:mm a · MMM d y", Locale.getDefault())
            .format(item.dateTime) + " · " + item.reporter).also {
            binding.tvhRowIncidentReport.text = it }

        binding.rowIncidentReport.setOnClickListener {
            val sharedPref =
                fragment.activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            with (sharedPref.edit()) {
                putString("reportToView", Gson().toJson(item))
                apply()
            }

            fragment.findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToReportDetailsFragment()
            )
        }
    }

    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
    override fun getItemCount() = incidentReportFilterList.size

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                incidentReportFilterList = if (charSearch.isEmpty()) {
                    oldIncidentReportList
                } else {
                    val resultList = ArrayList<IncidentReport>()

                    for (row in oldIncidentReportList) {
                        if (row.reporter.lowercase().contains(charSearch.lowercase()) ||
                            row.incidentType.lowercase().contains(charSearch.lowercase())) {
                            resultList.add(row)
                        }
                    }

                    resultList
                }

                val filterResults = FilterResults()

                filterResults.values = incidentReportFilterList

                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                setFilterData(results?.values as List<IncidentReport>)
            }
        }
    }

    fun setData(newIncidentReportList: List<IncidentReport>) {
        val diffUtil = IncidentReportDiffUtil(oldIncidentReportList, newIncidentReportList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldIncidentReportList = newIncidentReportList

        incidentReportFilterList = oldIncidentReportList

        diffResults.dispatchUpdatesTo(this)
    }

    fun setFilterData(newIncidentReportList: List<IncidentReport>) {
        val diffUtil = IncidentReportDiffUtil(oldIncidentReportList, newIncidentReportList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        incidentReportFilterList = newIncidentReportList

        diffResults.dispatchUpdatesTo(this)
        notifyItemRangeChanged(0, incidentReportFilterList.size)
    }
}