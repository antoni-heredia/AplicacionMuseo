package com.example.antonio.aplicacionmuseo;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ColeccionAdapter extends RecyclerView.Adapter<ColeccionAdapter.ColeccionViewHolder> {
    private List<Coleccion> items;
    static  private Museo ms;
    public static class ColeccionViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView idColeccion;
        public ImageView compartir;
        public TextView cantidadObras;
        public ColeccionViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imgColeccion);
            nombre = (TextView) v.findViewById(R.id.txtColeccion);
            cantidadObras = (TextView) v.findViewById(R.id.cantidadObras);

            idColeccion = (TextView) v.findViewById(R.id.idColeccion);
            v.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VisualizacionObras.class);
                    intent.putExtra("museo",ms);
                    intent.putExtra("id_coleccion",Integer.parseInt((String) idColeccion.getText()));
                    v.getContext().startActivity(intent);

                }
            });

            compartir = (ImageView) v.findViewById(R.id.compartir);
            compartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Estoy viendo la coleccion "+nombre.getText()+" en el museo Fundacion Rodriguez Acosta.");
                    v.getContext().startActivity(Intent.createChooser(intent, "Share with"));
                }
            });
        }
    }

    public ColeccionAdapter(Museo ms) {
        this.ms = ms;
        this.items = ms.colecciones;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ColeccionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_coleccion, viewGroup, false);
        return new ColeccionViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ColeccionViewHolder viewHolder, int i) {
        Picasso.get().load(items.get(i).url).into(viewHolder.imagen);
        viewHolder.nombre.setText(items.get(i).nombreColeccion);
        viewHolder.idColeccion.setText(String.valueOf(items.get(i).id));
        viewHolder.cantidadObras.setText("Obras en la coleccion: "+String.valueOf(items.get(i).obras.size()));

    }
}
