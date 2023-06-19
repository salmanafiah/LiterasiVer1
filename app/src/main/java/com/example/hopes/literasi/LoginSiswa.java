package com.example.hopes.literasi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSiswa extends AppCompatActivity implements View.OnClickListener {

    private EditText Name;
    private EditText Password;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_siswa);

        Name = (EditText)findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etPassword);

        Login = (Button)findViewById(R.id.btnLogin);
        userRegistration = (TextView)findViewById(R.id.tvRegister);
        forgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
       // Login.setEnabled(false);

        forgotPassword.setOnClickListener(this);


       /* Name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    Login.setEnabled(false);
                } else {
                    Login.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                Login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        validate(Name.getText().toString(), Password.getText().toString());
                    }
                });
            }
        }); */




        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            finish();
            startActivity(new Intent(LoginSiswa.this, MainActivity.class));
        }

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginSiswa.this, LoginPendaftaran.class));
            }
        });



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String userName, String userPassword) {
        final String eName = Name.getText().toString();
        final String ePassword = Password.getText().toString();
        if (eName.isEmpty() || ePassword.isEmpty()){ Toast.makeText(LoginSiswa.this, "Masukan Data yang di Perlukan", Toast.LENGTH_SHORT).show(); }
        else {
            progressDialog.setMessage("Mohon Tunggu");
            progressDialog.show();
            //menggunakan FireBase
            firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginSiswa.this, "Login Sukses", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(LoginSiswa.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginSiswa.this, "Login Gagal", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvForgotPassword){
            startActivity(new Intent(LoginSiswa.this, PasswordActivity.class));
        }
    }
}
