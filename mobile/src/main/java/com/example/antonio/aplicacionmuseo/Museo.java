package com.example.antonio.aplicacionmuseo;

import java.util.ArrayList;

class Museo {

    String nombreMuseo;
    ArrayList<Coleccion> colecciones;
    ArrayList<Obra> obras;
    ArrayList<Sala> salas;
    public Museo(String nombreMuseo) {
        this.nombreMuseo = nombreMuseo;
        colecciones = new ArrayList<Coleccion>();
        obras = new ArrayList<Obra>();
        salas = new ArrayList<Sala>();
    }

    public void addColeccion( Coleccion coleccion){
      colecciones.add(coleccion);
    }

    public void addObra(Obra obra) { obras.add(obra);}
    public void addSala(Sala sala){ salas.add(sala);}
    public Coleccion getColeccion(int id){
        for(Coleccion c : colecciones){
            if(c.id == id)
                return c;
        }
        return null;
    }

    public Obra getObra(int id){
        for(Obra o : obras){
            if(o.id == id)
                return o;
        }
        return null;
    }

    public Sala getSala(int id){
        for(Sala s : salas){
            if(s.id == id)
                return s;
        }
        return null;
    }

}


class Coleccion {
    int id;
    String nombreColeccion;
    String descripcion;
    ArrayList<Obra> obras;

    public Coleccion(int id) {
        this.id = id;
        obras = new ArrayList<Obra>();
    }
    public void addObra(Obra obra){
        obras.add(obra);
    }

    public String toString(){
        return "Nombre: "+ nombreColeccion;
    }
}

class Obra{
    int id;
    String url;
    String nombreObra;
    String descripcion;
    Coleccion coleccion;
    Sala sala;

    public Obra(int id) {
        this.id = id;
    }



}

class Sala {
    int id;
    String nombre;
    ArrayList<Obra> obras;
    public Sala(int id) {
        this.id = id;
        obras = new ArrayList<Obra>();
    }
}
