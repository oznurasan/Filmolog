package com.example.filmolog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageTask;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class MainAdapter extends FirebaseRecyclerAdapter<MainModel, MainAdapter.myViewHolder> implements AdapterView.OnItemSelectedListener {

    String videotitle;
    //private Spinner spinner;
    String silinecek_key = "";
    ArrayList<String> key_al = new ArrayList<>();
    public String kategori_title;
    public EditText video_kategori;
    private Context mContext;
    private String imageUrl;
    private String videoUrl;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_VIDEO=1;


    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> arrayList = new ArrayList<>();

    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options, Context mContext, String myImageUrl) {
        super(options);
        this.imageUrl = myImageUrl;
        this.mContext = mContext;
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */


    public MainAdapter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        kategori_title = adapterView.getItemAtPosition(position).toString();
        video_kategori.setText(kategori_title);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setItems(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideo(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull MainModel model) {

        holder.video_isim.setText(model.getVideo_isim());
        holder.video_yonetmen.setText((model.getVideo_yonetmen()));
        holder.video_kategori.setText(model.getVideo_kategori());


        Glide.with(holder.img.getContext())
                .load(model.getVideo_thumb())
                .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true, 1200)
                        .create();

                //dialogPlus.show();

                View view = dialogPlus.getHolderView();

                EditText video_isim = view.findViewById(R.id.video_isim);
                EditText video_aciklama = view.findViewById(R.id.video_aciklama);
                EditText video_yonetmen = view.findViewById(R.id.video_yonetmen);
                EditText video_imdb = view.findViewById(R.id.video_imdb);
                EditText video_oyuncu = view.findViewById(R.id.video_oyuncu);
                EditText video_kategori = view.findViewById(R.id.video_kategori);
                EditText video_thumb = view.findViewById(R.id.video_thumb);
                EditText video_url = view.findViewById(R.id.video_url);
                Spinner spinner = view.findViewById(R.id.spinner);

                Button btnUpdate = view.findViewById(R.id.btnUpdate);
                Button btnResim = view.findViewById(R.id.btnResim);
                Button btnVideo = view.findViewById(R.id.btnVideo);


                video_isim.setText(model.getVideo_isim());
                video_aciklama.setText(model.getVideo_aciklama());
                video_yonetmen.setText(model.getVideo_yonetmen());
                video_imdb.setText(model.getVideo_imdb());
                video_oyuncu.setText(model.getVideo_oyuncu());
                video_thumb.setText(model.getVideo_thumb());
                video_url.setText(model.getVideo_url());


                dialogPlus.show();

                btnResim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.putExtra("movieId", getRef(position).getKey());
                        ((Activity) mContext).startActivityForResult(i, RESULT_LOAD_IMAGE);


                    }
                });

                btnVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        i.putExtra("movieId", getRef(position).getKey());
                        ((Activity) mContext).startActivityForResult(i, RESULT_LOAD_VIDEO);
                    }
                });


                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("video_isim", video_isim.getText().toString());
                        map.put("video_aciklama", video_aciklama.getText().toString());
                        map.put("video_yonetmen", video_yonetmen.getText().toString());
                        map.put("video_imdb", video_imdb.getText().toString());
                        map.put("video_oyuncu", video_oyuncu.getText().toString());
                        map.put("video_kategori", spinner.getSelectedItem().toString());
                        if (imageUrl==null)
                            map.put("video_thumb", video_thumb.getText().toString());
                        else
                            map.put("video_thumb", imageUrl);

                        if (videoUrl== null)
                            map.put("video_url", video_url.getText().toString());
                        else
                            map.put("video_url", videoUrl);

                        FirebaseDatabase.getInstance().getReference().child("videos")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(holder.video_isim.getContext(), "Film Başarıyla Güncellendi.", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.video_isim.getContext(), "Güncelleme Başarısız.", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });

                    }
                });
            }
        });
        holder.btnSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.video_isim.getContext());
                builder.setTitle("Silmek İstediğinize Emin Misiniz ?");
                builder.setMessage("Uyarı: Silme işlemi geri alınamaz");

                builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("videos")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.video_isim.getContext(), "İptal Edildi.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new myViewHolder(view);

    }


    class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView video_isim, video_yonetmen, video_kategori;
        Button btnEdit, btnSil;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (CircleImageView) itemView.findViewById(R.id.img1);
            video_isim = (TextView) itemView.findViewById(R.id.video_isim);
            video_yonetmen = (TextView) itemView.findViewById(R.id.video_yonetmen);
            video_kategori = (TextView) itemView.findViewById(R.id.video_kategori);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
            btnSil = (Button) itemView.findViewById(R.id.btnSil);

        }
    }


}
