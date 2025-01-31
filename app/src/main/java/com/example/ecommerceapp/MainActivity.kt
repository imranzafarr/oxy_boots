package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.ecommerceapp.Adapters.OnBoardingItemsAdapter
import com.example.ecommerceapp.Models.OnBoardingItems
import com.example.ecommerceapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var onboardingItemsAdapter: OnBoardingItemsAdapter
    private val mainBinding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(mainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Function call foe setting onBoarding Items
        setOnboardingItems()
        //Function call for setting up indicators
        setupIndicators()
        //Function call for setting current indicator
        setCurrentIndicators(0)
    }

    //Function for setting current indicator
    @SuppressLint("SetTextI18n")
    private fun setCurrentIndicators(position: Int) {
        //Getting child count of the indicator container
        val childCount=mainBinding.indicatorContainer.childCount
        for (i in 0 until childCount){
            //Getting the image view
            val imageView=mainBinding.indicatorContainer.getChildAt(i) as ImageView
            //Checking if the position is equal to the current position
            if(i==position){
                //Setting the drawable for the image view
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            }
            //Checking if the position is not equal to the current position
            else
            {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive
                    ))
            }
        }
        if (position == 0) {
            mainBinding.nextButton.text = "Get Started"  // When on First item
        } else {
            mainBinding.nextButton.text = "Next"  // For all other items
        }
    }

    //Function for setting onBoarding Items
    private fun setOnboardingItems(){
         onboardingItemsAdapter=OnBoardingItemsAdapter(
            listOf(
                //First Item
                OnBoardingItems(
                    image = R.drawable.shoeone,
                    title = "Start Journey With Nike",
                    description = "Smart, Gorgeous and Fashionable Collection"),
                //Second Item
                OnBoardingItems(
                    image = R.drawable.shoetwo,
                    title = "Follow Latest Style Shoes",
                    description = "There Are Many Beautiful And Attractive Plants To Your Room"),
                //Third Item
                OnBoardingItems(
                    image = R.drawable.shoethree,
                    title = "Summer Shoes Nike 2022",
                    description = "Transform Your Space with Unique and Elegant Designs")))

        mainBinding.onBoardingViewPager.adapter=onboardingItemsAdapter
        mainBinding.onBoardingViewPager.registerOnPageChangeCallback(object:
        //Registering on page change callback
        ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicators(position)
            }
        })

        (mainBinding.onBoardingViewPager.getChildAt(0)
                as RecyclerView).overScrollMode= RecyclerView.OVER_SCROLL_NEVER
        mainBinding.nextButton.setOnClickListener {
            if (mainBinding.onBoardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                mainBinding.onBoardingViewPager.currentItem += 1
            } else {
                navigateToNextActivity()
            }


        }}
    //Function for setting up indicators
    private fun setupIndicators(){
        val indicators= arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams= LayoutParams(WRAP_CONTENT,WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for(i in indicators.indices){
            indicators[i]=ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                it.layoutParams=layoutParams
                mainBinding.indicatorContainer.addView(it)
            }
        }

    }
    //Function for navigating to next activity
    private fun navigateToNextActivity(){
        startActivity(Intent(applicationContext,SignInActivity::class.java))
        finish()

    }
}