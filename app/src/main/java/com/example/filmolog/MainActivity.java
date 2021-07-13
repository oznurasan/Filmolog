package com.example.filmolog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filmolog.Model.VideoUploadDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Uri videoUri;
    TextView text_video_selected;
    String videoCategory;
    String videotitle;
    String currentuid;
    Button uploadVideoButton;
    Button selectVideo;
    //StorageReference mstorageRef;
    StorageTask mUploadTask;
   // DatabaseReference referenceVideos;
    EditText video_description,video_oyuncu,video_yonetmen,video_imdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_video_selected=findViewById(R.id.textvideoselected);
        video_description=findViewById(R.id.movies_description);
        video_yonetmen=findViewById(R.id.movies_yonetmen);
        video_oyuncu=findViewById(R.id.movies_oyuncu);
        video_imdb=findViewById(R.id.movies_imdb);
        uploadVideoButton = findViewById(R.id.buttonUpload);
        selectVideo =  findViewById(R.id.uploads_video_btn);
        //referenceVideos= FirebaseDatabase.getInstance().getReference("videos");
        //mstorageRef= FirebaseStorage.getInstance().getReference("videos");

        Spinner spinner=findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> categories=new ArrayList<>();
        categories.add("Aksiyon");
        categories.add("Komedi");
        categories.add("Romantik");
        categories.add("Korku");
        categories.add("Macera");
        categories.add("Bilim Kurgu");
        categories.add("Fantastik");
        categories.add("Gerilim");
        categories.add("Belgesel");
        categories.add("Biyografi");
        categories.add("Polisiye");
        categories.add("Psikolojik");


        ArrayAdapter<String> dataAdpter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categories);
        dataAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdpter);


        uploadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadFileToFirebase();
            }
        });
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openvideoFiles();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        videoCategory= adapterView.getItemAtPosition(i).toString();

        Toast.makeText(this, "Seçildi: "+videoCategory, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void openvideoFiles(){
        Intent in=new Intent(Intent.ACTION_GET_CONTENT);
        in.setType("video/*");
        startActivityForResult(in,101);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //filmin adını videodan çekiyoruz ve textvideoselected Text ine aktarıyoruz.
        if(requestCode==101 && resultCode== RESULT_OK && data.getData()!= null){
            videoUri=data.getData();

            String path=null;
            Cursor cursor;
            int coloum_index_data;
            String[] projection={MediaStore.MediaColumns.DATA,MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            ,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA};
            final String orderby=MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            cursor=MainActivity.this.getContentResolver().query(videoUri,projection,null,null,orderby);
            coloum_index_data=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while(cursor.moveToNext()){
                path=cursor.getString(coloum_index_data);
                videotitle= FilenameUtils.getBaseName(path);
              }
             text_video_selected.setText(videotitle);


        }
    }
    public void uploadFileToFirebase(){

        if(text_video_selected.equals("Video Seçilmedi")){
            Toast.makeText(this, "Lütfen Video Seçimi Yapın!", Toast.LENGTH_SHORT).show();
          }
        else{
            if(mUploadTask!= null && mUploadTask.isInProgress()){
                Toast.makeText(this, "Video yükleme işlemi devam ediyor..", Toast.LENGTH_SHORT).show();
            }
            else{
                uploadFiles();
            }
        }
    }

    private void uploadFiles(){
        if(videoUri!=null){
            StorageReference storageReference=FirebaseStorage.getInstance().getReference("videos").child(videotitle);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://filmolog-668bc-default-rtdb.firebaseio.com/").getReference().child("videos").child(videotitle);
            UploadTask uploadTask = storageReference.putFile(videoUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override

                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override

                        public void onSuccess(Uri uri) {

                            String video_url=uri.toString();

                            VideoUploadDetails videoUploadDetails=new VideoUploadDetails("","","",
                                    video_url , videotitle,video_description.getText().toString(),videoCategory,video_oyuncu.getText().toString(),video_yonetmen.getText().toString(),video_imdb.getText().toString());
                            String uploadsid=FirebaseDatabase.getInstance().getReference("videos").push().getKey();
                            FirebaseDatabase.getInstance().getReference("videos").child(uploadsid).setValue(videoUploadDetails);
                            currentuid=uploadsid;

                            if(task.isSuccessful())
                                Toast.makeText(MainActivity.this, "Yüklendi", Toast.LENGTH_SHORT).show();
                            if(currentuid.equals(uploadsid)){
                                startThumbnailActivity();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Yüklemede Hata", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Yüklenecek video seçili değil.", Toast.LENGTH_SHORT).show();
        }

    }

    public void startThumbnailActivity(){
        Intent in= new Intent(MainActivity.this,UploadResimActivity2.class);
        in.putExtra("currentuid",currentuid);
        in.putExtra("thumbnailsName",videotitle);
        startActivity(in);
        Toast.makeText(this, "Video Yüklemesi Başarılı. Şimdi Resim Yükleyin", Toast.LENGTH_SHORT).show();


    }
}