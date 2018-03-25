package com.example.nimy.nimy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SeleccionarMascota extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Animal> animals;
    private GridLayout linearLayout;
    private Typeface lovely;
    private String ruta_fuente;
    private CircleImageView agregarOtro;
    private ArrayList<CircleImageView> circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_mascota);
        linearLayout=(GridLayout)findViewById(R.id.layout_seleccionar);
        linearLayout.setColumnCount(3);
        animals=new ArrayList<Animal>();
        ruta_fuente="fonts/lovely.ttf";
        lovely=Typeface.createFromAsset(getAssets(), ruta_fuente);
        consulta();
        circleImageView=new ArrayList<CircleImageView>();
    }

    public void interfaz(ArrayList<Animal> animals){
        LinearLayout layout=new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(200 ,200);
        agregarOtro=new CircleImageView(getApplicationContext());
        agregarOtro.setBorderOverlay(true);
        agregarOtro.setBorderColor(Color.parseColor("#C0DA4E"));
        agregarOtro.setImageDrawable(getResources().getDrawable(R.drawable.i_agregar));
        agregarOtro.setOnClickListener(this);
        agregarOtro.setLayoutParams(parms);
        agregarOtro.setPadding(10,10,10,10);
        layout.addView(agregarOtro);
        TextView agregarOtrotexto=new TextView(getApplicationContext());
        agregarOtrotexto.setGravity(Gravity.CENTER_HORIZONTAL);
        agregarOtrotexto.setText("Agregar");
        agregarOtrotexto.setTextSize(30);
        agregarOtrotexto.setTextColor(Color.parseColor("#3B0B2E"));
        agregarOtrotexto.setTypeface(lovely);
        layout.addView(agregarOtrotexto);
        linearLayout.addView(layout);
        if(animals.size()!=0) {
            for (int i = 0; i < animals.size(); i++) {
                LinearLayout layout1 = new LinearLayout(getApplicationContext());
                layout1.setOrientation(LinearLayout.VERTICAL);
                CircleImageView view = new CircleImageView(getApplicationContext());
                view.setBorderOverlay(true);
                view.setBorderColor(Color.parseColor("#C0DA4E"));
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);
                view.setLayoutParams(parms);
                view.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                view.setPadding(10, 10, 10, 10);
                view.setId(i);
                view.setImageBitmap(animals.get(i).getFoto());
                circleImageView.add(view);
                layout1.addView(view);
                TextView textView = new TextView(getApplicationContext());
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText(animals.get(i).getNombre());
                textView.setTextSize(30);
                textView.setTextColor(Color.parseColor("#3B0B2E"));
                textView.setTypeface(lovely);
                layout1.addView(textView);
                linearLayout.addView(layout1);
            }
        }
    }

    public void consulta(){
        ParseQuery<ParseObject> mascotaQuery = ParseQuery.getQuery("mascota");
        mascotaQuery.whereEqualTo("dueno_mascota", ParseUser.getCurrentUser());
        mascotaQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> mascotas, ParseException e) {
                if (e == null) {
                    Log.d("mascotas", "Retrieved " + mascotas.size() + " mascotas");
                    for(int i=0;i<mascotas.size();i++){
                        Animal animal=new Animal();
                        animal.setNombre((String)mascotas.get(i).get("nombre"));
                        ParseFile foto=(ParseFile)mascotas.get(i).getParseFile("foto");
                        try {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(foto.getData(), 0, foto.getData().length);
                            animal.setFoto(bitmap);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        animals.add(animal);
                    }
                    interfaz(animals);
                }else {
                    Log.d("mascota", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v==agregarOtro){
            Intent intent = new Intent(SeleccionarMascota.this, Registrar.class);
            startActivity(intent);
        }for(int i=0;i<circleImageView.size();i++){
            if(v==circleImageView.get(i)){
                Intent intent=new Intent(SeleccionarMascota.this, MainActivity.class);
                intent.putExtra("nombre_mascota", animals.get(i).getNombre());
                startActivity(intent);
                }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
