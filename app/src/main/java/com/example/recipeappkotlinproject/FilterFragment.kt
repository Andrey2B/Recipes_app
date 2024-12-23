package com.example.recipeappkotlinproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class FilterFragment : Fragment() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeFilter: ImageView = view.findViewById(R.id.imageView8)

        closeFilter.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this@FilterFragment) //Delete current fragment
                .commit()
        }

    }

    companion object {

        @JvmStatic
        fun newInstance() = FilterFragment()
    }
}