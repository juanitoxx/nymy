package com.example.nimy.nimy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.example.nimy.nimy.Clases.Evento;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeleccionarEvento extends AppCompatActivity implements View.OnClickListener {

    CompactCalendarView calendario;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private ActionBar actionBar;
    private ArrayList<Evento> eventos;
    private Animal animal;
    private Button btnAgregarEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_evento);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.i_nimy);
        Intent intent=getIntent();
        animal=new Animal();
        btnAgregarEvento=(Button)findViewById(R.id.btnAgregarEvento);
        btnAgregarEvento.setOnClickListener(this);
        animal.setNombre(intent.getStringExtra("mascota"));
        calendario = (CompactCalendarView) findViewById(R.id.calendario);
        calendario.setUseThreeLetterAbbreviation(true);
        calendario.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });
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
                        if (objects.get(i).get("nombre").equals(animal.getNombre())){
                            ParseQuery<ParseObject> eventoQuery = ParseQuery.getQuery("evento");
                            eventoQuery.whereEqualTo("mascota", objects.get(i));
                            eventoQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    agregarEventos(objects);
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

    public void agregarEventos (List<ParseObject> objects){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for(int i=0;i<objects.size();i++){
            Date date = null;
            try {
                date = sdf.parse(objects.get(i).getString("fecha"));
                long timeInMillis = date.getTime();
                Event event=new Event(Color.RED, timeInMillis, objects.get(i).getString("tipo"));
                calendario.addEvent(event);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v==btnAgregarEvento){
            Intent intent = new Intent(SeleccionarEvento.this, AgendaMascota.class);
            intent.putExtra("mascota", animal.getNombre());
            startActivity(intent);
        }
    }
}
