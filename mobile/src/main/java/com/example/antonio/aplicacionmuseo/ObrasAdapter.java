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


public class ObrasAdapter extends RecyclerView.Adapter<ObrasAdapter.ObraViewHolder> {
    private List<Obra> items;
    static  private Museo ms;
    public static class ObraViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView nombreColeccion;
        public TextView idObra;

        public ObraViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imgObras);
            nombre = (TextView) v.findViewById(R.id.txtSalas);
            nombreColeccion = (TextView) v.findViewById(R.id.txtDescripcionObras);
            idObra = (TextView) v.findViewById(R.id.idObra);
            v.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ms.getObraId(Integer.parseInt((String) idObra.getText()));
                    Intent intent = new Intent(v.getContext(), Informacion_sobre_obra.class);
                    intent.putExtra("museo",ms);
                    v.getContext().startActivity(intent);

                }
            });
        }
    }

    public ObrasAdapter(Museo ms, List<Obra>obras) {
        this.ms = ms;
        this.items = obras;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ObraViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_obra, viewGroup, false);
        return new ObraViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ObraViewHolder viewHolder, int i) {
        Picasso.get().load(items.get(i).url).into(viewHolder.imagen);
        viewHolder.nombre.setText(items.get(i).nombreObra);
        viewHolder.nombreColeccion.setText("Coleccion:"+String.valueOf(items.get(i).coleccion.nombreColeccion));
        viewHolder.idObra.setText(String.valueOf(items.get(i).id));;
    }
}
