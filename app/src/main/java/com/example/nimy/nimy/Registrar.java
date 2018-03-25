package com.example.nimy.nimy;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.example.nimy.nimy.Clases.Localizacion;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Registrar extends AppCompatActivity implements View.OnClickListener {

    private int año, mes, dia;
    private static final String TAG = "RegistrarActivy";
    Animal animal;

    /*Declaracion de Variables para hacer el casteo del XML*/
    private EditText fecha;
    private EditText nombre;
    private Spinner color;
    private Spinner tipo;
    private Spinner raza;
    private RadioButton macho;
    private RadioButton hembra;
    private Button btnSiguiente;
    private LocationManager locationManager;
    private double longitud;
    private double latitud;
    private final int MY_PERMISSIONS = 100;
    private LinearLayout linearLayout;
    private LocationManager mLocationManager;
    /*Finaliza Declaracion de Variables para hacer el casteo del XML*/

    /*Declaracion de Variables para la fecha*/
    private DatePickerDialog.OnDateSetListener oyentefecha;
    private ActionBar actionBar;
     /*Finaliza Declaracion de Variables para la fecha*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        animal = new Animal();
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.i_nimy);
        /*Casteo XML*/
        nombre = (EditText) findViewById(R.id.txtnombre);
        color = (Spinner) findViewById(R.id.txtcolor_mascota);
        tipo = (Spinner) findViewById(R.id.SpTipo);
        raza = (Spinner) findViewById(R.id.SpRaza);
        hembra = (RadioButton) findViewById(R.id.rdHembra);
        macho = (RadioButton) findViewById(R.id.rdMacho);
        fecha = (EditText) findViewById(R.id.fecha_nacimiento);
        fecha.setOnClickListener(this);
        btnSiguiente = (Button) findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        /*Fin Casteo XML*/
        oyentefecha = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "OnDataSet: date " + year + "/" + month + "/" + dayOfMonth);
                String fechatexto = dayOfMonth + "/" + month + "/" + year;
                fecha.setText(fechatexto);
            }
        };
        if (permisos_aplicacion()) {
            btnSiguiente.setEnabled(true);
        } else {
            btnSiguiente.setEnabled(false);
        }
    }

    private boolean permisos_aplicacion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if ((checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        if ((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) || (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION))) {
            Snackbar.make(linearLayout, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, MY_PERMISSIONS);
                }
            });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, MY_PERMISSIONS);
        }
        return false;
    }

    private String obtenerDireccion(double latitud, double longitud) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Mi dirección de ubicación actual", strReturnedAddress.toString());
            } else {
                Log.w("Mi dirección de ubicación actual", "Sin dirección devuelta!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Mi dirección de ubicación actual", "No se puede obtener la dirección!");
        }
        return strAdd;
    }

    @Override
    public void onClick(View v) {
        if (v == fecha) {
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            año = c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(Registrar.this, R.style.Theme_AppCompat, oyentefecha, año, mes, dia);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
        if (v == btnSiguiente) {
            if (nombre.getText().toString().isEmpty()) {
                Toast.makeText(Registrar.this, "Por Favor Ingrese Nombre de Mascota", Toast.LENGTH_SHORT)
                        .show();
            } else if (fecha.getText().toString().isEmpty()) {
                Toast.makeText(Registrar.this, "Por Favor Ingrese Fecha de Nacimiento", Toast.LENGTH_SHORT)
                        .show();
            } else if (color.getSelectedItem().toString().isEmpty()) {
                Toast.makeText(Registrar.this, "Por Favor Seleccione un Color", Toast.LENGTH_SHORT)
                        .show();
            } else if (raza.getSelectedItem().toString().isEmpty()) {
                Toast.makeText(Registrar.this, "Por Favor Seleccione una Raza", Toast.LENGTH_SHORT)
                        .show();
            } else if (tipo.getSelectedItem().toString().isEmpty()) {
                Toast.makeText(Registrar.this, "Por Favor Seleccione un Tipo", Toast.LENGTH_SHORT)
                        .show();
            } else if (!macho.isChecked() && !hembra.isChecked()) {
                Toast.makeText(Registrar.this, "Por Favor Seleccione un Sexo", Toast.LENGTH_SHORT)
                        .show();
            }

            if (!nombre.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty() &&
                    !color.getSelectedItem().toString().isEmpty() && !raza.getSelectedItem().toString().isEmpty()
                    && !tipo.getSelectedItem().toString().isEmpty() && macho.isChecked() || hembra.isChecked()
                    ) {
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
                Location location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
                this.latitud = location.getLatitude();
                this.longitud = location.getLongitude();
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        this.animal.setEmail_dueño(currentUser.getEmail());
                        this.animal.setDueño(currentUser.getString("nombre_usuario"));
                        this.animal.setNumero_tel(currentUser.getString("telephone"));
                        this.animal.setNombre(nombre.getText().toString());
                        this.animal.setFecha_nacimiento(fecha.getText().toString());
                        this.animal.setTipo(tipo.getSelectedItem().toString());
                        this.animal.setRaza(raza.getSelectedItem().toString());
                        this.animal.setColor(color.getSelectedItem().toString());
                        this.animal.setDireccion(obtenerDireccion(latitud,longitud));
                        if (hembra.isChecked()) {
                            animal.setSexo(hembra.getText().toString());
                        } else if (macho.isChecked()) {
                            animal.setSexo(macho.getText().toString());
                        }
                Intent intent = new Intent(Registrar.this, TomarFoto.class);
                intent.putExtra("mascota", animal);
                startActivity(intent);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Registrar.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                btnSiguiente.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Registrar.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }


 }








