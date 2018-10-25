package com.example.antonio.aplicacionmuseo;

import android.os.Bundle;
import android.support.constraint.Constraints;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
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
        FirebaseMuseo msfire = new FirebaseMuseo(database);
        msfire.traerDatosMuseo(ms);
        WebView wb = findViewById(R.id.wbview);
        wb.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
                Toast.makeText(getApplicationContext(), "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                ms.getObraSiguiente();
                mostrarDatos();
            }
            public void onSwipeLeft() {
                ms.getObraAnterior();
                mostrarDatos();
            }
            public void onSwipeBottom() {
                Toast.makeText(getApplicationContext(), "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        final Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mostrarDatos();
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
            mostrarDatos();
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

/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_POINTER_DOWN):
                idObraActual ++;
                btnMostrarDatosClick();
                Toast.makeText(getApplicationContext(), "action down", Toast.LENGTH_SHORT).show();

                return true;
            case (MotionEvent.ACTION_MOVE):
                Toast.makeText(getApplicationContext(), "action move", Toast.LENGTH_SHORT).show();
                return true;
            case (MotionEvent.ACTION_UP):
                Toast.makeText(getApplicationContext(), "action up", Toast.LENGTH_SHORT).show();

                return true;
            case (MotionEvent.ACTION_CANCEL):
                Toast.makeText(getApplicationContext(), "action cancel", Toast.LENGTH_SHORT).show();

                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Toast.makeText(getApplicationContext(), "action outside", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onTouchEvent(event);
        }

    }
*/
    private void mostrarDatos(){

        WebView wb = findViewById(R.id.wbview);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        String data = "<html><head><title>Example</title><meta name=\"viewport\"\" /></head>";
        data = data + "<body><center><img width=\""+width+"\" src=\""+ms.getObraActual().url+"\" /></center></body></html>";
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.loadData(data, "text/html", null);

        TextView descripcion = findViewById(R.id.txtDescripcion);
        descripcion.setText(ms.getObraActual().descripcion);

    }

    private void Escribir(){
        // Read from the database
        DatabaseReference myRef = database.getReference("mensajeEnviado");
        myRef.setValue("Hello, World!");
    }
}
