package com.example.incidentreport.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incidentreport.R
import com.example.incidentreport.adapters.IncidentReportAdapter
import com.example.incidentreport.databinding.FragmentHomeBinding
import com.example.incidentreport.db.IncidentReportDatabase
import com.example.incidentreport.models.IncidentReport
import kotlinx.android.synthetic.main.fragment_pager.*
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
        if (db.latestReports(5).isNotEmpty()) {
            incidentReportAdapter.setData(db.latestReports(5))

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

        // Get pager & bnv from Pager Fragment
        val pager = requireActivity().pager
        val bnv = requireActivity().bottomNavigationView

        binding.btnViewAll.setOnClickListener {
            pager.currentItem = 1
            bnv.selectedItemId = R.id.miReports
        }

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
        db.addIncidentReport(
            IncidentReport(0,
                reporter = "Four",
                incidentType = "Firearms",
                dateTime = Calendar.getInstance().time,
                location = "Somewhere",
                missing = 0,
                injured = 2,
                fatalities = 0,
                description = "Something happened.",
                victims = listOf("Test test", "Some")
            )
        )
        db.addIncidentReport(
            IncidentReport(0,
                reporter = "Two",
                incidentType = "Explosion",
                dateTime = Calendar.getInstance().time,
                location = "Somewhere",
                missing = 0,
                injured = 2,
                fatalities = 0,
                description = "Something happened.",
                victims = listOf("Test test", "Some")
            )
        )
        db.addIncidentReport(
            IncidentReport(0,
                reporter = "Tester",
                incidentType = "Earthquake",
                dateTime = Calendar.getInstance().time,
                location = "Somewhere",
                missing = 0,
                injured = 2,
                fatalities = 0,
                description = "Something happened.",
                victims = listOf("Test test", "Some")
            )
        )
        db.addIncidentReport(
            IncidentReport(0,
                reporter = "Three",
                incidentType = "Flooding",
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