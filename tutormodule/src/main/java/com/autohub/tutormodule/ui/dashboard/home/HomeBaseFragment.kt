package com.autohub.tutormodule.ui.dashboard.home


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.autohub.skln.fragment.BaseFragment
import com.autohub.tutormodule.R
import com.autohub.tutormodule.ui.dashboard.classmanager.ClassManagerFragment
import com.autohub.tutormodule.ui.dashboard.classmanager.StudentsListFragment
import com.autohub.tutormodule.ui.dashboard.listner.HomeListener
import com.autohub.tutormodule.ui.manageClasses.AddBatchFragment
import com.autohub.tutormodule.ui.manageClasses.BatchOptionsFragment

/**
 * A simple [Fragment] subclass.
 */
class HomeBaseFragment : BaseFragment() {

    internal var view: View? = null
    var homeFragment: HomeFragment? = null
    private lateinit var homeListener: HomeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_base, container, false)
        showHomefragment()
        return view

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeListener = context as HomeListener
    }

    fun showHomefragment() {
        homeFragment = HomeFragment()
        childFragmentManager
                .beginTransaction()
                .replace(R.id.homecontainer, homeFragment!!).commit()
    }

    fun showManagerFragment() {
        homeFragment = null
        childFragmentManager
                .beginTransaction()
                .replace(R.id.homecontainer, ClassManagerFragment()).commit()
    }


    fun showAddBatchFragment(showAddBatch : Boolean) {
        val bundle = Bundle()
        bundle.putBoolean("showAddBatch", showAddBatch)
        val addBatchFragment = AddBatchFragment()
        addBatchFragment.arguments = bundle
        childFragmentManager
                .beginTransaction()
                .replace(R.id.homecontainer, addBatchFragment).commit()
    }


    fun showBatchOptionsFragment() {
        val batchOptionsFragment = BatchOptionsFragment()
        childFragmentManager
                .beginTransaction()
                .replace(R.id.homecontainer, batchOptionsFragment).commit()
    }

    fun showStudentsListFragment() {
        val studentsListFragment = StudentsListFragment()
        childFragmentManager
                .beginTransaction()
                .replace(R.id.homecontainer, studentsListFragment).commit()
    }
}