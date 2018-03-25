package com.example.nimy.nimy;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TomarFoto extends AppCompatActivity implements View.OnClickListener {

    private static String APP_DIRECTORY = "NimyPicture/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "ImagenNimy";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    private ImageView imagen;
    private Button btnCrearPerfil;
    private Button btnElegirImagen;
    private RelativeLayout relativeLayout;

    private String mPath;
    private Animal animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomar_foto);
        Parse.initialize(this);
        Intent intent = getIntent();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jBJh6jKCJipl2TdDTcTYt51Ftyg9jOBhfcfhubjj")
                .clientKey("chJvmPMPKY0QDIuklkvqmAfrWgxRkBNUiy8dEmoe")
                .server("https://parseapi.back4app.com")
                .enableLocalDataStore()
                .build() );
        animal = (Animal) intent.getSerializableExtra("mascota");
        imagen = (ImageView) findViewById(R.id.imagen_foto);
        btnCrearPerfil = (Button) findViewById(R.id.btnCrearPefil);
        btnCrearPerfil.setOnClickListener(this);
        btnCrearPerfil.setEnabled(false);
        btnElegirImagen = (Button) findViewById(R.id.btnElegirImagen);
        btnElegirImagen.setOnClickListener(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_foto);
        if(permisos_aplicacion()){
            btnElegirImagen.setEnabled(true);
            btnCrearPerfil.setEnabled(true);
        }else{
            btnElegirImagen.setEnabled(false);
            btnCrearPerfil.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btnCrearPerfil){
                this.animal.generarQR();
                Bitmap bmp = this.animal.getCodigoQR();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile codigoQR = new ParseFile("codigoQR.jpg", byteArray);
                codigoQR.saveInBackground();

                Bitmap bitmap = this.animal.getFoto();
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                byte[] bytes = stream1.toByteArray();
                ParseFile foto = new ParseFile("foto.jpg", bytes);
                foto.saveInBackground();

                ParseObject mascota = new ParseObject("mascota");
                mascota.put("nombre", this.animal.getNombre());
                mascota.put("fecha_nacimiento", this.animal.getFecha_nacimiento());
                mascota.put("tipo", this.animal.getTipo());
                mascota.put("raza", this.animal.getRaza());
                mascota.put("color", this.animal.getColor());
                mascota.put("sexo", this.animal.getSexo());
                mascota.put("dueno_mascota", ParseUser.getCurrentUser());
                //mascota.put("direccion", this.animal.getDireccion());
                mascota.put("codigoQR", codigoQR);
                mascota.put("foto", foto);
                mascota.pinInBackground();
                mascota.saveInBackground();

                Intent intent = new Intent(TomarFoto.this, MainActivity.class);
                startActivity(intent);
        }
        if(v==btnElegirImagen){
            mostrarOpciones();
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

    private void mostrarOpciones() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(TomarFoto.this);
        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    abrirCamara();
                }else if(option[which] == "Elegir de galeria"){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona imagen para la app"), SELECT_PICTURE);
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void abrirCamara() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();
        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();
        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".tiff";
            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;
            File newFile = new File(mPath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    bitmap = getResizedBitmap(bitmap, 400);
                    imagen.setImageBitmap(bitmap);
                    this.animal.setFoto(bitmap);
                    btnCrearPerfil.setEnabled(true);
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    try {
                        Bitmap bitmap1=MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
                        bitmap1 = getResizedBitmap(bitmap1, 400);
                        this.animal.setFoto(bitmap1);
                    } catch (IOException e) {
                        Toast.makeText(TomarFoto.this,e.getMessage().toString(), Toast.LENGTH_SHORT)
                                .show();
                    }
                    imagen.setImageURI(path);
                    btnCrearPerfil.setEnabled(true);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(TomarFoto.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                btnCrearPerfil.setEnabled(true);
                btnElegirImagen.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TomarFoto.this);
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

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void consulta(){

    }
}
