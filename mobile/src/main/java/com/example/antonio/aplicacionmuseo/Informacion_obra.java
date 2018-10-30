package com.example.antonio.aplicacionmuseo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class Informacion_obra extends AppCompatActivity {

    private TextView mTextMessage;
    private Museo ms;

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    SimpleTwoFingerDoubleTapDetector multiTouchListener = new SimpleTwoFingerDoubleTapDetector() {
        @Override
        public void onTwoFingerDoubleTap() {
            // Do what you want here, I used a Toast for demonstration
            Toast.makeText(getApplicationContext(), "Two Finger Double Tap", Toast.LENGTH_SHORT).show();
        }
    };

    // Override onCreate() and anything else you want


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(multiTouchListener.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_obra);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //traemos los datos de la actividad anterior
        Intent intent = getIntent();
        ms = (Museo) intent.getSerializableExtra("museo");
        ConstraintLayout cl = findViewById(R.id.layaoutObra);
        cl.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeTop() {
                Toast.makeText(getApplicationContext(), "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                ms.getObraSiguiente();
                loadObraActual();
            }
            public void onSwipeLeft() {
                ms.getObraAnterior();
                loadObraActual();
            }
            public void onSwipeBottom() {
                Toast.makeText(getApplicationContext(), "bottom", Toast.LENGTH_SHORT).show();
            }

        });




        loadObraActual();

        //hacer que el texto sea scrolleable
        //TextView txt = findViewById(R.id.descripcionObra);
        //txt.setMovementMethod(new ScrollingMovementMethod());




    }



    private void loadObraActual(){
        ImageView iv = findViewById(R.id.imagenObra);
        Picasso.get().load(ms.getObraActual().url).into(iv);
        TextView txt = findViewById(R.id.descripcionObra);
        txt.setText(ms.getObraActual().descripcion);
        setTitle(ms.getObraActual().nombreObra);
    }
}
