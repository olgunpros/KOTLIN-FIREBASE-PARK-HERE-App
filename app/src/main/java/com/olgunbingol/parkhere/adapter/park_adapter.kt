package com.olgunbingol.parkhere.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.olgunbingol.parkhere.DetayFragment
import com.olgunbingol.parkhere.R
import com.olgunbingol.parkhere.databinding.ParkRowBinding
import com.olgunbingol.parkhere.model.otopark

class park_adapter(private val otoparklarList: ArrayList<otopark>, private val navController: NavController) : RecyclerView.Adapter<park_adapter.otopark_Holder>() {

    class otopark_Holder(val binding: ParkRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): otopark_Holder {
        val binding = ParkRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return otopark_Holder(binding)
    }

    override fun getItemCount(): Int {
        return otoparklarList.size
    }

    override fun onBindViewHolder(holder: otopark_Holder, position: Int) {
        val otopark = otoparklarList[position]
        holder.binding.nameText.text = otopark.otoparkadi
        holder.binding.adressText.text = otopark.adres
        holder.binding.detailClicked.setOnClickListener {
            val bundle = Bundle().apply {
                putString("name", otopark.otoparkadi)
                putString("adress", otopark.adres)
                putString("price", otopark.price)
                putDouble("longitude", otopark.longitude)
                putDouble("latitude", otopark.latitude)

            }
            navController.navigate(R.id.detayFragment, bundle)
        }
    }
}
