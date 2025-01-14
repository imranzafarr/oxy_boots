package com.example.ecommerceapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Grid
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.Adapters.ShoesRcViewAdapter
import com.example.ecommerceapp.Models.ShoeApiInterface
import com.example.ecommerceapp.Models.ShoesResponse
import com.example.ecommerceapp.databinding.ActivityAllShoesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AllShoesActivity : AppCompatActivity() {
    private val binding: ActivityAllShoesBinding by lazy {
        ActivityAllShoesBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Navigating to previous activity
        binding.backImage.setOnClickListener {
            this@AllShoesActivity.onBackPressedDispatcher.addCallback(this,object:
                OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    finish()
                }
            })
            onBackPressedDispatcher.onBackPressed()
        }
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
//                    if (shoesResponse?.status == "success") {
//                        binding.shoesRecyclerView.layoutManager = LinearLayoutManager(this@AllShoesActivity)
//                        binding.shoesRecyclerView.adapter = ShoesRcViewAdapter(shoesResponse.result)
//                    } else {
//                        Toast.makeText(this@AllShoesActivity, "API response not successful", Toast.LENGTH_SHORT).show()
//                    }
                    if(shoesResponse!=null){
                        val allShoes = shoesResponse.result.flatMap { it.products }
                        binding.shoesRecyclerView.layoutManager = GridLayoutManager(this@AllShoesActivity, 2)
                        binding.shoesRecyclerView.adapter = ShoesRcViewAdapter(allShoes)
                    }
                }
                else {
                    Toast.makeText(this@AllShoesActivity, "API response not successful", Toast.LENGTH_SHORT).show()
                }

            }

            //Checking if response is not successful
            override fun onFailure(call: Call<ShoesResponse>, t: Throwable) {
                Toast.makeText(this@AllShoesActivity, "Failed to fetch Shoes Data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}