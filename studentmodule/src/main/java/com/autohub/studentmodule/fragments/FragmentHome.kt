package com.autohub.studentmodule.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.autohub.skln.fragment.BaseFragment
import com.autohub.skln.listeners.ItemClickListener
import com.autohub.skln.models.AcadmicsData
import com.autohub.skln.models.HobbiesData
import com.autohub.skln.models.User
import com.autohub.skln.utills.ActivityUtils
import com.autohub.skln.utills.GlideApp
import com.autohub.studentmodule.activities.AddClassActivity
import com.autohub.studentmodule.adaptors.AcadmicsAdaptor
import com.autohub.studentmodule.adaptors.HobbiesAdaptor
import com.autohub.studentmodule.listners.HomeListners
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.autohub.studentmodule.R
import com.autohub.studentmodule.databinding.FragmentStudentHomeBinding

/**
 * Created by Vt Netzwelt
 */
class FragmentHome : BaseFragment() {
    private var mBinding: FragmentStudentHomeBinding? = null
    private var acadmicsAdaptor: AcadmicsAdaptor? = null
    private var hobbiesAdaptor: HobbiesAdaptor? = null
    private lateinit var homeListner: HomeListners
    private lateinit var user: User
    private val acadmicClickListener = ItemClickListener<AcadmicsData> {
        homeListner.onAcadmicsSelect(user, it.classname)
    }

    private val hobbiesClickListener = ItemClickListener<HobbiesData> {

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeListner = context as HomeListners
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_student_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentStudentHomeBinding.bind(view)
        mBinding!!.acadmicrecycleview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        acadmicsAdaptor = AcadmicsAdaptor(requireContext(), acadmicClickListener)
        mBinding!!.acadmicrecycleview.adapter = acadmicsAdaptor

        mBinding!!.hobbiesrecycleview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        hobbiesAdaptor = HobbiesAdaptor(requireContext(), hobbiesClickListener)
        mBinding!!.hobbiesrecycleview.adapter = hobbiesAdaptor

        setUpUserInfo()
        setupProfile()
        mBinding!!.rrGotoclass.setOnClickListener {
            ActivityUtils.launchActivity(requireContext(),
                    AddClassActivity::class.java)
        }

    }

    private fun setupProfile() {
        val ref = FirebaseStorage.getInstance().reference.child("student/" +
                firebaseAuth.currentUser!!.uid + ".jpg")
        GlideApp.with(this)
                .load(ref)
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // disable caching of glide
                .skipMemoryCache(true)
                .placeholder(com.autohub.skln.R.drawable.default_pic)
                .into(mBinding!!.profilePicture)
    }

    private fun setUpUserInfo() {
        firebaseStore.collection(getString(R.string.db_root_students)).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    user = documentSnapshot.toObject(User::class.java)!!
                    user.id = documentSnapshot.id
                    //                        User user = User.prepareUser(documentSnapshot);
                    mBinding!!.heyUser.text = String.format("Hey, \n%s.", user.firstName)
                    setSubjects(user)
                    setHobbies(user)
                    //                        mUserViewModel.setUser(user);
                }
                .addOnFailureListener { e -> showSnackError(e.message) }
    }

    private fun setSubjects(user: User) {
        acadmicsAdaptor!!.setData(user.acadmics)

    }

    private fun setHobbies(user: User) {
        hobbiesAdaptor!!.setData(user.hobbies)
    }
}