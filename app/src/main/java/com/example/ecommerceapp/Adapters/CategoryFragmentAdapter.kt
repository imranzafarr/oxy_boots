package com.example.ecommerceapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ecommerceapp.CategoryFragment
import com.example.ecommerceapp.Models.Category

// Adapter for managing category fragments in a ViewPager2
class CategoryFragmentAdapter
    (fa:FragmentActivity // The fragment activity that hosts the ViewPager2
 , private val categories:List<Category> // List of categories to display
                               )
    :FragmentStateAdapter(fa)  // Extending FragmentStateAdapter for efficient fragment management
{
    // Returns the number of items (categories) in the adapter
    override fun getItemCount(): Int = categories.size

    // Creates a new fragment instance for the given position
    override fun createFragment(position: Int): Fragment {

        // Retrieve the category at the given position
        val category = categories[position]

        // Create and return a new instance of CategoryFragment with the category data
        return CategoryFragment.newInstance(category)
    }
}

