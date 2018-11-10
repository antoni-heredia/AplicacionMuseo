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


public class SalasAdapter extends RecyclerView.Adapter<SalasAdapter.SalasViewHolder> {
    private List<Sala> items;
    static  private Museo ms;
    public static class SalasViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView nombreColeccion;
        public TextView idSala;
        public ImageView compartir;

        public SalasViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imgSalas);
            nombre = (TextView) v.findViewById(R.id.txtSalas);
            nombreColeccion = (TextView) v.findViewById(R.id.txtDescripcionSala);
            idSala = (TextView) v.findViewById(R.id.idSala);
            v.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), VisualizacionObras.class);
                    intent.putExtra("museo",ms);
                    intent.putExtra("id_sala",Integer.parseInt((String) idSala.getText()));
                    v.getContext().startActivity(intent);

                }
            });

            compartir = (ImageView) v.findViewById(R.id.compartir);
            compartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Estoy en la sala "+nombre.getText()+"del museo Fundacion Rodriguez Acosta.");
                    v.getContext().startActivity(Intent.createChooser(intent, "Share with"));
                }
            });
        }
    }

    public SalasAdapter(Museo ms) {
        this.ms = ms;
        this.items = ms.salas;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public SalasViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_sala, viewGroup, false);
        return new SalasViewHolder(v);
    }



    @Override
    public void onBindViewHolder(SalasViewHolder viewHolder, int i) {
        Picasso.get().load(items.get(i).url).into(viewHolder.imagen);
        viewHolder.nombre.setText(items.get(i).nombre);
        viewHolder.nombreColeccion.setText("Obras en la coleccion: "+items.get(i).obras.size());
        viewHolder.idSala.setText(String.valueOf(items.get(i).id));;
    }
}
