package com.example.incidentreport.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incidentreport.MainActivity
import com.example.incidentreport.R
import com.example.incidentreport.adapters.EditableVictimsAdapter
import com.example.incidentreport.databinding.FragmentEditReportBinding
import com.example.incidentreport.db.IncidentReportDatabase
import com.example.incidentreport.models.IncidentReport
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_create_report.*
import kotlinx.android.synthetic.main.fragment_edit_report.*
import java.text.SimpleDateFormat
import java.util.*

class EditReportFragment : Fragment(R.layout.fragment_edit_report) {

    private val victimsAdapter by lazy { EditableVictimsAdapter() }

    private var _binding: FragmentEditReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var report: IncidentReport

    private var victimsList = emptyList<String>().toMutableList()

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReportBinding.inflate(inflater, container, false)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val reportJson = sharedPref?.getString("reportToView", null)

        report = Gson().fromJson(reportJson, IncidentReport::class.java)
        victimsList = report.victims as MutableList<String>

        binding.btnBack.setOnClickListener {
            findNavController().navigate(
                EditReportFragmentDirections.actionEditReportFragmentToReportDetailsFragment()
            )
        }

        binding.erIncidentTypeTextView.setAdapter(ArrayAdapter(
            requireContext(), R.layout.list_item, resources.getStringArray(R.array.incident_types)
        ))

        (SimpleDateFormat("yyyy-d-MM", Locale.getDefault())
            .format(report.dateTime)).also {
                binding.erTVDate.text = it }

        (SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(report.dateTime)).also {
                binding.erTVTime.text = it }

        binding.erLocation.setText(report.location)
        binding.erInjured.setText(report.injured.toString())
        binding.erMissing.setText(report.missing.toString())
        binding.erFatalities.setText(report.fatalities.toString())
        binding.erIncidentDescription.setText(report.description)

        binding.erBtnDate.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    "${year}-${monthOfYear + 1}-${dayOfMonth}".also { binding.erTVDate.text = it }
                }, mYear, mMonth, mDay
            ).show()
        }

        binding.erBtnTime.setOnClickListener {
            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            TimePickerDialog(requireContext(),
                { _, hourOfDay, minute ->
                    "${
                        CreateReportFragment.checkDigit(hourOfDay)}:${CreateReportFragment.checkDigit(minute)}"
                        .also { binding.erTVTime.text = it }
                },
                mHour,
                mMinute,
                false
            ).show()
        }

        if (victimsList.isNotEmpty()) {
            victimsAdapter.setData(victimsList)

            binding.rvERReports.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE

            binding.rvERReports.layoutManager = LinearLayoutManager(requireContext())
            binding.rvERReports.adapter = victimsAdapter
        } else {
            binding.rvERReports.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }

        binding.erBtnAddVictim.setOnClickListener {
            victimsList.add("")
            victimsAdapter.setData(victimsList)

            if (victimsList.isNotEmpty()) {
                victimsAdapter.setData(victimsList)

                binding.rvERReports.visibility = View.VISIBLE
                binding.tvNoRecordsAvailable.visibility = View.GONE

                binding.rvERReports.layoutManager = LinearLayoutManager(requireContext())
                binding.rvERReports.adapter = victimsAdapter
            } else {
                binding.rvERReports.visibility = View.GONE
                binding.tvNoRecordsAvailable.visibility = View.VISIBLE
            }
        }

        binding.btnUpdateReport.setOnClickListener {
            val loggedInUser = MainActivity.getLoggedInUser(requireActivity())

            val reporter = loggedInUser?.fullName.toString()
            val incidentType = binding.erIncidentTypeTextView.text.toString()
            val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .parse(binding.erTVDate.text.toString() + " "
                        + binding.erTVTime.text.toString())
            val location = binding.erLocation.text.toString().trim()
            val injured = binding.erInjured.text.toString()
            val missing = binding.erMissing.text.toString()
            val fatalities = binding.erFatalities.text.toString()
            val incidentDescription = binding.erIncidentDescription.text.toString().trim()

            val db = IncidentReportDatabase(requireContext())

            if (reporter.isNotEmpty() && dateTime != null && incidentType.isNotEmpty() &&
                missing.isNotEmpty() && injured.isNotEmpty() && fatalities.isNotEmpty() &&
                incidentDescription.isNotEmpty() && victimsAdapter.getVictims().isNotEmpty() &&
                location.isNotEmpty()) {

                val status = db.updateIncidentReport(
                    IncidentReport(
                        id = report.id,
                        reporter = reporter,
                        incidentType = incidentType,
                        dateTime = dateTime,
                        location = location,
                        missing = missing.toInt(),
                        injured = injured.toInt(),
                        fatalities = fatalities.toInt(),
                        description = incidentDescription,
                        victims = victimsAdapter.getVictims()
                    )
                )

                if (status > -1) {
//                sendMessage()
                    Snackbar.make(
                        binding.fragmentEditReport,
                        "Incident report successfully updated.",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    with (sharedPref?.edit()) {
                        this?.putString("reportToView", Gson().toJson(db.getReport(report.id.toString())))
                        this?.apply()
                    }
                }
            } else {
                Snackbar.make(
                    binding.fragmentEditReport,
                    "All fields must not be blank.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }
}