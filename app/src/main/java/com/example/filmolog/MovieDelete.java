package com.example.filmolog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;

public class MovieDelete extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Uri videoUri;
    String videotitle;
    Button btn_sil;
    StorageTask mUploadTask;

    TextView text_video_selected;

    ArrayList<String> key_al=new ArrayList<>();

    private Spinner spinner;
    private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> arrayList=new ArrayList<>();
    String silinecek_key="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_delete);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);//görünümü belirler
        getSupportActionBar().setCustomView(R.layout.actionbar);//alınacak xml dosyasını belirler

        text_video_selected=findViewById(R.id.textvideoselected);
        btn_sil = findViewById(R.id.btn_sil);
        spinner=findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        showDataSpinner();

        btn_sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movie_delete(silinecek_key);

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //String silinecek="";
        videotitle= adapterView.getItemAtPosition(i).toString();
        //videotitle=adapterView.getSelectedItem().toString();
        Toast.makeText(this, "Seçildi: "+videotitle, Toast.LENGTH_SHORT).show();
        text_video_selected.setText(videotitle);
        silinecek_key=key_al.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showDataSpinner(){
        databaseReference.child("videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                key_al.clear();
                for(DataSnapshot item: dataSnapshot.getChildren()) {

                    arrayList.add(item.child("video_isim").getValue(String.class));
                    key_al.add(item.getKey());
                }
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(MovieDelete.this,R.layout.style_spinner,arrayList);
                spinner.setAdapter(arrayAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteFileToFirebase(){

        if(text_video_selected.equals("Film Seçilmedi")){
            Toast.makeText(this, "Lütfen Bir Film Seçimi Yapın!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(mUploadTask!= null && mUploadTask.isInProgress()){
                Toast.makeText(this, "Film silme işlemi devam ediyor..", Toast.LENGTH_SHORT).show();
            }
            else{

            }
        }
    }

    private void movie_delete(String key){

        DatabaseReference film = FirebaseDatabase.getInstance().getReference("videos").child(key);
        film.removeValue();

        Toast.makeText(this,"Film Silindi",Toast.LENGTH_SHORT).show();
    }
}
