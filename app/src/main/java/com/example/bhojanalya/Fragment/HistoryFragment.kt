package com.example.bhojanalya.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bhojanalya.RecentOrderItems
import com.example.bhojanalya.adapter.BuyAgainAdapter
import com.example.bhojanalya.databinding.FragmentHistoryBinding
import com.example.bhojanalya.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItems: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment

        //init firebase auth and database in this
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        //retrieve and display the user order history
        retrieveBuyHistory()

        //recent buy button click
        binding.recentBuyItem.setOnClickListener {
            seeItemsRecentBuy()
        }
        binding.receivedButton.setOnClickListener {

            updateOrderStatus()
        }

        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItems[0].itemPushKey
        val completeOrderReference = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderReference.child("paymentReceived").setValue(true)
    }

    //fun to see items in recent
    private fun seeItemsRecentBuy() {
        listOfOrderItems.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem",listOfOrderItems)
            startActivity(intent)
        }
    }

    //fun to see items in recent history
    private fun retrieveBuyHistory() {
        binding.recentBuyItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItems.add(it)
                    }
                }
                listOfOrderItems.reverse()
                if (listOfOrderItems.isNotEmpty()) {
                    //display most recent buy item at bottom
                    setDataInRecentBuyItem()
                    //setup to recyclerview with previous buy items
                    setPreviousBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    //display the most recent purchase
    private fun setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItems.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainFoodName.text = it.foodNames?.firstOrNull() ?: ""
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull() ?: ""
                val image = it.foodImages?.firstOrNull() ?: ""
                Glide.with(requireContext()).load(image).into(buyAgainFoodImage)

                val isOrderAccepted = listOfOrderItems[0].orderAccepted
                if (isOrderAccepted){
                    orderStatus.background.setTint(Color.GREEN)
                    receivedButton.visibility = View.VISIBLE
                }

            }
        }
    }

    // see previous items view
    private fun setPreviousBuyItemsRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for (i in 1 until listOfOrderItems.size) {
            listOfOrderItems[i].foodNames?.firstOrNull()?.let {
                buyAgainFoodName.add(it)
                listOfOrderItems[i].foodPrices?.firstOrNull()?.let {
                    buyAgainFoodPrice.add(it)
                    listOfOrderItems[i].foodImages?.firstOrNull()?.let {
                        buyAgainFoodImage.add(it)
                    }
                }
                val rv = binding.buyAgainRecyclerView
                rv.layoutManager = LinearLayoutManager(requireContext())
                buyAgainAdapter = BuyAgainAdapter(
                    buyAgainFoodName,
                    buyAgainFoodPrice,
                    buyAgainFoodImage,
                    requireContext()
                )
                rv.adapter = buyAgainAdapter
            }
        }
    }
}