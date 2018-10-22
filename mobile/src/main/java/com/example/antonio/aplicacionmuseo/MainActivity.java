package com.example.antonio.aplicacionmuseo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    // Write a message to the database
    Museo ms = new Museo("Fundacion Rodriguez-Acosta");
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Codigo añadido por mi
        traerDatosMuseo();

        final Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                btnMostrarDatosClick();
            }
        });

        final Button boton = findViewById(R.id.button2);
        boton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Escribir();
            }
        });
        /****************************************************************/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void traerDatosMuseo(){
        /*
        DatabaseReference rootRef = database.getReference("Colecciones");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //oast.makeText(getApplicationContext(),dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                for(DataSnapshot dsColeccion : dataSnapshot.getChildren()) {

                    Coleccion c = new Coleccion(dsColeccion.getKey());
                    for(DataSnapshot dsObra : dsColeccion.getChildren()){

                        Obra obra = new Obra(dsObra.getKey(),dsObra.getValue(String.class));

                        c.addObra(obra);
                    }
                    ms.addColeccion(c);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"error: database", Toast.LENGTH_LONG).show();
            }
        };

        rootRef.addValueEventListener(eventListener);
        */


        traerDatosColecciones();
    }
    private void traerDatosColecciones(){
        //Traer colecciones
        DatabaseReference referenciaColecciones = database.getReference("Coleciones");
        ValueEventListener eventListenerColecciones = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dsColeccion : dataSnapshot.getChildren()) {

                    Coleccion c = new Coleccion(Integer.parseInt(dsColeccion.getKey()));

                    for(DataSnapshot dsCampo : dsColeccion.getChildren()){

                        if(dsCampo.getKey().equals("Nombre")){
                            c.nombreColeccion = dsCampo.getValue(String.class);
                        }else if (dsCampo.getKey().equals("Descripcion")){
                            c.descripcion = dsCampo.getValue(String.class);

                        }
                    }
                    ms.addColeccion(c);

                }
                traerDatosSalas();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"error: database", Toast.LENGTH_LONG).show();
            }
        };
        //Añadir eventlisteners
        referenciaColecciones.addValueEventListener(eventListenerColecciones);
    }
    private void traerDatosSalas(){
        //Traer datos salas
        DatabaseReference referenciaSalas = database.getReference("Salas");
        ValueEventListener eventListenerSalas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //oast.makeText(getApplicationContext(),dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                for(DataSnapshot dsSala : dataSnapshot.getChildren()) {

                    Sala s = new Sala(Integer.parseInt(dsSala.getKey()));
                    for(DataSnapshot dsCampo : dsSala.getChildren()){
                        if(dsCampo.getKey().equals("Nombre")){
                            s.nombre = dsCampo.getValue(String.class);
                        }
                    }
                    ms.addSala(s);

                }
                traerDatosObras();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"error: database", Toast.LENGTH_LONG).show();
            }
        };
        referenciaSalas.addValueEventListener(eventListenerSalas);

    }
    private void traerDatosObras(){
        //Traer datos obras
        DatabaseReference referenciaObras = database.getReference("Obras");
        ValueEventListener eventListenerObras = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //oast.makeText(getApplicationContext(),dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                for(DataSnapshot dsSala : dataSnapshot.getChildren()) {

                    Obra o = new Obra(Integer.parseInt(dsSala.getKey()));
                    for(DataSnapshot dsCampo : dsSala.getChildren()){

                        switch (dsCampo.getKey()){
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
                Toast.makeText(getApplicationContext(),"error: database", Toast.LENGTH_LONG).show();
            }
        };
        referenciaObras.addValueEventListener(eventListenerObras);

    }
    private void btnMostrarDatosClick(){
        Toast.makeText(getApplicationContext(), ms.obras.get(0).coleccion.toString(), Toast.LENGTH_SHORT).show();
    }

    private void Escribir(){
        // Read from the database
        DatabaseReference myRef = database.getReference("mensajeEnviado");
        myRef.setValue("Hello, World!");
    }
}
