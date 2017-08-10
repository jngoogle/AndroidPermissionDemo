package com.example.jngoogle.androidpermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * when you first download this project from git
 * you will find Dispatcher can't work
 * please click 「Make Project」then the project will be ready.
 * <br>
 * <p>
 * you can find Dispatcher at this location(switch to Project View):
 * <br>
 * app->build->generated->source->apt->debug->com.example.jngoogle.androidpermission
 */
@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    EditText phoneNumberEt;
    Button callBtn;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNumberEt = findViewById(R.id.et_call_phone_number);
        callBtn = findViewById(R.id.btn_call);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityPermissionsDispatcher.callPhoneNumberWithCheck(MainActivity.this);
                callPhoneNumber();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void callPhoneNumber() {
        String phoneNumber = phoneNumberEt.getText().toString().trim();
        Uri data = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL, data);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    void showRationaleForCallPhone(final PermissionRequest request) {
        new AlertDialog
                .Builder(this)
                .setTitle("Oops")
                .setMessage("grant call phone permission, if you want call")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // invoke default dialog for granting permission
                        request.proceed();
                    }
                })
                .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // invoke @OnPermissionDenied
                        request.cancel();
                    }
                })
                .show();
    }

//    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
//    void showCallPhoneDenied() {
//        Toast.makeText(MainActivity.this, "reject call phone permission, you can't call", Toast.LENGTH_SHORT).show();
//    }

    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    void onCallPhoneNeverAskAgain() {
        new AlertDialog.Builder(this)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // go to application settings
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("you have rejected call phone permission, go to grant it now ?")
                .show();
    }
}
