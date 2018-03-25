package com.example.nimy.nimy;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.example.nimy.nimy.Clases.Ubicacion;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton boton_informacion;
    private FloatingActionButton boton_mascota;
    private FloatingActionButton boton_leerQR;
    private CircleMenu circleMenu;
    private TextView nombre_perfil;
    private TextView edad_perfil;
    private TextView direccion_perfil;
    private TextView color_perfil;
    private int cantidad_mascotas;
    private CircleImageView imagen_perfil;
    private ImageView imagen_no;
    private Animal animal;
    private Typeface lovely;
    private String ruta_fuente;
    private String string;

    private final int MY_PERMISSIONS = 100;
    private LinearLayout relativeLayout;
    private ArrayList<Ubicacion> ubicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.initialize(this);
        ruta_fuente="fonts/lovely.ttf";
        lovely=Typeface.createFromAsset(getAssets(), ruta_fuente);
        animal=new Animal();
        ubicaciones=new ArrayList<Ubicacion>();
        Intent intent = getIntent();
        string="";
        if(intent.getStringExtra("nombre_mascota")!=null){
            string = intent.getStringExtra("nombre_mascota");
        }
        imagen_perfil=(CircleImageView)findViewById(R.id.imagen_perfil);
        imagen_no=(ImageView)findViewById(R.id.imagen_no_hay_mascotas);
        nombre_perfil=(TextView)findViewById(R.id.txtnombre_perfil);
        edad_perfil=(TextView)findViewById(R.id.txtedad_perfil);
        direccion_perfil=(TextView)findViewById(R.id.txtdireccion_perfil);
        color_perfil=(TextView)findViewById(R.id.txtcolor_perfil);
        boton_informacion = (FloatingActionButton) findViewById(R.id.btninformacion);
        boton_informacion.setOnClickListener(this);
        boton_mascota = (FloatingActionButton) findViewById(R.id.btnmascota);
        boton_mascota.setOnClickListener(this);
        boton_leerQR = (FloatingActionButton)findViewById(R.id.btncodigoQR);
        boton_leerQR.setOnClickListener(this);
        circleMenu = (CircleMenu)findViewById(R.id.menu_principal);
        relativeLayout=(LinearLayout)findViewById(R.id.layoutGeneral);
        circleMenu.setMainMenu(Color.parseColor("#C0DA4E"),R.drawable.i_detalles,R.drawable.i_android);
        consulta();
        consultaUbicaciones();
        if(permisos_aplicacion()){
            boton_leerQR.setEnabled(true);
        }else{
            boton_leerQR.setEnabled(false);
        }
    }

    public void consulta(){
        ParseQuery<ParseObject> mascotaQuery = ParseQuery.getQuery("mascota");
        mascotaQuery.whereEqualTo("dueno_mascota", ParseUser.getCurrentUser());
        mascotaQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listamascotas, ParseException e) {
                if (e == null) {
                    Log.d("mascotas", "Retrieved " + listamascotas.size() + " mascotas");
                    cantidad_mascotas=listamascotas.size();
                    if(cantidad_mascotas==0){
                        imagen_no.setImageDrawable(getResources().getDrawable(R.drawable.i_no_mascotas));
                    }else if(cantidad_mascotas!=0){
                        iniciarMenu();
                        if(!string.equals("")){
                            for(int i=0;i<listamascotas.size();i++){
                                if(listamascotas.get(i).get("nombre").equals(string)){
                                    animal.setNombre((String)listamascotas.get(i).get("nombre"));
                                    animal.setFecha_nacimiento((String) listamascotas.get(i).get("fecha_nacimiento"));
                                    animal.setColor((String) listamascotas.get(i).get("color"));
                                    animal.setDireccion((String) listamascotas.get(i).get("direccion"));
                                    ParseFile foto=(ParseFile)listamascotas.get(i).getParseFile("foto");
                                    try {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(foto.getData(), 0, foto.getData().length);
                                        animal.setFoto(bitmap);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    perfilDefecto(animal);
                                }
                            }
                        }else if(string.equals("")){
                            animal.setNombre((String)listamascotas.get(0).get("nombre"));
                            animal.setFecha_nacimiento((String) listamascotas.get(0).get("fecha_nacimiento"));
                            animal.setColor((String) listamascotas.get(0).get("color"));
                            animal.setDireccion((String) listamascotas.get(0).get("direccion"));
                            ParseFile foto=(ParseFile)listamascotas.get(0).getParseFile("foto");
                            try {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(foto.getData(), 0, foto.getData().length);
                                animal.setFoto(bitmap);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            perfilDefecto(animal);
                        }
                    }
                } else {
                    Log.d("mascota", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void perfilDefecto(Animal animal){
        Bitmap originalBitmap =animal.getFoto();
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());
        imagen_perfil.setImageDrawable(roundedDrawable);
        animal.calcularEdad();
        nombre_perfil.setText("Nombre: "+animal.getNombre());
        nombre_perfil.setTypeface(lovely);
        edad_perfil.setText("Edad: "+String.valueOf(animal.getEdad()));
        edad_perfil.setTypeface(lovely);
        color_perfil.setText("Color: "+animal.getColor());
        color_perfil.setTypeface(lovely);
        direccion_perfil.setText("Direccion: "+animal.getDireccion());
        direccion_perfil.setTypeface(lovely);
    }

    public void iniciarMenu(){
         /*Inicia Menu Principal*/
        circleMenu.addSubMenu(Color.parseColor("#C0DA4E"),R.drawable.i_buscar);
        circleMenu.addSubMenu(Color.parseColor("#C0DA4E"), R.drawable.i_agenda);
        circleMenu.addSubMenu(Color.parseColor("#C0DA4E"),R.drawable.i_descargar);
        circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        if(i==0){
                            Intent intent = new Intent(MainActivity.this, BuscarMascota.class);
                            intent.putExtra("ubicaciones", ubicaciones);
                            startActivity(intent);
                        }if(i==1){
                            Intent intent = new Intent(MainActivity.this, SeleccionarEvento.class);
                            intent.putExtra("mascota", animal.getNombre());
                            startActivity(intent);
                        }if(i==2){
                            Intent intent = new Intent(MainActivity.this, Descargar_QR.class);
                            intent.putExtra("mascota", animal.getNombre());
                            startActivity(intent);
                        }
                    }
                });/*Finaliza Menu Principal*/
    }

    @Override
    public void onClick(View v) {
        if(v==boton_informacion){
            Intent intent = new Intent(MainActivity.this, pantalla_informacion.class);
            startActivity(intent);
        }if(v==boton_mascota){
            Intent intent = new Intent(MainActivity.this, SeleccionarMascota.class);
            startActivity(intent);
        }if(v==boton_leerQR){
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Escanear Codigo QR");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.initiateScan();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "Usted ha cancelado la lectura", Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent(MainActivity.this, LeerCodigoQR.class);
                intent.putExtra("busqueda", result.getContents());
                startActivity(intent);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private boolean permisos_aplicacion() {
        if(Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(relativeLayout, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                boton_leerQR.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    public void consultaUbicaciones(){
        ParseQuery<ParseObject> mascotaQuery = ParseQuery.getQuery("mascota");
        mascotaQuery.whereEqualTo("dueno_mascota", ParseUser.getCurrentUser());
        mascotaQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("mascotas", "Retrieved " + objects.size() + " mascotas");
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).get("nombre").equals(animal.getNombre())){
                            ParseQuery<ParseObject> ubicacionesQuery = ParseQuery.getQuery("ubicaciones");
                            ubicacionesQuery.whereEqualTo("mascota", objects.get(i));
                            ubicacionesQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    for(int i=0;i<objects.size();i++){
                                        Ubicacion ubicacion=new Ubicacion();
                                        ubicacion.setFecha_lectura(objects.get(i).getString("fecha_lectura"));
                                        ParseGeoPoint parseGeoPoint=objects.get(i).getParseGeoPoint("ubicacion_punto");
                                        ubicacion.setLongitud(parseGeoPoint.getLongitude());
                                        ubicacion.setLatitud(parseGeoPoint.getLatitude());
                                        ubicaciones.add(ubicacion);
                                    }
                                }
                            });
                        }
                    }

                }else{
                    Log.d("mascota", "Error: " + e.getMessage());
                }
            }
        });
    }

    public Animal getAnimal() { return animal; }

    public void setAnimal(Animal animal) { this.animal = animal; }
}
