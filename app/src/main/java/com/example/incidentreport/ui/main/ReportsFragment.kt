package com.example.incidentreport.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incidentreport.R
import com.example.incidentreport.adapters.IncidentReportAdapter
import com.example.incidentreport.databinding.FragmentReportsBinding
import com.example.incidentreport.db.IncidentReportDatabase

class ReportsFragment : Fragment(R.layout.fragment_reports) {
    private val incidentReportAdapter by lazy { IncidentReportAdapter(this, requireContext()) }

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout for this fragment
        _binding = FragmentReportsBinding.inflate(inflater, container, false)

        // Populate employees RecyclerView
        val db = IncidentReportDatabase(requireContext())
        val incidentReports = db.latestReports(0)
        if (incidentReports.isNotEmpty()) {
            incidentReportAdapter.setData(incidentReports)

            binding.rvReports.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE

            binding.rvReports.layoutManager = LinearLayoutManager(requireContext())
            binding.rvReports.adapter = incidentReportAdapter
        } else {
            binding.rvReports.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }

        // Employees SearchView
        binding.searchIncidentReport.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                incidentReportAdapter.getFilter().filter(query)
                return false
            }
        })

        return binding.root
    }
}