package com.example.ecommerceapp
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerceapp.Adapters.ShoesRcViewAdapter
import com.example.ecommerceapp.Models.Category
import com.example.ecommerceapp.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment() {
    private val binding: FragmentCategoryBinding by lazy { FragmentCategoryBinding.inflate(layoutInflater) }
    private lateinit var adapter: ShoesRcViewAdapter

    companion object{
        private const val CATEGORY_KEY = "CATEGORY"
        fun newInstance(category: Category): CategoryFragment {
            val fragment = CategoryFragment()
            val bundle = Bundle()
            bundle.putSerializable(CATEGORY_KEY, category)
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val category = arguments?.getSerializable(CATEGORY_KEY) as Category
        binding.categoryRecyclerView.layoutManager= GridLayoutManager(requireContext(),2)
        adapter = ShoesRcViewAdapter(category.products)
        binding.categoryRecyclerView.adapter = adapter

    }
}