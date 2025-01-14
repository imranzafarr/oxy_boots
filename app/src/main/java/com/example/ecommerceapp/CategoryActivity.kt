package com.example.ecommerceapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerceapp.Adapters.CategoryFragmentAdapter
import com.example.ecommerceapp.Adapters.ShoesRcViewAdapter
import com.example.ecommerceapp.Models.ShoeApiInterface
import com.example.ecommerceapp.Models.ShoesItems
import com.example.ecommerceapp.Models.ShoesResponse
import com.example.ecommerceapp.databinding.ActivityCategoryBinding
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CategoryActivity : AppCompatActivity() {
    private  val binding: ActivityCategoryBinding by lazy{
        ActivityCategoryBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: CategoryFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backImage.setOnClickListener {
            finish()

        }
        //Setting retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://faux-api.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api=retrofit.create(ShoeApiInterface::class.java)
        api.getShoes().enqueue(object : Callback<ShoesResponse> {
            override fun onResponse(call: Call<ShoesResponse>, response: Response<ShoesResponse>) {
                if(response.isSuccessful){
                    val shoesResponse = response.body()
                    shoesResponse?.let {
                        val categories = it.result
                        adapter = CategoryFragmentAdapter(this@CategoryActivity, categories)
                        binding.categoryViewPager.adapter = adapter

                        // Set up TabLayout with ViewPager2
                        TabLayoutMediator(binding.tabLayout, binding.categoryViewPager) { tab, position ->
                            tab.text = categories[position].category
                        }.attach()
                    }

                }
            }

            override fun onFailure(call: Call<ShoesResponse>, t: Throwable) {
                Toast.makeText(this@CategoryActivity, "Failed to fetch Shoes Data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}