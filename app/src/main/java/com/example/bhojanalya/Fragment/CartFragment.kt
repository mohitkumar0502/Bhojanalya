package com.example.bhojanalya.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bhojanalya.PayOutActivity
import com.example.bhojanalya.adapter.CartAdapter
import com.example.bhojanalya.databinding.FragmentCartBinding
import com.example.bhojanalya.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        //init firebase
        auth = FirebaseAuth.getInstance()
        retrieveCartItems()
        //proceed button
        binding.ProceedButton.setOnClickListener {
            //get oder detail before proceeding to checkout
            getOderItemsDetail()
        }


        return binding.root
    }

    private fun getOderItemsDetail() {
        val oderIdReference: DatabaseReference =
            database.reference.child("user").child(userId).child("cartItems")

        val foodNames = mutableListOf<String>()
        val foodPrices = mutableListOf<String>()
        val foodDescriptions = mutableListOf<String>()
        val foodIngredients = mutableListOf<String>()
        val foodImagesUri = mutableListOf<String>()
        //get item Quantity
        val foodQuantities = cartAdapter.getUpdatedItemsQuantities()

        oderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get cart items to their respective list
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    //add items detail to list
                    orderItems?.foodName?.let { foodNames.add(it) }
                    orderItems?.foodPrice?.let { foodPrices.add(it) }
                    orderItems?.foodDescription?.let { foodDescriptions.add(it) }
                    orderItems?.foodImage?.let { foodImagesUri.add(it) }
                    orderItems?.foodIngredients?.let { foodIngredients.add(it) }
                }
                orderNow(
                    foodNames,
                    foodPrices,
                    foodDescriptions,
                    foodImagesUri,
                    foodIngredients,
                    foodQuantities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "order making failed please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun orderNow(
        foodNames: MutableList<String>,
        foodPrices: MutableList<String>,
        foodDescriptions: MutableList<String>,
        foodImagesUri: MutableList<String>,
        foodIngredients: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", foodNames as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodPrices as ArrayList<String>)
            intent.putExtra("FoodItemImage", foodImagesUri as ArrayList<String>)
            intent.putExtra("FoodItemDescription", foodDescriptions as ArrayList<String>)
            intent.putExtra("FoodItemIngredients", foodIngredients as ArrayList<String>)
            intent.putExtra("FoodItemQuantities", foodQuantities as ArrayList<Int>)
            startActivity(intent)

        }
    }

    private fun retrieveCartItems() {
        //database reference
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("cartItems")
//List to store cart items
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodIngredients = mutableListOf()
        foodImagesUri = mutableListOf()
        quantity = mutableListOf()

        //fetch data from the database
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cart items object from the child node
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)

                    //add cart items details
                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItems?.foodQuantity?.let { quantity.add(it) }
                    cartItems?.foodImage?.let { foodImagesUri.add(it) }
                    cartItems?.foodIngredients?.let { foodIngredients.add(it) }
                }
                setAdapter()
            }

            private fun setAdapter() {
                cartAdapter = CartAdapter(
                    requireContext(),
                    foodNames,
                    foodPrices,
                    foodDescriptions,
                    foodImagesUri,
                    quantity,
                    foodIngredients
                )
                binding.cartRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "data not Fetch", Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {

    }
}