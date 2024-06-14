package com.example.bhojanalya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bhojanalya.adapter.NotificationAdapter
import com.example.bhojanalya.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Notification_Bottom_Fragment : BottomSheetDialogFragment(){
    private lateinit var binding:FragmentNotificationBottomBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentNotificationBottomBinding.inflate(inflater,container,false)
        val notifications= listOf("Your order has been Canceled Successfully","Order has been taken by the driver","Congrats Your Order Placed")
        val notificationsImages= listOf(R.drawable.sademoji, R.drawable.truck,R.drawable.congratulation)
        val adapter=NotificationAdapter(ArrayList(notifications), ArrayList(notificationsImages))
binding.notficationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notficationRecyclerView.adapter=adapter
        return binding.root
    }

    companion object {
    }
    }
