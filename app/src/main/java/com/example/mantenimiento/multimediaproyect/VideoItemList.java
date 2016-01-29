package com.example.mantenimiento.multimediaproyect;

/**
 * Created by nonek on 29/01/2016.
 */
public class VideoItemList {

    private String img;
    private String titulo;
    private String subtitulo;

    public VideoItemList (String img, String titulo, String subtitulo) {
        this.img = img;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
    }

    public String getImg() {
        return img;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setImg(String img){
        this.img=img;
    }

    public void setTitulo(String titulo){
        this.titulo=titulo;
    }

    public void setSubtitulo(String subtitulo){
        this.subtitulo=subtitulo;
    }

}
