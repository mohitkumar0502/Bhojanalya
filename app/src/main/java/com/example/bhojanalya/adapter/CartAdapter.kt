package com.example.bhojanalya.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bhojanalya.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private var cartDescription: MutableList<String>,
    private var cartImages: MutableList<String>,
    private var cartQuantity: MutableList<Int>,
    private var cartIngredients: MutableList<String>,

    ) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    //instance Firebase
    private val auth = FirebaseAuth.getInstance()

    init {
        //init firebase
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItems.size

        itemQuantities = IntArray(cartItemNumber) { 1 }
        cartItemsReference = database.reference.child("user").child(userId).child("cartItems")


    }

    companion object {
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    //get updated quantity
    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                cartFoodName.text = cartItems[position]
                cartItemPrice.text = cartItemPrices[position]

                //load image using Glide
                val uriString = cartImages[position]
                val uri = Uri.parse(uriString)

                Glide.with(context).load(uri).into(cartimage)

                cartItemquantity.text = quantity.toString()

                minusbutton.setOnClickListener {
                    decreaseQuantity(position)
                }
                plusbutton.setOnClickListener {
                    increaseQuantity(position)

                }
                deleteButton.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteQuantity(itemPosition)
                    }

                }
            }
        }
        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemquantity.text = itemQuantities[position].toString()
            }
        }


        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemquantity.text = itemQuantities[position].toString()
            }

        }

        private fun deleteQuantity(position: Int) {
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve) { uniqueKey ->
                if (uniqueKey !== null) {
                    removeItem(position, uniqueKey)
                }
            }

        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    cartItems.removeAt(position)
                    cartImages.removeAt(position)
                    cartDescription.removeAt(position)
                    cartQuantity.removeAt(position)
                    cartItemPrices.removeAt(position)
                    cartIngredients.removeAt(position)
                    Toast.makeText(context, "item Deleted successfully", Toast.LENGTH_SHORT).show()
                    //update item quantity
                    itemQuantities =
                        itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == positionRetrieve) {
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }
}