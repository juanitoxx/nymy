package com.example.nimy.nimy.Clases;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;
import java.util.Locale;

public class Ubicacion {

    private String fecha_lectura;
    private double longitud;
    private double latitud;
    private String direccion="";

    public String getFecha_lectura() { return fecha_lectura; }

    public void setFecha_lectura(String fecha_lectura) { this.fecha_lectura = fecha_lectura; }

    public double getLongitud() { return longitud; }

    public void setLongitud(double longitud) { this.longitud = longitud; }

    public double getLatitud() { return latitud; }

    public void setLatitud(double latitud) { this.latitud = latitud; }

    public String getDireccion() { return direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }
}
