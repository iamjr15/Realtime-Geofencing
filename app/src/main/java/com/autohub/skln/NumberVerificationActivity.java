package com.autohub.skln;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.autohub.skln.databinding.ActivityNumberVerificationBinding;
import com.autohub.skln.utills.ActivityUtils;
import com.autohub.skln.utills.AppConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.autohub.skln.utills.AppConstants.KEY_ACCOUNT_TYPE;
import static com.autohub.skln.utills.AppConstants.KEY_PHONE_NUMBER;
import static com.autohub.skln.utills.AppConstants.TYPE_STUDENT;
import static com.autohub.skln.utills.AppConstants.TYPE_TUTOR;

public class NumberVerificationActivity extends BaseActivity {
    private ActivityNumberVerificationBinding mBinding;
    private String mVerificationId;
    private String phoneNum;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            Toast.makeText(NumberVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String id, PhoneAuthProvider.ForceResendingToken token) {
            Toast.makeText(NumberVerificationActivity.this, R.string.sent, Toast.LENGTH_SHORT).show();
            mVerificationId = id;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_number_verification);
        mBinding.setCallback(this);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new IllegalArgumentException("Data sent is null");
        }
        phoneNum = extras.getString(AppConstants.KEY_PHONE_NUMBER);
        mBinding.codePicker.setClickable(false);
        mBinding.codePicker.setFocusable(false);
        mBinding.codePicker.setEnabled(false);
        mBinding.codePicker.registerCarrierNumberEditText(mBinding.tvPhoneNumber);
        mBinding.codePicker.setFullNumber(phoneNum);

        verifyPhoneNumber();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void verifyPhoneNumber() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    public void onNextClick() {
        if (mVerificationId == null || mBinding.pinView.getValue().length() != mBinding.pinView.getPinLength()) {
            showSnackError(R.string.enter_correct_otp);
            return;
        }
        showLoading();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mBinding.pinView.getValue());
        signInWithPhoneAuthCredential(credential);
    }

    public void onResendClick() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mBinding.codePicker.getFullNumberWithPlus(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        Toast.makeText(this, R.string.sending, Toast.LENGTH_SHORT).show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getFirebaseStore().collection(getString(R.string.db_root_all_users)).document(getFirebaseAuth().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    hideLoading();
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot snapshot = task.getResult();
                                        if (snapshot == null) {
                                            Toast.makeText(NumberVerificationActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        String phoneNumber = snapshot.getString(KEY_PHONE_NUMBER);
                                        String accountType = snapshot.getString(KEY_ACCOUNT_TYPE);

                                        if (mBinding.codePicker.getFullNumberWithPlus().equalsIgnoreCase(phoneNumber) && TYPE_TUTOR.equals(accountType)) {
                                            Intent i = new Intent(NumberVerificationActivity.this, LoginActivity.class);
                                            i.putExtra(KEY_ACCOUNT_TYPE, TYPE_TUTOR);
                                            startActivity(i);
                                            finish();
                                            return;

                                        } else if (mBinding.codePicker.getFullNumberWithPlus().equalsIgnoreCase(phoneNumber) && TYPE_STUDENT.equals(accountType)) {
                                            Intent i = new Intent(NumberVerificationActivity.this, LoginActivity.class);
                                            i.putExtra(KEY_ACCOUNT_TYPE, TYPE_STUDENT);
                                            startActivity(i);
                                            finish();
                                            return;

                                        }
                                    }
                                    getAppPreferenceHelper().setUserPhoneNumber(mBinding.codePicker.getFullNumberWithPlus());
                                    Bundle bundle = new Bundle();
                                    bundle.putString(KEY_PHONE_NUMBER, mBinding.codePicker.getFullNumberWithPlus());
                                    ActivityUtils.launchActivity(NumberVerificationActivity.this, TutorOrStudent.class, bundle);
                                    finish();
                                }
                            });
                        } else {
                            hideLoading();
                            if (task.getException() instanceof FirebaseAuthException) {
                                showSnackError(task.getException().getMessage());
                            } else {
                                showSnackError(getString(R.string.error));
                            }
                        }
                    }
                });
    }
}
