package com.example.incidentreport.ui.main

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incidentreport.MainActivity
import com.example.incidentreport.R
import com.example.incidentreport.adapters.EditableVictimsAdapter
import com.example.incidentreport.databinding.FragmentCreateReportBinding
import com.example.incidentreport.db.IncidentReportDatabase
import com.example.incidentreport.models.IncidentReport
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_create_report.*
import java.text.SimpleDateFormat
import java.util.*

class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    private val permissionRequest = 101

    private val victimsAdapter by lazy { EditableVictimsAdapter() }

    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0

    private val victimsList = emptyList<String>().toMutableList()

    private lateinit var incidentReport: IncidentReport

    companion object {
        /**
         * Used for fixing missing zeros in TimePickerDialog
         * @param number [Int]
         * @return [Int]
         */
        fun checkDigit(number: Int) = if (number <= 9) "0$number" else number.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.SEND_SMS
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS), permissionRequest)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateReportBinding.inflate(inflater, container, false)

        binding.incidentTypeTextView.setAdapter(ArrayAdapter(
            requireContext(), R.layout.list_item, resources.getStringArray(R.array.incident_types)
        ))

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnDate.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    "${year}-${monthOfYear + 1}-${dayOfMonth}".also { tvDate.text = it }
                }, mYear, mMonth, mDay
            ).show()
        }

        binding.btnTime.setOnClickListener {
            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            TimePickerDialog(requireContext(),
                { _, hourOfDay, minute ->
                    "${checkDigit(hourOfDay)}:${checkDigit(minute)}".also { tvTime.text = it }
                },
                mHour,
                mMinute,
                false
            ).show()
        }

        binding.etMissing.setText("0")
        binding.etInjured.setText("0")
        binding.etFatalities.setText("0")
        binding.rvVictims.visibility = View.GONE
        binding.tvNoRecordsAvailable.visibility = View.VISIBLE

        binding.btnAddVictim.setOnClickListener {
            victimsList.add("")
            victimsAdapter.setData(victimsList)

            if (victimsList.isNotEmpty()) {
                victimsAdapter.setData(victimsList)

                binding.rvVictims.visibility = View.VISIBLE
                binding.tvNoRecordsAvailable.visibility = View.GONE

                binding.rvVictims.layoutManager = LinearLayoutManager(requireContext())
                binding.rvVictims.adapter = victimsAdapter
            } else {
                binding.rvVictims.visibility = View.GONE
                binding.tvNoRecordsAvailable.visibility = View.VISIBLE
            }
        }

        binding.btnCreateReport.setOnClickListener {
            saveIncidentReport(binding)
        }

        return binding.root
    }

    /**
     * Save an [IncidentReport] to database then show snackbar and send SMS message.
     * @return void
     */
    private fun saveIncidentReport(binding: FragmentCreateReportBinding) {
        val loggedInUser = MainActivity.getLoggedInUser(requireActivity())

        val reporter = loggedInUser?.fullName.toString()
        val incidentType = binding.incidentTypeTextView.text.toString()
        val dateTime = "$mYear-${mMonth + 1}-${mDay} $mHour:$mMinute"
        val location = binding.etLocation.text.toString().trim()
        val injured = binding.etInjured.text.toString()
        val missing = binding.etMissing.text.toString()
        val fatalities = binding.etFatalities.text.toString()
        val incidentDescription = binding.etIncidentDescription.text.toString().trim()

        val db = IncidentReportDatabase(requireContext())

        if (reporter.isNotEmpty() && dateTime.isNotEmpty() && incidentType.isNotEmpty() &&
            missing.isNotEmpty() && injured.isNotEmpty() && fatalities.isNotEmpty() &&
            incidentDescription.isNotEmpty() && victimsList.isNotEmpty() &&
            location.isNotEmpty()) {

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            incidentReport = IncidentReport(0,
                reporter = reporter,
                incidentType = incidentType,
                dateTime = sdf.parse(dateTime) as Date,
                location = location,
                missing = missing.toInt(),
                injured = injured.toInt(),
                fatalities = fatalities.toInt(),
                description = incidentDescription,
                victims = victimsAdapter.getVictims()
            )

            val status = db.addIncidentReport(incidentReport)

            if (status > -1) {
                val permissionCheck = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.SEND_SMS
                )

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    myMessage()
                    Snackbar.make(
                        binding.clCreateReport,
                        "Incident report successfully created and SMS sent.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.clCreateReport,
                        "Incident report created but unable to send SMS.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Snackbar.make(
                binding.clCreateReport,
                "All fields must not be blank.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun myMessage() {
        val phoneNumber = "15555215554"
        val myMsg = "INCIDENT REPORT " +
                "\nReporter: ${incidentReport.reporter}" +
                "\nIncident Type: ${incidentReport.incidentType}" +
                "\nDate & Time: ${incidentReport.dateTime}" +
                "\nIncident Description: ${incidentReport.description}" +
                "\nInjured: ${incidentReport.injured}" +
                "\nMissing: ${incidentReport.missing}" +
                "\nFatality: ${incidentReport.fatalities}" +
                "\nVictims: ${incidentReport.victims.toString()
                    .replace("[", "").replace("]", "")}" +
                "\nLocation: ${incidentReport.location}" +
                "\nCreation Date: ${incidentReport.createdAt}"

        val smsManager: SmsManager = SmsManager.getDefault()

        try {
            val parts = smsManager.divideMessage(myMsg)

            smsManager.sendMultipartTextMessage(
                phoneNumber, null, parts,
                null, null
            )
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
}