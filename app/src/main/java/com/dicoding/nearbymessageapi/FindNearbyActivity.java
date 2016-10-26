package com.dicoding.nearbymessageapi;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FindNearbyActivity extends AppCompatActivity
    implements EasyPermissions.PermissionCallbacks,
        View.OnClickListener{
    private static final int RC_EMAIL_PERM = 122;
    public static final String TAG = "NearbyApp";

    private Button btnFind;
    private TextView tvUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);

        btnFind = (Button)findViewById(R.id.btn_find);
        btnFind.setOnClickListener(this);

        getSupportActionBar().setTitle("Around You");

        tvUserEmail = (TextView)findViewById(R.id.tv_user_email);
        getPhoneEmail();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_EMAIL_PERM)
    private void getPhoneEmail() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            tvUserEmail.setText("Hola,, "+MainActivity.getEmail(this));
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_email),
                    RC_EMAIL_PERM, Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission email granted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG,"Permission email denied");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_find){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
