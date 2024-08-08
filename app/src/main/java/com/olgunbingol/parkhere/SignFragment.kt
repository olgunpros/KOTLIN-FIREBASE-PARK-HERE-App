package com.olgunbingol.parkhere

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.google.firebase.auth.GoogleAuthProvider

class SignFragment : Fragment() {
    private lateinit var mailText: TextView
    private lateinit var passwordTextt: TextView
    private lateinit var passwordTextt2: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClick: ImageView
    private lateinit var signClicked: Button
    private lateinit var loginClickedd: Button
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val reqCode = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        mailText = view.findViewById(R.id.mailText)
        passwordTextt = view.findViewById(R.id.passwordTextt)
        passwordTextt2 = view.findViewById(R.id.passwordTextt2)
        signClicked = view.findViewById(R.id.signClicked)
        loginClickedd = view.findViewById(R.id.loginClickedd)
        googleClick = view.findViewById(R.id.googleClick)
        loginClickedd.setOnClickListener {
            findNavController().navigate(R.id.action_signFragment_to_loginFragment3)
        }
        signClicked.setOnClickListener {
            val email = mailText.text.toString()
            val password = passwordTextt.text.toString()
            val password2 = passwordTextt2.text.toString()

            if (password == password2) {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    findNavController().navigate(R.id.action_signFragment_to_homeFragment)
                }.addOnFailureListener {
                    Toast.makeText(context, "localizedMessage", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleClick.setOnClickListener {
            Toast.makeText(context, "Signing In", Toast.LENGTH_SHORT).show()
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
                firebaseAuthWithGoogle(account.idToken!!)
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                findNavController().navigate(R.id.action_signFragment_to_homeFragment)
            } else {
                Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(requireActivity()) != null) {
            findNavController().navigate(R.id.action_signFragment_to_homeFragment)
        }
    }

}
