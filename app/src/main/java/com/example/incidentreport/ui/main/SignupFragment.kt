package com.example.incidentreport.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.incidentreport.R
import com.example.incidentreport.db.UserDatabase
import com.example.incidentreport.models.User
import com.example.incidentreport.utils.Validator.Companion.isValidEmail
import com.example.incidentreport.utils.Validator.Companion.isValidPassword
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_signup.*

class SignupFragment : Fragment(R.layout.fragment_signup) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGoToLogin.setOnClickListener {
            findNavController().navigate(
                SignupFragmentDirections.actionSignupFragmentToMainFragment()
            )
        }

        btnSignup.setOnClickListener {
            signup()
        }
    }

    /**
     * Create user.
     * @return void
     */
    private fun signup() {
        val fullname = etSignupName.text.toString().trim()
        val email = etSignupEmail.text.toString().trim()
        val password = etSignupPassword.text.toString()
        val confirmPassword = etSignupConfirmPassword.text.toString()
        val databaseHandler = UserDatabase(requireContext())

        // Empty check
        if (fullname.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            // Email and Password validation
            if (isValidEmail(etSignupEmail) && isValidPassword(etSignupPassword)) {
                // Check if Password match
                if (password == confirmPassword) {
                    val status = databaseHandler.addUser(User(0, fullname, email, password))

                    if (status != null) {
                        // User gets created
                        if (status > -1) {
                            etSignupName.text?.clear()
                            etSignupEmail.text?.clear()
                            etSignupPassword.text?.clear()
                            etSignupConfirmPassword.text?.clear()

                            findNavController().navigate(
                                SignupFragmentDirections.actionSignupFragmentToMainFragment()
                            )

                            Snackbar.make(
                                signupFragment,
                                "User successfully created!.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            signupFragment,
                            "Email is already in use.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Snackbar.make(
                        signupFragment,
                        "Password does not match.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Snackbar.make(
                signupFragment,
                "Email, name, and password must not be empty.",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}