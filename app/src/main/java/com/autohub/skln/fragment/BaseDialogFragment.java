package com.autohub.skln.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;

import com.autohub.skln.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 2019-08-01.
 */
public class BaseDialogFragment extends DialogFragment {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    protected FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    protected FirebaseFirestore getFirebaseStore() {
        return mFirebaseFirestore;
    }

    protected final void showSnackError(String message) {
        if (requireActivity() instanceof BaseActivity) {
            ((BaseActivity) requireActivity()).showSnackError(message);
        }
    }

    protected final void showSnackError(@StringRes int message) {
        if (requireActivity() instanceof BaseActivity) {
            ((BaseActivity) requireActivity()).showSnackError(message);
        }
    }


    protected void showLoading() {
        if (requireActivity() instanceof BaseActivity) {
            ((BaseActivity) requireActivity()).showLoading();
        }
    }

    protected void hideLoading() {
        if (requireActivity() instanceof BaseActivity) {
            ((BaseActivity) requireActivity()).hideLoading();
        }
    }


    protected boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

}
