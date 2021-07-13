package com.example.filmolog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AnaEkran extends AppCompatActivity {
    private ImageView ekle,sil,guncelle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_ekran);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);//görünümü belirler
        getSupportActionBar().setCustomView(R.layout.actionbar);//alınacak xml dosyasını belirler

        ekle=findViewById(R.id.ekle);
        ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AnaEkran.this,MainActivity.class);
                startActivity(i);
            }
        });
        sil=findViewById(R.id.sil);
        sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AnaEkran.this,MovieDelete.class);
                startActivity(i);
            }
        });
        guncelle=findViewById(R.id.guncelle);
        guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AnaEkran.this,MovieUpdate.class);
                startActivity(i);
            }
        });
    }
}