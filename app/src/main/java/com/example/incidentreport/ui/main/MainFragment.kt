package com.example.incidentreport.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.incidentreport.MainActivity.Companion.setLoggedInUser
import com.example.incidentreport.R
import com.example.incidentreport.db.UserDatabase
import com.example.incidentreport.utils.Validator.Companion.isValidEmail
import com.example.incidentreport.utils.Validator.Companion.isValidPassword
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(R.layout.main_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGoToSignup.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToSignupFragment()
            )
        }

        btnLogin.setOnClickListener {
            login()
        }
    }

    /**
     * Login user.
     * @return void
     */
    private fun login() {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        val databaseHandler = UserDatabase(requireContext())

        // Empty check
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Email and Password validation
            if (isValidEmail(etLoginEmail) && isValidPassword(etLoginPassword)) {

                val status = databaseHandler.findUser(email, password)

                if (status != null) {
                    setLoggedInUser(requireActivity(), status)

                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToPagerFragment()
                    )
                } else {
                    Snackbar.make(
                        loginFragment,
                        "User not found.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Snackbar.make(
                loginFragment,
                "Email and password must not be empty.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}