package com.netzwelt.loginsignup.student

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.autohub.skln.BaseActivity
import com.autohub.skln.BuildConfig
import com.autohub.skln.utills.ActivityUtils
import com.autohub.skln.utills.AppConstants.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.SetOptions
import com.hbb20.CountryCodePicker
import com.netzwelt.loginsignup.R
import com.netzwelt.loginsignup.databinding.ActivityNumberVerificationBinding
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*
import java.util.concurrent.TimeUnit


class NumberVerificationActivity : BaseActivity(), TextView.OnEditorActionListener {
    private var mBinding: ActivityNumberVerificationBinding? = null
    private var mVerificationId: String? = null
    private var phoneNum: String? = null
    private var userMap: HashMap<String, Any>? = null
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            Toast.makeText(this@NumberVerificationActivity, e.message, Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
            Toast.makeText(this@NumberVerificationActivity, R.string.sent, Toast.LENGTH_SHORT).show()
            mVerificationId = id
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_number_verification)
        mBinding!!.callback = this
        val extras = intent.extras ?: throw IllegalArgumentException("Data sent is null")
        userMap = extras.getSerializable(KEY_USERMAP) as HashMap<String, Any>?
        //
        mBinding!!.etPhoneNumber.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mBinding!!.etPhoneNumber, InputMethodManager.SHOW_IMPLICIT)
        mBinding!!.codePicker.registerCarrierNumberEditText(mBinding!!.etPhoneNumber)
        mBinding!!.etPhoneNumber.setOnEditorActionListener(this)
        mBinding!!.btngetotp.setOnClickListener {
            if (mBinding!!.codePicker.isValidFullNumber) {
                phoneNum = mBinding!!.codePicker.fullNumberWithPlus
                mBinding!!.pinView.visibility = View.VISIBLE
                mBinding!!.btnResendCode.visibility = View.VISIBLE
                mBinding!!.btnNext.visibility = View.VISIBLE
                verifyPhoneNumber()
            } else {
                Toast.makeText(this@NumberVerificationActivity, R.string.enter_valid_number, Toast.LENGTH_SHORT).show()
            }
        }
        setCustomTypeface((findViewById<View>(R.id.codePicker) as CountryCodePicker).textView_selectedCountry, BaseActivity.FONT_TYPE_CERAPRO_BOLD)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun verifyPhoneNumber() {
        if (mBinding!!.codePicker.isValidFullNumber) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNum!!,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks)
            Toast.makeText(this, R.string.sending, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@NumberVerificationActivity, R.string.enter_valid_number, Toast.LENGTH_SHORT).show()
        }

    }

    fun onResendClick() {
        if (mBinding!!.codePicker.isValidFullNumber) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    mBinding!!.codePicker.fullNumberWithPlus,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks)
            Toast.makeText(this, R.string.sending, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@NumberVerificationActivity, R.string.enter_valid_number, Toast.LENGTH_SHORT).show()
        }
    }


    fun onNextClick() {

        if (mVerificationId == null || mBinding!!.pinView.value.length != mBinding!!.pinView.pinLength) {
            showSnackError(R.string.enter_correct_otp)
            return
        }
        showLoading()
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, mBinding!!.pinView.value)

        signInWithPhoneAuthCredential(credential)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        hideLoading()
                        userMap!![KEY_PHONE_NUMBER] = mBinding!!.codePicker.fullNumberWithPlus
                        showLoading()
                        linkWithCredentials()

                    } else {
                        hideLoading()
                        if (task.exception is FirebaseAuthException) {
                            showSnackError(task.exception!!.message)
                        } else {
                            showSnackError(getString(R.string.error))
                        }
                    }
                }
    }

    private fun unLinkCredentials() {
        firebaseAuth.currentUser!!.unlink(firebaseAuth.currentUser!!.providerId)
                .addOnCompleteListener(this) { linkWithCredentials() }
    }

    private fun linkWithCredentials() {
        val credential = EmailAuthProvider.getCredential(userMap!![KEY_EMAIL]!!.toString(),
                userMap!![KEY_PASSWORD]!!.toString())
        firebaseAuth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener(this@NumberVerificationActivity) { task ->
                    hideLoading()


                    if (task.isSuccessful) {
                        saveUserData()
                    } else {
                        showSnackError(task.exception!!.message)
                    }
                }
    }

    private fun saveUserData() {

        firebaseStore.collection(getString(R.string.db_root_students)).document(firebaseAuth.currentUser!!.uid).set(userMap!!, SetOptions.merge())
                .addOnSuccessListener {
                    hideLoading()
                    appPreferenceHelper.setUserPhoneNumber(mBinding!!.codePicker.fullNumberWithPlus)
                    ActivityUtils.launchActivity(this@NumberVerificationActivity,
                            StudentClassSelect::class.java)
                }
                .addOnFailureListener { e ->
                    hideLoading()
                    showSnackError(e.message)
                }

    }

    override fun onEditorAction(textView: TextView, actionId: Int, keyEvent: KeyEvent): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE && mBinding!!.codePicker.isValidFullNumber) {
            phoneNum = mBinding!!.codePicker.fullNumberWithPlus
            mBinding!!.pinView.visibility = View.VISIBLE
            mBinding!!.btnResendCode.visibility = View.VISIBLE
            mBinding!!.btnNext.visibility = View.VISIBLE
            verifyPhoneNumber()
            return true
        }
        Toast.makeText(this@NumberVerificationActivity, R.string.enter_valid_number, Toast.LENGTH_SHORT).show()
        return false
    }
}
