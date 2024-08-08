package com.olgunbingol.parkhere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.olgunbingol.parkhere.adapter.park_adapter
import com.olgunbingol.parkhere.databinding.FragmentHomeBinding
import com.olgunbingol.parkhere.model.otopark

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var textT: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parkAdapter: park_adapter
    private lateinit var parkArrayList: ArrayList<otopark>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        textT = view.findViewById(R.id.textT)

        db = FirebaseFirestore.getInstance()
        parkArrayList = ArrayList()

        parkAdapter = park_adapter(parkArrayList, findNavController())
        binding.parkrecyclerView.layoutManager = LinearLayoutManager(context)
        binding.parkrecyclerView.adapter = parkAdapter

        getData()

        val currentUser = auth.currentUser
        textT.text = currentUser?.email ?: "Please Sign In"

        binding.bottomNavigationView2.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }
                R.id.searchFragment -> {
                    findNavController().navigate(R.id.searchFragment)
                    true
                }
                R.id.mapsFragment -> {
                    findNavController().navigate(R.id.mapsFragment)
                    true
                }
                R.id.signFragment -> {
                    auth.signOut()
                    Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.signFragment)
                    true
                }
                else -> false
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getData() {
        db.collection("Otoparklar").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show()
            } else {
                if (value != null && !value.isEmpty) {
                    val documents = value.documents
                    parkArrayList.clear() // Listeyi temizle
                    for (document in documents) {
                        val otoparkadi = document.getString("otoparkadi") ?: ""
                        val imageUrl = document.getString("imageUrl") ?: ""
                        val price = document.getString("price") ?: ""
                        val address = document.getString("address") ?: ""
                        val status = document.getString("status") ?: ""
                        val longitude = document.getDouble("longitude") ?: 0.0
                        val latitude = document.getDouble("latitude") ?: 0.0

                        val otopark = otopark(otoparkadi, imageUrl, price, address, status, longitude, latitude)
                        parkArrayList.add(otopark)
                    }
                    parkAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
