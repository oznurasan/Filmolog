package com.example.filmolog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.apache.commons.io.FilenameUtils;

import java.util.Collections;

public class MovieUpdate extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_VIDEO=1;

    String myUrl;
    String myvideoUrl;
    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    ImageButton Filmekle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_update);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //görünümü belirler
        getSupportActionBar().setCustomView(R.layout.actionbar);

        Filmekle=findViewById(R.id.btnFilmEkle);
        Filmekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MovieUpdate.this,MainActivity.class);
                startActivity(i);
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.rv);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));

        FirebaseRecyclerOptions<MainModel> options=
                new FirebaseRecyclerOptions.Builder<MainModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("videos"),MainModel.class)
                .build();

        mainAdapter=new MainAdapter(options,MovieUpdate.this,myUrl);
        mainAdapter=new MainAdapter(options,MovieUpdate.this,myvideoUrl);
        recyclerView.setAdapter(mainAdapter);


    }


    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    int coloum_index_data;

    private String getVideoExtension(Uri uri) {

        Cursor cursor;
        String[] projection={MediaStore.MediaColumns.DATA,MediaStore.Video.Media.BUCKET_DISPLAY_NAME
                ,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA};

        final String orderby=MediaStore.Video.Media.DEFAULT_SORT_ORDER;
        cursor=MovieUpdate.this.getContentResolver().query(videoUri,projection,null,null,orderby);
        coloum_index_data=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        return orderby;

    }

    StorageTask uploadTask;

    StorageTask mUploadTask;
    Uri videoUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();


            if (imageUri != null) {
                Toast.makeText(getApplicationContext(), "Yükleniyor", Toast.LENGTH_SHORT);
                final StorageReference filereferance = FirebaseStorage.getInstance().getReference("VideoThumbnails").child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));

                uploadTask = filereferance.putFile(imageUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filereferance.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();
                            mainAdapter.setItems(myUrl);



                        } else {
                            Toast.makeText(getApplicationContext(), "Hata", Toast.LENGTH_SHORT);
                        }
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Fotoğraf Seçilmedi", Toast.LENGTH_SHORT);
            }


        }
        //filmin adını videodan çekiyoruz ve textvideoselected Text ine aktarıyoruz.
        if(requestCode==101 && resultCode== RESULT_OK && data.getData()!= null){
            videoUri=data.getData();

            String path=null;
            Cursor cursor;



            final StorageReference filevideo = FirebaseStorage.getInstance().getReference("videos").child(System.currentTimeMillis()
                    + "." + getVideoExtension(videoUri));

            mUploadTask = filevideo.putFile(videoUri);
            mUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filevideo.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myvideoUrl = downloadUri.toString();
                        mainAdapter.setVideo(myvideoUrl);



                    } else {
                        Toast.makeText(getApplicationContext(), "Hata", Toast.LENGTH_SHORT);
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Video Seçilmedi", Toast.LENGTH_SHORT);
        }

    }

    }





