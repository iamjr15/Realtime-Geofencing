package com.netzwelt.tutormodule.ui.dashboard.classmanager


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.autohub.skln.fragment.BaseFragment

import com.netzwelt.tutormodule.R
import com.netzwelt.tutormodule.databinding.FragmentStudentsListBinding

/**
 * A simple [Fragment] subclass.
 */
class StudentsListFragment : BaseFragment() {

    private lateinit var mAdapter: StudentListAdaptor


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_students_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentStudentsListBinding.bind(view)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerView.setEmptyView(binding.rrempty)
        mAdapter = StudentListAdaptor(requireContext())
        binding.recyclerView.adapter = mAdapter
    }

}
