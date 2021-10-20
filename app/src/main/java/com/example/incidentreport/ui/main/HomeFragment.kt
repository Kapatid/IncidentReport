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
import com.example.incidentreport.databinding.FragmentHomeBinding
import com.example.incidentreport.db.IncidentReportDatabase
import com.example.incidentreport.models.IncidentReport
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val incidentReportAdapter by lazy { IncidentReportAdapter(this, requireContext()) }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val db = IncidentReportDatabase(requireContext())

        // Populate database for testing
        if (db.getIncidentReports().isEmpty()) {
            reportFactory()
        }

        // Populate employees RecyclerView
        if (db.latestReports().isNotEmpty()) {
            incidentReportAdapter.setData(db.latestReports())

            binding.rvLatestReports.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE

            binding.rvLatestReports.layoutManager = LinearLayoutManager(requireContext())
            binding.rvLatestReports.adapter = incidentReportAdapter
        } else {
            binding.rvLatestReports.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }

        binding.tvTodayReports.text = db.todayReportSum().toString()
        binding.tvInjured.text = db.injuredSum().toString()
        binding.tvMissing.text = db.missingSum().toString()
        binding.tvFatalities.text = db.fatalitiesSum().toString()

        return binding.root
    }

    private fun reportFactory() {
        val db = IncidentReportDatabase(requireContext())

        db.addIncidentReport(
            IncidentReport(0,
                reporter = "One",
                incidentType = "Earthquake",
                dateTime = Calendar.getInstance().time,
                location = "Somewhere",
                missing = 1,
                injured = 0,
                fatalities = 0,
                description = "Something happened.",
                victims = listOf("One")
            )
        )
        db.addIncidentReport(
            IncidentReport(0,
                reporter = "Two",
                incidentType = "Car accident",
                dateTime = Calendar.getInstance().time,
                location = "Somewhere",
                missing = 0,
                injured = 2,
                fatalities = 0,
                description = "Something happened.",
                victims = listOf("Test test", "Some")
            )
        )
    }
}