package com.example.ecommerceapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager


import androidx.recyclerview.widget.LinearLayoutManager

import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel

import com.example.ecommerceapp.Adapters.ShoesRcViewAdapter


import com.example.ecommerceapp.Models.ShoeApiInterface
import com.example.ecommerceapp.Models.ShoesResponse
import com.example.ecommerceapp.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ShoesRcViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Open Drawer when menuImage is clicked
        binding.menuImage.setOnClickListener {
            (activity as? StoreActivity)?.openDrawer()
        }
        binding.seeAll.setOnClickListener{
            val intent = Intent(context, CategoryActivity::class.java)
            startActivity(intent)
        }

        // Creating image list
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.bannerone, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.bannertwo,  ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.bannerthree, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.bannerfour, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.bannerfive, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
//                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val url = when(position) {
                    0 -> "https://www.nike.com/w/mens-shoes-nik1zy7ok" // Nike
                    1 -> "https://www.adidas.com/us/men-shoes" // Adidas
                    2 -> "https://us.puma.com/us/en/men/shoes?srsltid=AfmBOophqwtfwETXz0Hr" +
                            "X_sdAnv1VQoiMT2elc5-MgeXRjccPt4n16h9" // Puma
                    3 -> "https://www.reebok.com/c/200000002/men-shoes" // Reebok
                    4 -> "https://www.newbalance.com/pd/9060/U9060V1-49464.html" // New Balance
                    else -> "https://www.google.com/search?q=shoes"
                }
                // Open the URL in browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        })

        //Setting retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://faux-api.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Connecting api with retrofit
        val api = retrofit.create(ShoeApiInterface::class.java)
        api.getShoes().enqueue(object : Callback<ShoesResponse> {
            override fun onResponse(call: Call<ShoesResponse>, response: Response<ShoesResponse>) {
                //Checking if response is successful
                if (response.isSuccessful) {
                    val shoesResponse = response.body()
                    if(shoesResponse!=null && shoesResponse.result.isNotEmpty()){
                        val allShoes = shoesResponse.result.flatMap { it.products }
                        binding.shoesRecyclerView.layoutManager = GridLayoutManager(context,2)
                        binding.shoesRecyclerView.adapter = ShoesRcViewAdapter(allShoes)
                    }
                }
                else {
                    Toast.makeText(context, "API response not successful", Toast.LENGTH_SHORT).show()
                }
            }

            //Checking if response is not successful
            override fun onFailure(call: Call<ShoesResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to fetch Shoes Data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

}




















