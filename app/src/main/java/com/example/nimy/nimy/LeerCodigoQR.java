package com.example.nimy.nimy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Ubicacion;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

public class LeerCodigoQR extends AppCompatActivity implements View.OnClickListener {

    private TextView correo_dueño;
    private TextView nombre_dueño;
    private TextView telefono;
    private TextView direccion;
    private TextView nombre_mascota;
    private TextView color_mascota;
    private Button btnActualizar;
    private String[] datos;
    private Ubicacion ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_codigo_qr);
        String informacion =  getIntent().getExtras().getString("busqueda");
        datos = informacion.split(":");
        Parse.initialize(this);
        btnActualizar=(Button)findViewById(R.id.btnActualizarUbicacion);
        btnActualizar.setOnClickListener(this);
        correo_dueño=(TextView)findViewById(R.id.txtcorreo_electronico_encontrado);
        correo_dueño.setText(datos[0]);
        nombre_dueño=(TextView)findViewById(R.id.txtnombredueño_encontrado);
        nombre_dueño.setText(datos[1]);
        telefono=(TextView)findViewById(R.id.txtnumerotelefonico_encontrado);
        telefono.setText(datos[2]);
        nombre_mascota=(TextView)findViewById(R.id.txtnombremascota_encontrado);
        nombre_mascota.setText(datos[3]);
        color_mascota=(TextView)findViewById(R.id.txtcolorcaracteristico_encontrado);
        color_mascota.setText(datos[4]);
        direccion=(TextView)findViewById(R.id.txtdireccion_encontrado);
        direccion.setText(datos[5]);
        ubicacion=new Ubicacion();
    }

    @Override
    public void onClick(View v) {
        if(v==btnActualizar){
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            final Calendar c = Calendar.getInstance();
                int dia = c.get(Calendar.DAY_OF_MONTH);
                int mes = c.get(Calendar.MONTH);
                int año = c.get(Calendar.YEAR);
                String fecha=mes+"/"+dia+"/"+año;
                ubicacion.setLatitud(location.getLatitude());
                ubicacion.setLongitud(location.getLongitude());
                ubicacion.setFecha_lectura(fecha);
                try {
                    generarPunto();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    public void generarPunto() throws ParseException {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", correo_dueño.getText().toString());
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null){
                    ParseQuery<ParseObject> mascotaQuery = ParseQuery.getQuery("mascota");
                    mascotaQuery.whereEqualTo("dueno_mascota", objects.get(0));
                    mascotaQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> mascotas, ParseException e) {
                            if (e == null) {
                                Log.d("mascotas", "Retrieved " + mascotas.size() + " mascotas");
                                for (int i = 0; i < mascotas.size(); i++) {
                                    if (mascotas.get(i).get("nombre").equals(nombre_mascota.getText().toString())){
                                        ParseObject puntoInsertar=new ParseObject ( "ubicaciones" );
                                        ParseGeoPoint parseGeoPoint=new ParseGeoPoint(ubicacion.getLatitud(), ubicacion.getLongitud());
                                        puntoInsertar.put("ubicacion_punto", parseGeoPoint);
                                        puntoInsertar.put("fecha_lectura", ubicacion.getFecha_lectura());
                                        puntoInsertar.put("mascota", mascotas.get(i));
                                        puntoInsertar.pinInBackground ();
                                        puntoInsertar.saveEventually();
                                        Intent intent = new Intent(LeerCodigoQR.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }else{
                                Log.d("mascota", "Error: " + e.getMessage());
                            }
                        }
                    });
                }else{}
            }
        });
    }
}
