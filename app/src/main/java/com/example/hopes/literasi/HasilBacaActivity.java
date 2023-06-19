package com.example.hopes.literasi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class HasilBacaActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    //Button-------------------------------
    private Button btnMenu;
    //inisial-Profil-----------------------
    private TextView profileName, profileAge, profileEmail, tvHasilBaca;

    public static final String EXTRA_AGE = "extra_age";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_baca);
        //Setup Data--------------------------------
        profileName = findViewById(R.id.tvProfileName);
        profileAge = findViewById(R.id.tvProfileAge);
        profileEmail = findViewById(R.id.tvProfileEmail);
        tvHasilBaca =  findViewById(R.id.tvHasilBaca);
        //End Setup Data-----------------------------
        //Database-----------------------------------
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //End DATABASE-------------------------------
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        //StorageReference storageReference = firebaseStorage.getReference();
        int waktuMembaca = getIntent().getIntExtra(EXTRA_AGE, 0);
         String text ="Telah Membaca Buku Selama "+  waktuMembaca + " Detik";
         tvHasilBaca.setText(text);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                //UserRead userRead = dataSnapshot.getValue(UserRead.class);
                profileName.setText(userProfile.getUserName());
               // profileAge.setText((int) userRead.getWaktuBaca());
                profileEmail.setText(userProfile.getUserEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(MenuSiswa.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }

        });

        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HasilBacaActivity.this, MainActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.logoutMenu:{
                Logout();
                break;
            }
            case R.id.refreshMenu:
                onRestart();
                break;
            default:
                startActivity(new Intent(HasilBacaActivity.this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(HasilBacaActivity.this, LoginSiswa.class));
    }

    @Override
    protected void onRestart() {

        // TODO Auto-generated method stub
        super.onRestart();
        Intent i = new Intent(HasilBacaActivity.this, HasilBacaActivity.class);  //your class
        startActivity(i);
        finish();

    } //Refresh Method
}

