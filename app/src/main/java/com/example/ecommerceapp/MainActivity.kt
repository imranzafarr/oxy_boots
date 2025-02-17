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
    // Adapter for onboarding items
    private lateinit var onboardingItemsAdapter: OnBoardingItemsAdapter

    // View binding for accessing UI components efficiently
    private val mainBinding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)  // Sets the root view using view binding

        // Adjusts padding to prevent UI elements from overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize and set up the onboarding items for the ViewPager
        setOnboardingItems()

        // Set up the indicator dots at the bottom
        setupIndicators()

        // Highlight the first indicator as the default selection
        setCurrentIndicators(0)
    }

    /**
     * Updates the indicator UI to show which onboarding page is currently active.
     * Also updates the text of the next button.
     */
    @SuppressLint("SetTextI18n")
    private fun setCurrentIndicators(position: Int) {

        val childCount=mainBinding.indicatorContainer.childCount

        // Loop through all indicators and update their drawable based on the current position
        for (i in 0 until childCount){

            val imageView=mainBinding.indicatorContainer.getChildAt(i) as ImageView

            if(i==position){
                // Set active indicator drawable for the selected position
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            }

            else
            {
                // Set inactive indicator drawable for unselected positions
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive))
            }
        }

        // Update the next button text based on the current onboarding item
        mainBinding.nextButton.text = if (position == 0) "Get Started" else "Next"
    }

    /**
     * Initializes the onboarding ViewPager with a list of onboarding items and
     * sets up a page change listener to update indicators accordingly.
     */
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

        // Set adapter for ViewPager
        mainBinding.onBoardingViewPager.adapter=onboardingItemsAdapter

        // Registering a listener to detect page changes and update indicators
        mainBinding.onBoardingViewPager.registerOnPageChangeCallback(object:

        ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Update indicator based on the new selected page
                setCurrentIndicators(position)
            }
        })

        // Disable overscroll effect on ViewPager to prevent the glow effect when swiping
        (mainBinding.onBoardingViewPager.getChildAt(0)
                as RecyclerView).overScrollMode= RecyclerView.OVER_SCROLL_NEVER

        // Set click listener for the "Next" button
        mainBinding.nextButton.setOnClickListener {
            if (mainBinding.onBoardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                mainBinding.onBoardingViewPager.currentItem += 1
            } else {
                // If on the last page, navigate to the SignInActivity
                navigateToNextActivity()
            }


        }}

    /**
     * Creates and sets up indicator dots for each onboarding item.
     */
    private fun setupIndicators(){
        val indicators= arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams= LayoutParams(WRAP_CONTENT,WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0) // Add margin between indicators

        // Create indicators dynamically based on the number of onboarding items
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
                // Add indicator to the container
                mainBinding.indicatorContainer.addView(it)
            }
        }

    }

    /**
     * Navigates to the SignInActivity after the onboarding process is completed.
     */
    private fun navigateToNextActivity(){
        startActivity(Intent(applicationContext,SignInActivity::class.java))
        finish() // Close the onboarding activity to prevent returning to it

    }
}
