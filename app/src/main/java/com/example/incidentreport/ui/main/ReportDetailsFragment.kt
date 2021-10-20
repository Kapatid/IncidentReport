package com.example.incidentreport.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incidentreport.R
import com.example.incidentreport.adapters.IncidentReportAdapter
import com.example.incidentreport.adapters.VictimsAdapter
import com.example.incidentreport.databinding.FragmentReportDetailsBinding
import com.example.incidentreport.db.IncidentReportDatabase
import com.example.incidentreport.models.IncidentReport
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_report_details.*
import java.text.SimpleDateFormat
import java.util.*

class ReportDetailsFragment : Fragment(R.layout.fragment_report_details) {
    private val victimsAdapter by lazy { VictimsAdapter() }
    private val incidentReportAdapter by lazy { IncidentReportAdapter(this, requireContext()) }

    private var _binding: FragmentReportDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var report: IncidentReport

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailsBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(
                ReportDetailsFragmentDirections.actionReportDetailsFragmentToPagerFragment()
            )
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val reportJson = sharedPref?.getString("reportToView", null)

        report = Gson().fromJson(reportJson, IncidentReport::class.java)

        binding.rdIncidentType.text = report.incidentType

        (SimpleDateFormat("hh:mm a MMMM d, y", Locale.getDefault())
            .format(report.dateTime)).also {
            binding.rdReportedAt.text = it }

        binding.rdReportedBy.text = report.reporter

        (SimpleDateFormat("hh:mm a MMMM d, y (z)", Locale.getDefault())
            .format(report.dateTime)).also {
                binding.rdTime.text = it }

        binding.rdLocation.text = report.location
        binding.rdMissing.text = report.missing.toString()
        binding.rdInjured.text = report.injured.toString()
        binding.rdFatalities.text = report.fatalities.toString()
        binding.rdDescription.text = report.description

        if (report.victims.isNotEmpty()) {
            victimsAdapter.setData(report.victims)

            binding.rdVictimList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE

            binding.rdVictimList.layoutManager = LinearLayoutManager(requireContext())
            binding.rdVictimList.adapter = victimsAdapter
        } else {
            binding.rdVictimList.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }

        binding.ivDelete.setOnClickListener {
            deleteReportAlertDialog(report)

//            findNavController().navigate(
//                ReportDetailsFragmentDirections.actionReportDetailsFragmentToPagerFragment()
//            )
        }

        binding.ivEdit.setOnClickListener {
            findNavController().navigate(
                ReportDetailsFragmentDirections.actionReportDetailsFragmentToEditReportFragment()
            )
        }

        return binding.root
    }

    /**
     * Delete alert dialog.
     * @param incidentReport Type of [IncidentReport]
     * @return void
     */
    private fun deleteReportAlertDialog(incidentReport: IncidentReport) {
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setTitle("DELETE RECORD")
            .setMessage("Are you sure you want to delete ${incidentReport.reporter}'s record?")

        // Performing positive action
        builder.setNegativeButton("DELETE") { dialogInterface, _ ->

            val db = IncidentReportDatabase(requireContext())

            val status = db.deleteIncidentReport(incidentReport.id)

            if (status > -1) {
                Snackbar.make(
                    fragmentReportDetails,
                    "${incidentReport.reporter}'s report deleted.",
                    Snackbar.LENGTH_SHORT
                ).show()

                incidentReportAdapter.setData(db.getIncidentReports())
            }

            dialogInterface.dismiss() // Dialog close
        }

        // Performing negative action
        builder.setPositiveButton("CANCEL") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog close
        }

        val alertDialog: AlertDialog = builder.create()

        // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.setCancelable(false)

        alertDialog.show()
    }
}