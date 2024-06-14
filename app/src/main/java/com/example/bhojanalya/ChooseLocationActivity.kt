package com.example.bhojanalya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.bhojanalya.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList = arrayOf("Jaipur","Delhi","Sultanpur","Mumbai","Varanasi","Ayodhya","Kanpur")
        val adapter =ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView =binding.listofLocation
        autoCompleteTextView.setAdapter(adapter)
    }
}