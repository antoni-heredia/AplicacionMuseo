package com.example.antonio.aplicacionmuseo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import in.championswimmer.sfg.lib.SimpleFingerGestures;

import static com.example.antonio.aplicacionmuseo.MainActivity.LOGTAG;

public class visualizacion_salas extends VoiceActivity
        implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor mProximity;
    private static final int SENSOR_SENSITIVITY = 4;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 400;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    Museo ms = new Museo("Fundacion Rodriguez-Acosta");
    private int id_sala = -1;
    private int id_coleccion= -1;
    public AIDataService aiDataService=null;
    private long startListeningTime = 0; // To skip errors (see processAsrError method)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacion_salas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.id_listener);
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
        Intent intent = getIntent();
        ms = (Museo) intent.getSerializableExtra("museo");
        mostrarDatos();

        //Dialogo flow
        //Initialize the speech recognizer and synthesizer
        initSpeechInputOutput(this);

        //Set up the speech button
        setSpeakButton();

        //Dialogflow configuration parameters
        final AIConfiguration config = new AIConfiguration(MainActivity.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(config);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        mProximity = senSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        senSensorManager.registerListener(this, mProximity , SensorManager.SENSOR_DELAY_NORMAL);

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 200) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    cambiarEstadoBoton();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

            if (sensorEvent.values[0] >= -SENSOR_SENSITIVITY && sensorEvent.values[0] <= SENSOR_SENSITIVITY) {
                cambiarEstadoBoton();
            }
        }
    }
    private void cambiarEstadoBoton(){

        shutdown();
        iniciarTTS();
        FloatingActionButton speak = findViewById(R.id.id_listener);

        speak.setEnabled(true);
        speak.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        speak.setRippleColor(ColorStateList.valueOf(Color.GREEN));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        getMenuInflater().inflate(R.menu.visualizacion_salas, menu);
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
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com.br/maps/place/Fundaci%C3%B3n+Rodr%C3%ADguez-Acosta/@37.1749117,-3.5943478,17z/data=!3m1!4b1!4m5!3m4!1s0xd71fcb9c6c9db65:0xfa1f0e636168d728!8m2!3d37.1749074!4d-3.5921591"));
            startActivity(intent);
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

            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {// Marshmallow+
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No se necesita dar una explicación al usuario, sólo pedimos el permiso.
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, MainActivity.MY_PERMISSIONS_REQUEST_CAMARA );
                        // MY_PERMISSIONS_REQUEST_CAMARA es una constante definida en la app. El método callback obtiene el resultado de la petición.
                    }
                }else{ //have permissions
                    Intent i = new Intent(getApplicationContext(),QrCodeActivity.class);
                    startActivityForResult( i,MainActivity.REQUEST_CODE_QR_SCAN);                }
            }else{ // Pre-Marshmallow
                Intent i = new Intent(getApplicationContext(),QrCodeActivity.class);
                startActivityForResult( i,MainActivity.REQUEST_CODE_QR_SCAN);            }


        } else if (id == R.id.nav_obras) {
            Intent intent = new Intent(getApplicationContext(), VisualizacionObras.class);
            intent.putExtra("museo",ms);
            startActivity(intent);
        } else if (id == R.id.nav_colecciones) {
            Intent intent = new Intent(getApplicationContext(), visualizacionColecciones.class);
            intent.putExtra("museo",ms);
            startActivity(intent);
        } else if (id == R.id.nav_salas) {

        } else if (id == R.id.nav_principal) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("museo",ms);
            startActivity(intent);
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
        adapter = new SalasAdapter(ms);
        recycler.setAdapter(adapter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == MainActivity.REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            manejoEtiquetaNfc(result);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MainActivity.MY_PERMISSIONS_REQUEST_CAMARA : {
                // Si la petición es cancelada, el array resultante estará vacío.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // El permiso ha sido concedido.
                    Intent i = new Intent(getApplicationContext(),QrCodeActivity.class);
                    startActivityForResult( i,MainActivity.REQUEST_CODE_QR_SCAN);
                } else {
                    // Permiso denegado, deshabilita la funcionalidad que depende de este permiso.
                }
                return;
            }
            // otros bloques de 'case' para controlar otros permisos de la aplicación
        }
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

    protected void manejoEtiquetaNfc(String text){
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


    //Todo lo referente a dialogoflow
    /**
     * Initializes the search button and its listener. When the button is pressed, a feedback is shown to the user
     * and the recognition starts
     */
    private void setSpeakButton() {
        // gain reference to speak button
        FloatingActionButton speak = findViewById(R.id.id_listener);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask the user to speak
                try {
                    FloatingActionButton speak = findViewById(R.id.id_listener);
                    speak.setEnabled(false);
                    speak(getResources().getString(R.string.initial_prompt), "ES", MainActivity.ID_PROMPT_QUERY);

                } catch (Exception e) {
                    Log.e(LOGTAG, "TTS not accessible");
                }
            }
        });
    }

    /**
     * Explain to the user why we need their permission to record audio on the device
     * See the checkASRPermission in the VoiceActivity class
     */
    public void showRecordPermissionExplanation() {
        Toast.makeText(getApplicationContext(), R.string.asr_permission, Toast.LENGTH_SHORT).show();
    }

    /**
     * If the user does not grant permission to record audio on the device, a message is shown and the app finishes
     */
    public void onRecordAudioPermissionDenied() {
        Toast.makeText(getApplicationContext(), R.string.asr_permission_notgranted, Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    /**
     * Starts listening for any user input.
     * When it recognizes something, the <code>processAsrResult</code> method is invoked.
     * If there is any error, the <code>onAsrError</code> method is invoked.
     */
    private void startListening() {
        if (deviceConnectedToInternet()) {
            try {

                /*Start listening, with the following default parameters:
                 * Language = English
                 * Recognition model = Free form,
                 * Number of results = 1 (we will use the best result to perform the search)
                 */
                startListeningTime = System.currentTimeMillis();
                Locale spanish = new Locale("es", "ES");
                listen(spanish, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, 1); //Start
            } catch (Exception e) {
                this.runOnUiThread(new Runnable() {  //Toasts must be in the main thread
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.asr_notstarted, Toast.LENGTH_SHORT).show();
                        changeButtonAppearanceToDefault();
                    }
                });

                Log.e(LOGTAG, "ASR could not be started");
                try {
                    speak(getResources().getString(R.string.asr_notstarted), "ES", MainActivity.ID_PROMPT_INFO);
                } catch (Exception ex) {
                    Log.e(LOGTAG, "TTS not accessible");
                }

            }
        } else {

            this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    changeButtonAppearanceToDefault();
                }
            });
            try {
                speak(getResources().getString(R.string.check_internet_connection), "ES", MainActivity.ID_PROMPT_INFO);
            } catch (Exception ex) {
                Log.e(LOGTAG, "TTS not accessible");
            }
            Log.e(LOGTAG, "Device not connected to Internet");

        }
    }

    /**
     * Invoked when the ASR is ready to start listening. Provides feedback to the user to show that the app is listening:
     * * It changes the color and the message of the speech button
     */
    @Override
    public void processAsrReadyForSpeech() {
        changeButtonAppearanceToListening();
    }

    /**
     * Provides feedback to the user to show that the app is listening:
     * * It changes the color and the message of the speech button
     */
    private void changeButtonAppearanceToListening() {
        FloatingActionButton speak = findViewById(R.id.id_listener);
        speak.setEnabled(false);
        FloatingActionButton button = findViewById(R.id.id_listener); //Obtains a reference to the button
        button.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    /**
     * Provides feedback to the user to show that the app is idle:
     * * It changes the color and the message of the speech button
     */
    private void changeButtonAppearanceToDefault() {
        FloatingActionButton button = findViewById(R.id.id_listener); //Obtains a reference to the button
        button.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
    }

    /**
     * Provides feedback to the user (by means of a Toast and a synthesized message) when the ASR encounters an error
     */
    @Override
    public void processAsrError(int errorCode) {

        changeButtonAppearanceToDefault();

        //Possible bug in Android SpeechRecognizer: NO_MATCH errors even before the the ASR
        // has even tried to recognized. We have adopted the solution proposed in:
        // http://stackoverflow.com/questions/31071650/speechrecognizer-throws-onerror-on-the-first-listening
        long duration = System.currentTimeMillis() - startListeningTime;
        if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            Log.e(LOGTAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
            stopListening();
        } else {
            int errorMsg;
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = R.string.asr_error_audio;
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMsg = R.string.asr_error_client;
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = R.string.asr_error_permissions;
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = R.string.asr_error_network;
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = R.string.asr_error_networktimeout;
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = R.string.asr_error_nomatch;
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = R.string.asr_error_recognizerbusy;
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = R.string.asr_error_server;
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = R.string.asr_error_speechtimeout;
                    break;
                default:
                    errorMsg = R.string.asr_error; //Another frequent error that is not really due to the ASR, we will ignore it
                    break;
            }
            FloatingActionButton speak = findViewById(R.id.id_listener);
            speak.setEnabled(true);
            String msg = getResources().getString(errorMsg);
            this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.asr_error, Toast.LENGTH_LONG).show();
                }
            });

            Log.e(LOGTAG, "Error when attempting to listen: " + msg);
            try {
                speak(msg, "ES", MainActivity.ID_PROMPT_INFO);
            } catch (Exception e) {
                Log.e(LOGTAG, "TTS not accessible");
            }
        }
    }

    /**
     * Synthesizes the best recognition result
     */
    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {

        if(nBestList!=null){

            Log.d(LOGTAG, "ASR best result: " + nBestList.get(0));

            if(nBestList.size()>0){
                changeButtonAppearanceToDefault();
                sendMsgToChatBot(nBestList.get(0)); //Send the best recognition hypothesis to the chatbot
            }
        }
    }
    private void iniciarTTS(){
        initSpeechInputOutput(this);
    }
    /**
     * Connects to DialogFlow sending the user input in text form
     * @param userInput recognized utterance
     */
    private void sendMsgToChatBot(String userInput) {

        //final AIRequest aiRequest = new AIRequest();
        //aiRequest.setQuery(userInput);

        new AsyncTask<String,Void,AIResponse>() {

            /**
             * Connects to the DialogFlow service
             * @param strings Contains the user request
             * @return language understanding result from DialogFlow
             */
            @Override
            protected AIResponse doInBackground(String... strings) {
                final String request = strings[0];
                Log.d(LOGTAG,"Request: "+strings[0]);
                try {
                    final AIRequest aiRequest = new AIRequest(request);
                    final AIResponse response = aiDataService.request(aiRequest);
                    Log.d(LOGTAG,"Request: "+aiRequest);
                    Log.d(LOGTAG,"Response: "+response);


                    return response;
                } catch (AIServiceException e) {
                    try {
                        speak("No se puede obtener resultado de DialogFlow", "Spanish", MainActivity.ID_PROMPT_INFO);
                        Log.e(LOGTAG,"Problemas trayendo los resultados");
                    } catch (Exception ex) {
                        Log.e(LOGTAG, "El español no esta disponible");
                    }
                }
                return null;
            }

            /**
             * The semantic parsing is decomposed and the text corresponding to the chatbot
             * response is synthesized
             * @param response parsing corresponding to the output of DialogFlow
             */
            @Override
            protected void onPostExecute(AIResponse response) {
                if (response != null) {
                    // process aiResponse here
                    // Mmore info for a more detailed parsing on the response: https://github.com/dialogflow/dialogflow-android-client/blob/master/apiAISampleApp/src/main/java/ai/api/sample/AIDialogSampleActivity.java

                    final Result result = response.getResult();
                    Log.d(LOGTAG,"Result: "+result.getResolvedQuery());
                    Log.d(LOGTAG,"Action: " + result.getAction());

                    final String chatbotResponse = result.getFulfillment().getSpeech();
                    try {
                        speak(chatbotResponse, "ES", MainActivity.ID_PROMPT_QUERY); //It always starts listening after talking, it is neccessary to include a special "last_exchange" intent in dialogflow and process it here
                        //so that the last system answer is synthesized using ID_PROMPT_INFO.
                    } catch (Exception e) { Log.e(LOGTAG, "TTS not accessible"); }

                }
            }
        }.execute(userInput);

    }

    /**
     * Checks whether the device is connected to Internet (returns true) or not (returns false)
     * From: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
     */
    public boolean deviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * Shuts down the TTS engine when finished
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //shutdown();
    }

    /**
     * Invoked when the TTS has finished synthesizing.
     *
     * In this case, it starts recognizing if the message that has just been synthesized corresponds to a question (its id is ID_PROMPT_QUERY),
     * and does nothing otherwise.
     *
     * According to the documentation the speech recognizer must be invoked from the main thread. onTTSDone callback from TTS engine and thus
     * is not in the main thread. To solve the problem, we use Androids native function for forcing running code on the UI thread
     * (runOnUiThread).

     *
     * @param uttId identifier of the prompt that has just been synthesized (the id is indicated in the speak method when the text is sent
     * to the TTS engine)
     */

    @Override
    public void onTTSDone(String uttId) {
        if(uttId.equals(MainActivity.ID_PROMPT_QUERY.toString())) {
            runOnUiThread(new Runnable() {
                public void run() {
                    startListening();
                }
            });
        }
    }

    /**
     * Invoked when the TTS encounters an error.
     *
     * In this case it just writes in the log.
     */
    @Override
    public void onTTSError(String uttId) {
        Log.e(LOGTAG, "TTS error");
    }

    /**
     * Invoked when the TTS starts synthesizing
     *
     * In this case it just writes in the log.
     */
    @Override
    public void onTTSStart(String uttId) {
        Log.d(LOGTAG, "TTS starts speaking");
    }

    @Override
    public void onResume(){
        super.onResume();
        iniciarTTS();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }
}
