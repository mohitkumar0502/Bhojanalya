package com.example.bhojanalya

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bhojanalya.databinding.ActivityChaluBinding

class ChaluActivity : AppCompatActivity() {
private val binding:ActivityChaluBinding by lazy {
    ActivityChaluBinding.inflate(layoutInflater)
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.nextButton.setOnClickListener {
            val intent =Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}