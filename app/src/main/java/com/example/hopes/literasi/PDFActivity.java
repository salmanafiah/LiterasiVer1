package com.example.hopes.literasi;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

public class PDFActivity extends AppCompatActivity implements OnLoadCompleteListener, OnPageErrorListener {

    //stopwatch
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    Button startTime;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;

    String email;
    long waktuBacaset;

    ProgressBar pdfViewProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        //firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();




        final PDFView pdfView= findViewById(R.id.pdfView);
        pdfViewProgressBar=findViewById(R.id.pdfViewProgressBar);

        pdfViewProgressBar.setVisibility(View.VISIBLE);

        //UNPACK OUR DATA FROM INTENT
        Intent i=this.getIntent();
        final String path=i.getExtras().getString("PATH");

        FileLoader.with(this)
                .load(path,false) //2nd parameter is optioal, pass true to force load from network
                .fromDirectory("My_PDFs", FileLoader.DIR_INTERNAL)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        pdfViewProgressBar.setVisibility(View.GONE);
                        File pdfFile = response.getBody();
                        try {
                            pdfView.fromFile(pdfFile)
                                    .defaultPage(1)
                                    .enableAnnotationRendering(true)
                                    .onLoad(PDFActivity.this)
                                    .scrollHandle(new DefaultScrollHandle(PDFActivity.this))
                                    .spacing(10) // in dp
                                    .onPageError(PDFActivity.this)
                                    .load();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        pdfViewProgressBar.setVisibility(View.GONE);
                        Toast.makeText(PDFActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        //stopwatch
        startTime = (Button) findViewById(R.id.btnStart) ;
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Waktu membaca: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startTime.performClick();
            }
        }, 1000);
    }

    //stopwatch function
    public void startChronometer(View v) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
            long elapsedMillis = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000 ;
            //InsertData();
            Intent moveData = new Intent(PDFActivity.this, HasilBacaActivity.class);
            int waktu = (int) elapsedMillis;
            moveData.putExtra(HasilBacaActivity.EXTRA_AGE, waktu);
            Toast.makeText(PDFActivity.this, "anda telah membaca selama " + elapsedMillis + " Detik" ,Toast.LENGTH_LONG).show();
            finish();
            //startActivity(new Intent(PDFActivity.this, MainActivity.class));
            startActivity(moveData);
        }
    }
    @Override
    public void loadComplete(int nbPages) {
        pdfViewProgressBar.setVisibility(View.GONE);
        Toast.makeText(PDFActivity.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPageError(int page, Throwable t) {
        pdfViewProgressBar.setVisibility(View.GONE);
        Toast.makeText(PDFActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    //FireBase
    private Boolean validate(){
        //progressbar
        Boolean result = false;
        UserProfile user = new UserProfile();
        email = user.getUserEmail();
        waktuBacaset = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000 ;

        if (email.isEmpty()){
            Toast.makeText(this, "Masukan Data yang di Perlukan", Toast.LENGTH_SHORT).show();
        }else {
            result = true;
        }
        return result;
    }
    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
       // UserRead userRead = new UserRead(email,waktuBaca);
        //myRef.setValue(userRead);
    }

    public void InsertData(){
        UserProfile user = new UserProfile();
        String mail = user.getUserEmail();
        UserRead userread = new UserRead();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference("server/saving-data/fireblog");
        email = (user.getUserEmail());
        waktuBacaset = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000 ;

        //Creating Person object

        //Adding values
        userread.setEmail(email);
        //userread.setWaktuBaca(waktuBacaset);
        DatabaseReference newRef = ref.child("UserRead").push();
        newRef.setValue(userread);
    }
}
