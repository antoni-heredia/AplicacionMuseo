package com.example.antonio.aplicacionmuseo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseMuseo {
    FirebaseDatabase database;

    public FirebaseMuseo(FirebaseDatabase database) {
        this.database = database;
    }

    void traerDatosMuseo(Museo ms) {
        traerDatosColecciones(ms);
    }

    private void traerDatosColecciones(final Museo ms) {
        //Traer colecciones
        DatabaseReference referenciaColecciones = database.getReference("Coleciones");
        ValueEventListener eventListenerColecciones = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dsColeccion : dataSnapshot.getChildren()) {

                    Coleccion c = new Coleccion(Integer.parseInt(dsColeccion.getKey()));

                    for (DataSnapshot dsCampo : dsColeccion.getChildren()) {

                        if (dsCampo.getKey().equals("Nombre")) {
                            c.nombreColeccion = dsCampo.getValue(String.class);
                        } else if (dsCampo.getKey().equals("Descripcion")) {
                            c.descripcion = dsCampo.getValue(String.class);

                        }
                    }
                    ms.addColeccion(c);

                }
                traerDatosSalas(ms);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //Añadir eventlisteners
        referenciaColecciones.addValueEventListener(eventListenerColecciones);
    }


    private void traerDatosSalas(final Museo ms) {
        //Traer datos salas
        DatabaseReference referenciaSalas = database.getReference("Salas");
        ValueEventListener eventListenerSalas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //oast.makeText(getApplicationContext(),dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                for (DataSnapshot dsSala : dataSnapshot.getChildren()) {

                    Sala s = new Sala(Integer.parseInt(dsSala.getKey()));
                    for (DataSnapshot dsCampo : dsSala.getChildren()) {
                        if (dsCampo.getKey().equals("Nombre")) {
                            s.nombre = dsCampo.getValue(String.class);
                        }
                    }
                    ms.addSala(s);

                }
                traerDatosObras(ms);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        referenciaSalas.addValueEventListener(eventListenerSalas);

    }

    private void traerDatosObras(final Museo ms) {
        //Traer datos obras
        DatabaseReference referenciaObras = database.getReference("Obras");
        ValueEventListener eventListenerObras = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //oast.makeText(getApplicationContext(),dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                for (DataSnapshot dsSala : dataSnapshot.getChildren()) {

                    Obra o = new Obra(Integer.parseInt(dsSala.getKey()));
                    for (DataSnapshot dsCampo : dsSala.getChildren()) {

                        switch (dsCampo.getKey()) {
                            case "Nombre":
                                o.nombreObra = dsCampo.getValue(String.class);
                                break;
                            case "Id_coleccion":
                                o.coleccion = ms.getColeccion(dsCampo.getValue(Integer.class));
                            case "Id_sala":
                                o.sala = ms.getSala(dsCampo.getValue(Integer.class));
                                break;
                            case "Descripcion":
                                o.descripcion = dsCampo.getValue(String.class);
                                break;
                            case "URL":
                                o.url = dsCampo.getValue(String.class);
                        }

                    }
                    ms.addObra(o);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        referenciaObras.addValueEventListener(eventListenerObras);

    }
}



