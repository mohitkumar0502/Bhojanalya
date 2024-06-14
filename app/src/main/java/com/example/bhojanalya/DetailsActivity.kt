package com.example.bhojanalya

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bhojanalya.databinding.ActivityDetailsBinding
import com.example.bhojanalya.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodImage: String? = null
    private var foodIngredient: String? = null
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //initialize fire base auth
        auth = FirebaseAuth.getInstance()

        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredient = intent.getStringExtra("MenuItemIngredient")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodImage = intent.getStringExtra("MenuItemImage")

        with(binding) {
            detailFoodName.text = foodName
            detailDescription.text = foodDescription
            detailIngredients.text = foodIngredient
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailFoodImage)
        }


        binding.imageButton.setOnClickListener {
            finish()
        }
        binding.addItembutton.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId  = auth.currentUser?.uid?:""
        //create a cart items object
        val cartItem =CartItems(foodName.toString(),foodPrice.toString(),foodDescription.toString(),foodImage.toString(),1)

        //save data to cart item to firebase database
        database.child("user").child(userId).child("cartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Item added into cart successfully ", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Item not added to cart", Toast.LENGTH_SHORT).show()
        }
    }
}