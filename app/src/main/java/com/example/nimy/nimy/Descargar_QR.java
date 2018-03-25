package com.example.nimy.nimy;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Descargar_QR extends AppCompatActivity implements View.OnClickListener {

    private Animal animal;
    private ImageView codigoQR;
    private TextView nombre_dueño;
    private TextView telefono;
    private TextView direccion;
    private TextView nombre_mascota;
    private TextView color_mascota;
    private FloatingActionButton btn_descargar;
    private String mPath;
    private String nombreMascota;
    private Typeface lovely;
    private String ruta_fuente;

    private static String APP_DIRECTORY = "NimyPicture/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "CodigoQR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargar__qr);
        Parse.initialize(this);
        Intent intent = getIntent();
        animal=new Animal();
        nombreMascota=intent.getStringExtra("mascota");
        ruta_fuente="fonts/lovely.ttf";
        lovely=Typeface.createFromAsset(getAssets(), ruta_fuente);
        btn_descargar=(FloatingActionButton)findViewById(R.id.btncodigoQR);
        btn_descargar.setOnClickListener(this);
        nombre_dueño=(TextView)findViewById(R.id.txtnombredueño);
        telefono=(TextView)findViewById(R.id.txtnumerotelefonico);
        direccion=(TextView)findViewById(R.id.txtdireccion);
        color_mascota=(TextView)findViewById(R.id.txtcolorcaracteristico);
        nombre_mascota=(TextView)findViewById(R.id.txtnombremascota);
        codigoQR=(ImageView) findViewById(R.id.imagenQR);
        consulta();
    }

    public void consulta() {
        ParseQuery<ParseObject> mascotaQuery = ParseQuery.getQuery("mascota");
        mascotaQuery.whereEqualTo("dueno_mascota", ParseUser.getCurrentUser());
        mascotaQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("mascotas", "Retrieved " + objects.size() + " mascotas");
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).get("nombre").equals(nombreMascota)){
                            animal.setDueño(ParseUser.getCurrentUser().getString("nombre_usuario"));
                            animal.setNumero_tel(ParseUser.getCurrentUser().getCurrentUser().getString("telephone"));
                            animal.setDireccion(objects.get(i).getString("direccion"));
                            animal.setColor(objects.get(i).getString("color"));
                            animal.setNombre(objects.get(i).getString("nombre"));
                            ParseFile codigoQR = (ParseFile) objects.get(i).getParseFile("codigoQR");
                            try {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(codigoQR.getData(), 0, codigoQR.getData().length);
                                animal.setCodigoQR(bitmap);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            pintarInterfaz();
                        }
                    }

                }else{
                    Log.d("mascota", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void pintarInterfaz(){
        nombre_dueño.setText(this.animal.getDueño());
        nombre_dueño.setTypeface(lovely);
        telefono.setText(this.animal.getNumero_tel());
        telefono.setTypeface(lovely);
        direccion.setText(this.animal.getDireccion());
        direccion.setTypeface(lovely);
        color_mascota.setText(this.animal.getColor());
        color_mascota.setTypeface(lovely);
        nombre_mascota.setText(this.animal.getNombre());
        nombre_mascota.setTypeface(lovely);
        this.codigoQR.setImageBitmap(this.animal.getCodigoQR());
    }

    @Override
    public void onClick(View v) {
        if(v==btn_descargar){
            descargarQR();
            Intent intent=new Intent(Descargar_QR.this, MainActivity.class);
            intent.putExtra("nombre_mascota", nombreMascota);
            startActivity(intent);
        }
    }

    private void descargarQR(){
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();
        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();
        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";
            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;
            File newFile = new File(mPath);
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                animal.getCodigoQR().compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
                MakeSureFileWasCreatedThenMakeAvabile(file);
                AbleToSave();
            }
            catch(FileNotFoundException e) {
                UnableToSave();
                Toast.makeText(Descargar_QR.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
            catch(IOException e) {
                UnableToSave();
                Toast.makeText(Descargar_QR.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void MakeSureFileWasCreatedThenMakeAvabile(File file){
        MediaScannerConnection.scanFile(Descargar_QR.this,
                new String[] { file.toString() } , null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    private void UnableToSave() {
        Toast.makeText(Descargar_QR.this, "¡No se ha podido guardar la imagen!", Toast.LENGTH_SHORT).show();
    }

    private void AbleToSave() {
        Toast.makeText(Descargar_QR.this, "Imagen guardada en la galería.", Toast.LENGTH_SHORT).show();
    }
}
