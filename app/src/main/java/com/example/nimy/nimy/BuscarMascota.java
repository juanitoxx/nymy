package com.example.nimy.nimy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.example.nimy.nimy.Clases.Ubicacion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuscarMascota extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Animal animal;
    private ArrayList<Ubicacion> ubicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_mascota);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ubicaciones=new ArrayList<Ubicacion>();
        Intent intent=getIntent();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double latitud = location.getLatitude();
        double longitud = location.getLongitude();
            LatLng actual = new LatLng(latitud, longitud);
            googleMap.addMarker(new MarkerOptions().position(actual)
                    .title("Usted se encuentra Aqui"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(actual));
            for(int i=0;i<ubicaciones.size();i++){
                LatLng nuevas = new LatLng(ubicaciones.get(i).getLatitud(), ubicaciones.get(i).getLongitud());
                googleMap.addMarker(new MarkerOptions().position(nuevas)
                        .title("Para la fecha "+ubicaciones.get(i).getFecha_lectura()+" su mascota se encontraba Aqui"));
            }
        }
}
