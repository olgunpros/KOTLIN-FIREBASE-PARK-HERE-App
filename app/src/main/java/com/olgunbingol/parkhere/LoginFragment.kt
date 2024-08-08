package com.olgunbingol.parkhere

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var emailText: TextView
    private lateinit var passwordText: TextView
    private lateinit var googleClicked: ImageView
    private lateinit var loginClicked: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val reqCode = 123
    private lateinit var checkboxClicked : CheckBox
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        emailText = view.findViewById(R.id.emailText)
        passwordText = view.findViewById(R.id.passwordText)
        googleClicked = view.findViewById(R.id.googleClicked)
        loginClicked = view.findViewById(R.id.loginClicked)
        checkboxClicked = view.findViewById(R.id.checkboxClicked)

        sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        var rememberChecked = sharedPreferences.getBoolean("remember_checked",false)
        checkboxClicked.isChecked = rememberChecked
        checkboxClicked.setOnCheckedChangeListener { _, isChecked ->
            rememberChecked = isChecked
        }
        if(rememberChecked == true) {
            findNavController().navigate(R.id.action_loginFragment3_to_homeFragment)
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        loginClicked.setOnClickListener {


            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                if(rememberChecked == true ) {
                    with(sharedPreferences.edit()) {
                        putBoolean("remember_checked",true)
                        apply()
                    }
                }
                else {
                    with(sharedPreferences.edit()) {
                        remove("remember_checked")
                        apply()
                    }
                }
                findNavController().navigate(R.id.action_loginFragment3_to_homeFragment)
            }.addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

        googleClicked.setOnClickListener {
            Toast.makeText(context, "Logging In", Toast.LENGTH_SHORT).show()
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, reqCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        findNavController().navigate(R.id.action_loginFragment3_to_homeFragment)
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(requireActivity()) != null) {
            findNavController().navigate(R.id.action_loginFragment3_to_homeFragment)
        }
    }
}
