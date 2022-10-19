package com.example.uniplus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ElementoLista extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context contexto;
    String[] titulos;
    int[] imagenes;

    public ElementoLista(Context contexto, String[] textos, int[]imagenes){
        this.contexto = contexto;
        this.titulos = textos;
        this.imagenes = imagenes;

        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        final View vista = inflater.inflate(R.layout.list_item, null);
        TextView texto = (TextView) vista.findViewById(R.id.nombreEspacio);
        ImageView imagen = (ImageView) vista.findViewById(R.id.imagenEspacio);

        texto.setText(titulos[i]);
        imagen.setImageResource(imagenes[i]);

        /*
        imagen.setTag(i);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent visorImagen = new Intent(contexto, VisorImagen.class);
                visorImagen.putExtra("IMG", imagenes[(Integer)view.getTag()]);
                contexto.startActivity(visorImagen);
            }
        }); */

        return vista;
    }

    @Override
    public int getCount() {
        return imagenes.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
