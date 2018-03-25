package com.example.nimy.nimy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Animal;
import com.example.nimy.nimy.Clases.Evento;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

public class AgendaMascota extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegistrarActivy";
    private TextView nombre_mascota;
    private EditText descripcion;
    private EditText fecha;
    private RadioButton vacuna;
    private RadioButton Baño;
    private RadioButton Otro;
    private Button btnAgregarEvento;
    private DatePickerDialog.OnDateSetListener oyentefecha;
    private Animal animal;
    private int dia, mes, anio;
    private Typeface lovely;
    private String ruta_fuente;
    private Evento evento;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.i_nimy);
        setContentView(R.layout.activity_agenda_mascota);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jBJh6jKCJipl2TdDTcTYt51Ftyg9jOBhfcfhubjj")
                .clientKey("chJvmPMPKY0QDIuklkvqmAfrWgxRkBNUiy8dEmoe")
                .server("https://parseapi.back4app.com")
                .enableLocalDataStore()
                .build() );
        animal=new Animal();
        Intent intent=getIntent();
        ruta_fuente="fonts/lovely.ttf";
        lovely=Typeface.createFromAsset(getAssets(), ruta_fuente);
        animal.setNombre(intent.getStringExtra("mascota"));
        evento=new Evento();
        nombre_mascota=(TextView)findViewById(R.id.txtnombremascota_actual);
        nombre_mascota.setText(animal.getNombre());
        nombre_mascota.setTypeface(lovely);
        descripcion=(EditText) findViewById(R.id.txtevento);
        fecha = (EditText) findViewById ( R.id.fecha_evento );
        fecha.setOnClickListener(this);
        btnAgregarEvento=(Button) findViewById ( R.id.btnAgregarEvento );
        btnAgregarEvento.setOnClickListener(this);
        vacuna=(RadioButton)findViewById( R.id.rbVacuna );
        Baño = (RadioButton) findViewById ( R.id.rbBaño );
        Otro = (RadioButton) findViewById ( R.id.rbOtro );
        oyentefecha = new DatePickerDialog.OnDateSetListener () {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "OnDataSet: date " + year + "/" + month + "/" + dayOfMonth);
                String fechatexto = dayOfMonth + "/" + month + "/" + year;
                fecha.setText(fechatexto);
            }
        };
    }

    public void consulta(){
        ParseQuery<ParseObject> mascotaQuery = ParseQuery.getQuery("mascota");
        mascotaQuery.whereEqualTo("dueno_mascota", ParseUser.getCurrentUser());
        mascotaQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("mascotas", "Retrieved " + objects.size() + " mascotas");
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).get("nombre").equals(animal.getNombre())){
                            ParseObject eventoinsertar=new ParseObject ( "agenda" );
                            eventoinsertar.put("tipo",evento.getTipo ().toString ());
                            eventoinsertar.put("descripcion", evento.getDescripcion ().toString ());
                            eventoinsertar.put("mascota", objects.get(i));
                            eventoinsertar.put("fecha", evento.getFecha());
                            eventoinsertar.pinInBackground ();
                            eventoinsertar.saveEventually();
                        }
                    }
                }else{
                    Log.d("mascota", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void onClick(View view) {
        if (view == fecha) {
            final Calendar c = Calendar.getInstance ();
            dia = c.get ( Calendar.DAY_OF_MONTH );
            mes = c.get ( Calendar.MONTH );
            anio = c.get ( Calendar.YEAR );
            DatePickerDialog datePickerDialog = new DatePickerDialog (
                    AgendaMascota.this, R.style.Theme_AppCompat, oyentefecha, anio, mes, dia );
            datePickerDialog.show ();
        }
        if (view == btnAgregarEvento) {
            if(!vacuna.isChecked() && !Baño.isChecked() && !Otro.isChecked()){
                Toast.makeText(AgendaMascota.this,"Por Favor Seleccione el tipo de Evento",Toast.LENGTH_SHORT)
                        .show();
            }else if(descripcion.getText().toString().isEmpty()){
                Toast.makeText(AgendaMascota.this,"Por Favor Ingrese la descripcion para el Evento",Toast.LENGTH_SHORT)
                        .show();
            }else if(fecha.getText().toString().isEmpty()){
                Toast.makeText(AgendaMascota.this,"Por Favor Ingrese Fecha para el Evento", Toast.LENGTH_SHORT)
                        .show();
            }
            if(vacuna.isChecked() || Baño.isChecked() || Otro.isChecked()
                    && !descripcion.getText().toString().isEmpty() && !fecha.getText().toString().isEmpty()){
                evento.setDescripcion(descripcion.getText().toString());
                evento.setFecha(fecha.getText().toString());
                if(vacuna.isChecked ()){
                    evento.setTipo (vacuna.getText().toString ());
                }if (Baño.isChecked ()) {
                    evento.setTipo(Baño.getText().toString());
                }if (Otro.isChecked ()){
                    evento.setTipo ( Otro.getText ().toString () );
                }
                consulta();
                Toast.makeText(AgendaMascota.this,"Evento Guardado", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AgendaMascota.this, MainActivity.class);
                intent.putExtra("nombre_mascota", animal.getNombre());
                startActivity(intent);
            }
        }
    }
}
