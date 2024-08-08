package com.olgunbingol.parkhere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.olgunbingol.parkhere.databinding.FragmentDetayBinding

class DetayFragment : Fragment() {
    private lateinit var binding: FragmentDetayBinding
    private lateinit var routeClicked: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name")
        val address = arguments?.getString("adress")
        val price = arguments?.getString("price")
        val longitude = arguments?.getDouble("longitude")
        val latitude = arguments?.getDouble("latitude")

        binding.nameView.text = name
        binding.adressView.text = address
        binding.pricee.text = price

        routeClicked = view.findViewById(R.id.routeClicked)
        routeClicked.setOnClickListener {
            if (longitude != null && latitude != null) {
                val bundle = Bundle().apply {
                    putDouble("longitude", longitude)
                    putDouble("latitude", latitude)
                }
                findNavController().navigate(R.id.action_detayFragment_to_mapsFragment, bundle)
            }
        }
    }
}
