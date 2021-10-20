package com.example.incidentreport.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.incidentreport.MainActivity
import com.example.incidentreport.R
import com.example.incidentreport.adapters.FragmentAdapter
import kotlinx.android.synthetic.main.fragment_pager.*

class PagerFragment : Fragment(R.layout.fragment_pager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loggedInUser = MainActivity.getLoggedInUser(requireActivity())
        if (loggedInUser != null) {
            "Hello ${loggedInUser.fullName}!".also { tvLoggedInUser.text = it }
        } else {
            findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToMainFragment()
            )
        }

        btnLogout.setOnClickListener {
            MainActivity.logout(requireActivity())
            findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToMainFragment()
            )
        }

        val adapter = activity?.let { FragmentAdapter(it.supportFragmentManager, lifecycle) }

        pager.isUserInputEnabled = false
        pager.adapter = adapter

        bottomNavigationView.menu.getItem(1).isEnabled = false

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.miHome -> {
                    pager.currentItem = 0
                }
                R.id.miReports -> {
                    pager.currentItem = 1
                }
            }
            true
        }

        fabAddReport.setOnClickListener {
            findNavController().navigate(
                PagerFragmentDirections.actionPagerFragmentToCreateReportFragment()
            )
        }
    }
}