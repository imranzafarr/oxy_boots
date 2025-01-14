package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.Adapters.NotificationAdapter

import com.example.ecommerceapp.Models.NotificationItems

import com.example.ecommerceapp.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class NotificationsFragment : Fragment() {
    //Applying binding
    private val binding by lazy { FragmentNotificationsBinding.inflate(layoutInflater) }
    //Defining NotificationAdapter
    private lateinit var notificationsAdapter: NotificationAdapter
    //Defining orderItems
    private val notifications=ArrayList<NotificationItems>()
    //Getting Firebase instance
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //backImage click effect
        binding.backImage.setOnClickListener {
            requireActivity().onBackPressed()
        }
        //Setting up RecyclerView
        notificationsAdapter= NotificationAdapter(notifications)
        binding.notificationRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            adapter=notificationsAdapter

        }
        //Function call to fetch Notification items
        fetchNotificationItems()

        binding.clearAll.setOnClickListener {
            deleteNotifications()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteNotifications() {
        // Getting FireStore instance
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            db.collection("notifications")
                .whereEqualTo("uid", currentUserUid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        // Deleting each item from the notifications collection
                        document.reference.delete()
                    }
                    // Clear local list and notify adapter
                    notifications.clear()
                    notificationsAdapter.notifyDataSetChanged()
                    toggleEmptyState()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to clear notifications", Toast.LENGTH_SHORT).show()
                }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchNotificationItems() {
        //Getting current user id
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        // Checking if user is logged in
        if (currentUserUid != null) {
            //Getting FireStore instance
            val firestore = FirebaseFirestore.getInstance()
            //Getting notifications from FireStore
            firestore.collection("notifications")
                .whereEqualTo("uid", currentUserUid)
                .orderBy("number",Query.Direction.DESCENDING)
                .get()
                //Handling success
                .addOnSuccessListener{querySnapshot->
                    notifications.clear()
                    for (document in querySnapshot) {
                        val item = document.toObject(NotificationItems::class.java)
                        notifications.add(item)
                    }
                    notificationsAdapter.notifyDataSetChanged()
                    toggleEmptyState()
                }
                //Handling failure
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to fetch Notifications", Toast.LENGTH_SHORT).show()
                }
        }
        //Generating a toast if user is not logged in
        else{
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEmptyState() {
        if (notifications.isEmpty()) {
            binding.emptytext.visibility = View.VISIBLE
            binding.notificationRecyclerView.visibility = View.GONE
        } else {
            binding.emptytext.visibility = View.GONE
            binding.notificationRecyclerView.visibility = View.VISIBLE
        }
    }
    }
