package com.example.ecommerceapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ecommerceapp.CategoryFragment
import com.example.ecommerceapp.Models.Category

class CategoryFragmentAdapter(fa:FragmentActivity, private val categories:List<Category>)
    :FragmentStateAdapter(fa)
{
    override fun getItemCount(): Int = categories.size

    override fun createFragment(position: Int): Fragment {
        val category = categories[position]
        return CategoryFragment.newInstance(category)
    }
}