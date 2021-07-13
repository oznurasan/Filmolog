package com.example.filmolog;
import androidx.annotation.NonNull;

public class MainModel {

        public String video_slide, video_type, video_thumb, video_url, video_isim, video_aciklama, video_kategori,video_oyuncu,video_imdb,video_yonetmen;

        MainModel() {

        }
        public MainModel(String video_slide, String video_type, String video_thumb, String video_url, String video_isim, String video_aciklama, String video_kategori, String video_oyuncu, String video_imdb, String video_yonetmen) {
            this.video_slide = video_slide;
            this.video_type = video_type;
            this.video_thumb = video_thumb;
            this.video_url = video_url;
            this.video_isim = video_isim;
            this.video_aciklama = video_aciklama;
            this.video_kategori = video_kategori;
            this.video_oyuncu = video_oyuncu;
            this.video_imdb = video_imdb;
            this.video_yonetmen = video_yonetmen;
        }

        public String getVideo_slide() {
            return video_slide;
        }

        public void setVideo_slide(String video_slide) {
            this.video_slide = video_slide;
        }

        public String getVideo_type() {
            return video_type;
        }

        public void setVideo_type(String video_type) {
            this.video_type = video_type;
        }

        public String getVideo_thumb() {
            return video_thumb;
        }

        public void setVideo_thumb(String video_thumb) {
            this.video_thumb = video_thumb;
        }

        public String getVideo_url() {
            return video_url;
        }

        public void setVideo_url(String video_url) {
            this.video_url = video_url;
        }

        public String getVideo_isim() {
            return video_isim;
        }

        public void setVideo_isim(String video_isim) {
            this.video_isim = video_isim;
        }

        public String getVideo_aciklama() {
            return video_aciklama;
        }

        public void setVideo_aciklama(String video_aciklama) {
            this.video_aciklama = video_aciklama;
        }

        public String getVideo_kategori() {
            return video_kategori;
        }

        public void setVideo_kategori(String video_kategori) {
            this.video_kategori = video_kategori;
        }

        public String getVideo_oyuncu() {return video_oyuncu;}

        public void setVideo_oyuncu(String video_oyuncu) { this.video_oyuncu = video_oyuncu; }

        public String getVideo_imdb() {return video_imdb;}

        public void setVideo_imdb(String video_imdb) { this.video_imdb = video_imdb; }

        public String getVideo_yonetmen() {return video_yonetmen;}

        public void setVideo_yonetmen(String video_yonetmen) { this.video_yonetmen = video_yonetmen; }


    }


