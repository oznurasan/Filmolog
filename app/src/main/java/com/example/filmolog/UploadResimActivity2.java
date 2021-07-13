package com.example.filmolog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadResimActivity2 extends AppCompatActivity {

    Uri videothumburi;
    String thumbnail_url;
    ImageView thumbnail_image;
    StorageReference mStoragerefthumnails;
    DatabaseReference referenceVideos;
    TextView textSelected;
    RadioButton radioButtonlatest, radioButtonpopular, radioButtonNotype, radioButtonSlide;
    StorageTask mStorageTask;
    DatabaseReference updatedataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_resim2);
        textSelected = findViewById(R.id.textNothumbnailselected);
        thumbnail_image = findViewById(R.id.imageview);
        radioButtonlatest = findViewById(R.id.radiolatestMovies);
        radioButtonpopular = findViewById(R.id.radiobestpopularMovies);
        radioButtonNotype = findViewById(R.id.radioNotype);
        radioButtonSlide = findViewById(R.id.radioSlideMovies);
        mStoragerefthumnails = FirebaseStorage.getInstance().getReference().child("VideoThumbnails");
        referenceVideos = FirebaseDatabase.getInstance().getReference().child("videos");
        String currentUid = getIntent().getExtras().getString("currentuid");
        updatedataref = FirebaseDatabase.getInstance().getReference("videos").child(currentUid);

        radioButtonNotype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String latestMovies=radioButtonlatest.getText().toString();
                updatedataref.child("video_type").setValue("");
                updatedataref.child("video_slide").setValue("");
                Toast.makeText(UploadResimActivity2.this, "Seçildi: Türü Yok", Toast.LENGTH_SHORT).show();
            }
        });

        radioButtonlatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latestMovies = radioButtonlatest.getText().toString();
                updatedataref.child("video_type").setValue(latestMovies);
                updatedataref.child("video_slide").setValue("");
                Toast.makeText(UploadResimActivity2.this, "Seçildi: " + latestMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioButtonpopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String popularMovies = radioButtonpopular.getText().toString();
                updatedataref.child("video_type").setValue(popularMovies);
                updatedataref.child("video_slide").setValue("");
                Toast.makeText(UploadResimActivity2.this, "Seçildi: " + popularMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioButtonSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String slideMovies = radioButtonSlide.getText().toString();
                updatedataref.child("video_slide").setValue(slideMovies);
                Toast.makeText(UploadResimActivity2.this, "Seçildi: " + slideMovies, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showimagechooser(View view) {
        Intent in = new Intent(Intent.ACTION_GET_CONTENT);
        in.setType("image/*");
        startActivityForResult(in, 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         // galeriden seçilen resim varsa videothumburi ye aktarıyorum.
        if (requestCode == 102 && resultCode == RESULT_OK && data.getData() != null) {

            videothumburi = data.getData();

            try{
                String thumbname=getFileName(videothumburi);
                //resim yolunun ismini yazdırıyoruz
                textSelected.setText(thumbname);
                //resmi gösteriyoruz
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),videothumburi);
                thumbnail_image.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace(); //hata mesajı
            }
        }
    }

    //dosya yolunu getirip return ediyorum
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    public void uploadfiletofirebase(View view){
        if(textSelected.equals("Resim seçili değil")){
            Toast.makeText(this, "Önce bir resim seçin", Toast.LENGTH_SHORT).show();
        }else{
            if (mStorageTask!=null && mStorageTask.isInProgress())
            {
                Toast.makeText(this, "Resim yükleme işlemi devam ediyor..", Toast.LENGTH_SHORT).show();
            }
            else
            {
                uploadFiles();
            }
        }

    }
    private void uploadFiles(){
        if(videothumburi!=null){
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Bekleyin Resim Yükleniyor..");
            progressDialog.show();
            String video_title=getIntent().getExtras().getString("thumbnailsName");

            StorageReference sRef= mStoragerefthumnails.child(video_title + "." + getfileExtension(videothumburi));

            sRef.putFile(videothumburi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            thumbnail_url=uri.toString();
                            //burada databaseye kayıt işlemi yapıyoruz
                            updatedataref.child("video_thumb").setValue(thumbnail_url);
                            progressDialog.dismiss();
                            Toast.makeText(UploadResimActivity2.this, "Dosya Yüklendi", Toast.LENGTH_SHORT).show();

                            Intent in= new Intent(UploadResimActivity2.this,MovieUpdate.class);
                            startActivity(in);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadResimActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress=(100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Yüklendi   "+ ((int)progress)+"%...");
                }
            });
        }
    }
    //bir dosyanın mime türünü (dosya biçimleri ve biçim içerikleri) algılama
    public String getfileExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMape=MimeTypeMap.getSingleton();
        return mimeTypeMape.getExtensionFromMimeType(cr.getType(uri));

    }
}
