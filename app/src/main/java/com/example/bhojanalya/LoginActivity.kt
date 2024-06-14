package com.example.bhojanalya

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bhojanalya.databinding.ActivityLoginBinding
import com.example.bhojanalya.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var userName:String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient



    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        // init firebase auth
        auth= Firebase.auth
        //init Firebase database
        database= Firebase.database.reference
        //google
        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions)


        binding.loginbutton.setOnClickListener{
            //get data from text fields
                email = binding.loginEmail.text.toString().trim()
                password = binding.loginPassword.text.toString().trim()
            if (email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill email or password☹️", Toast.LENGTH_SHORT).show()
            }else{
                createUser()
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
            }

            val intent=Intent(this,SignActivity::class.java)
            startActivity(intent)
        }
        binding.donthavebutton.setOnClickListener {
            val intent=Intent(this,SignActivity::class.java)
            startActivity(intent)
        }
        binding.googlelogin.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
    }
    //google sign in
    private val launcher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        //successfully sign in with google
                        Toast.makeText(this, "Successfully sign-in with google", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Google Sign-in Failed☹️", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Google Sign-in Failed☹️", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun createUser() {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveUserData()
                val user = auth.currentUser
                updateUi(user)
            } else {
                Toast.makeText(this, "Please Create Account", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount:Failure☹️", task.exception)
            }
        }
    }

    private fun saveUserData() {
        //retrieve data
        email=binding.loginEmail.text.toString().trim()
        password=binding.loginPassword.text.toString().trim()

        val user= UserModel(userName,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid

        //save data to Firebase
        database.child("user").child(userId).setValue(user)
    }
    //check if user is already login in
    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if (currentUser!=null){
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}