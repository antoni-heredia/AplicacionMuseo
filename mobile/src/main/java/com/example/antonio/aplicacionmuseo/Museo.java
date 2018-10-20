package com.example.antonio.aplicacionmuseo;

import java.lang.reflect.Array;
import java.util.ArrayList;

class Museo {

    String nombreMuseo;
    ArrayList<Coleccion> colecciones;

    public Museo(String nombreMuseo) {
        this.nombreMuseo = nombreMuseo;
        colecciones = new ArrayList<Coleccion>();
    }
    public void addColeccion( Coleccion coleccion){
      colecciones.add(coleccion);
    }
}


class Coleccion {

    String nombreColeccion;
    ArrayList<Obra> obras;

    public Coleccion(String nombreColeccion) {
        this.nombreColeccion = nombreColeccion;
        obras = new ArrayList<Obra>();
    }
    public void addObra(Obra obra){
        obras.add(obra);
    }
}

class Obra{
    String nombreObra;
    String descripcion;

    public Obra(String nombreObra, String descripcion) {
        this.nombreObra = nombreObra;
        this.descripcion = descripcion;
    }
}
