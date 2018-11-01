package com.example.antonio.aplicacionmuseo;

import java.io.Serializable;
import java.util.ArrayList;

class Museo implements Serializable {

    String nombreMuseo;
    ArrayList<Coleccion> colecciones;
    ArrayList<Obra> obras;
    ArrayList<Sala> salas;
    private int posObra;
    public Museo(String nombreMuseo) {
        this.nombreMuseo = nombreMuseo;
        colecciones = new ArrayList<Coleccion>();
        obras = new ArrayList<Obra>();
        salas = new ArrayList<Sala>();
        posObra = 0;
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

    public Obra getObraSiguiente(){
        posObra++;
        if(posObra > obras.size())
            posObra  = 0;
        return obras.get(posObra);
    }

    public Obra getObraAnterior(){
        posObra--;
        if(posObra < 0)
            posObra  = obras.size()-1;
        return obras.get(posObra);
    }
    public Obra  getObraActual(){
        return obras.get(posObra);
    }

    public Obra getObraId(int idObra){
        int i = 0;
        for(Obra o : obras){
            if(o.id == idObra) {
                posObra = i;
                return o;
            }
            i++;
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


class Coleccion implements Serializable {
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

class Obra implements Serializable{
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

class Sala implements Serializable{
    int id;
    String nombre;
    ArrayList<Obra> obras;
    public Sala(int id) {
        this.id = id;
        obras = new ArrayList<Obra>();
    }
}
