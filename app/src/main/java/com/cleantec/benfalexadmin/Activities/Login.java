package com.cleantec.benfalexadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cleantec.benfalexadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText etEmail,etPassword;
    Button btnLogin;
    FirebaseAuth firebaseAuth;
    String strEmail,strPassword;
    ProgressDialog progressDialog;
    SharedPreferences.Editor sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        views();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail=etEmail.getText().toString();
                strPassword=etPassword.getText().toString();
                if(strEmail.length()<5)
                {
                    etEmail.setError("Invalid Email");
                }
                else if (strPassword.length()<6)
                {
                    etPassword.setError("Password too short");
                }
                else{
                    LoginAdmin();
                }
            }
        });
    }

    private void views() {
        etEmail=findViewById(R.id.et_email);
        etPassword=findViewById(R.id.et_password);
        btnLogin=findViewById(R.id.btn_login);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        sharedPref=getSharedPreferences("admin",MODE_PRIVATE).edit();
    }

    public void LoginAdmin(){
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                sharedPref.putString("adminId",user.getUid()+"");
                sharedPref.apply();
                startActivity(new Intent(Login.this,MainActivity.class));
                progressDialog.dismiss();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Login.this, "No account found", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
    }
}