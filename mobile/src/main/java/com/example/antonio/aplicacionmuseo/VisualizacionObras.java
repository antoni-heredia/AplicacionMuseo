package com.example.antonio.aplicacionmuseo;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class VisualizacionObras extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    Museo ms = new Museo("Fundacion Rodriguez-Acosta");
    private int id_sala = -1;
    private int id_coleccion= -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacion_obras);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //traemos los datos de la actividad anterior
        Intent intent = getIntent();
        ms = (Museo) intent.getSerializableExtra("museo");
        if (intent.hasExtra("id_sala"))
            id_sala = (Integer) intent.getSerializableExtra("id_sala");
        if (intent.hasExtra("id_coleccion"))
            id_coleccion = (Integer) intent.getSerializableExtra("id_coleccion");

        mostrarDatos();
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
        getMenuInflater().inflate(R.menu.visualizacion_obras, menu);
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

        if (id == R.id.nav_qr) {
            // Handle the camera action
        } else if (id == R.id.nav_obras) {
            Intent intent = new Intent(getApplicationContext(), VisualizacionObras.class);
            intent.putExtra("museo",ms);
            startActivity(intent);
        } else if (id == R.id.nav_colecciones) {

        } else if (id == R.id.nav_salas) {

        } else if (id == R.id.nav_principal) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("museo",ms);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mostrarDatos(){

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        if(id_coleccion >= 0)
            adapter = new ObrasAdapter(ms,ms.colecciones.get(id_coleccion).obras);
        else if(id_sala  >= 0)
            adapter = new ObrasAdapter(ms,ms.salas.get(id_sala).obras);
        else
            adapter = new ObrasAdapter(ms,ms.obras);

        recycler.setAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] rawMsgs = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    rawMsgs[i] = (NdefMessage) rawMessages[i];
                }
                // only one message sent during the beam
                NdefMessage msg = (NdefMessage) rawMsgs[0];
                String s = new String(msg.getRecords()[0].getPayload());
                //Elimino los 3 primeros caracteres que aparecen ya que no son validos,
                // no entiendo muy bien el porque ocurre
                manejoEtiquetaNfc(s.substring(3));
            }
        }
    }


    protected  void manejoEtiquetaNfc(String text){
        String[] registros = text.split(":");
        String tipo = registros[0];
        int id = Integer.parseInt(registros[1]);
        if(tipo.toLowerCase().equals(MainActivity.TIPO_OBRA)){
            ms.getObraId(id);
            Intent intent = new Intent(getApplicationContext(), Informacion_sobre_obra.class);
            intent.putExtra("museo",ms);
            startActivity(intent);
        }else if ( tipo.toLowerCase().equals(MainActivity.TIPO_SALA)){
            Intent intent = new Intent(getApplicationContext(), VisualizacionObras.class);
            intent.putExtra("museo",ms);
            intent.putExtra("id_sala",id);
            startActivity(intent);
        }else if ( tipo.toLowerCase().equals(MainActivity.TIPO_COLECCION)){
            Intent intent = new Intent(getApplicationContext(), VisualizacionObras.class);
            intent.putExtra("museo",ms);
            intent.putExtra("id_coleccion",id);
            startActivity(intent);
        }

    }

}
