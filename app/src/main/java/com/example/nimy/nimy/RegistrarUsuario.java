package com.example.nimy.nimy;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Usuario;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrarUsuario extends AppCompatActivity implements View.OnClickListener {

    private static final String PATTERN_EMAIL = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";

    private Button btnRegistrar;
    private EditText txtnombre;
    private EditText txttelefono;
    private EditText txtcorreo;
    private EditText txtrecorreo;
    private EditText txtcontraseña;
    private EditText txtrecontraseña;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jBJh6jKCJipl2TdDTcTYt51Ftyg9jOBhfcfhubjj")
                .clientKey("chJvmPMPKY0QDIuklkvqmAfrWgxRkBNUiy8dEmoe")
                .server("https://parseapi.back4app.com")
                .enableLocalDataStore()
                .build() );
        txtnombre=(EditText)findViewById(R.id.txtNombreUsuario);
        txttelefono=(EditText)findViewById(R.id.txtNumeroTelefonico);
        txtcorreo=(EditText)findViewById(R.id.txtCorreoElectronico);
        txtrecorreo=(EditText)findViewById(R.id.txtReCorreoElectronico);
        txtcontraseña=(EditText)findViewById(R.id.txtContrasena);
        txtrecontraseña=(EditText)findViewById(R.id.txtReContrasena);
        btnRegistrar= (Button)findViewById(R.id.btnRegistrarse);
        btnRegistrar.setOnClickListener(this);
        usuario=new Usuario();
    }

    @Override
    public void onClick(View v) {
        if(v==btnRegistrar){
            if(txtnombre.getText().toString().isEmpty()){
                Toast.makeText(RegistrarUsuario.this,"Por Favor Ingrese Nombre de Usuario",Toast.LENGTH_SHORT)
                        .show();
            }else if(txttelefono.getText().toString().isEmpty() || txttelefono.getText().toString().length()<9){
                Toast.makeText(RegistrarUsuario.this,"Por Favor Ingrese Numero de Telefono Valido", Toast.LENGTH_SHORT)
                        .show();
            }else if(txtcorreo.getText().toString().isEmpty() || validarEmail(txtcorreo.getText().toString())==false){
                Toast.makeText(RegistrarUsuario.this,"Por Favor Ingrese un Correo Valido", Toast.LENGTH_SHORT)
                        .show();
            }else if(!txtcorreo.getText().toString().equals(txtrecorreo.getText().toString())){
                Toast.makeText(RegistrarUsuario.this,"Los Correos no Coinciden", Toast.LENGTH_SHORT)
                        .show();
            }else if(txtcontraseña.getText().toString().isEmpty() || txtcontraseña.getText().toString().length()<6 ){
                Toast.makeText(RegistrarUsuario.this,"Por Favor Ingrese una Contraseña Valida. Recuerde la contraseña debe ser de minimo 6 caracteres", Toast.LENGTH_SHORT)
                        .show();
            }else if(!txtcontraseña.getText().toString().equals(txtrecontraseña.getText().toString())){
                Toast.makeText(RegistrarUsuario.this,"Las Contraseñas no Coinciden", Toast.LENGTH_SHORT)
                        .show();
            }

            if(!txtnombre.getText().toString().isEmpty() &&
                    !txttelefono.getText().toString().isEmpty() && txttelefono.getText().toString().length()>=9 &&
                    !txtcorreo.getText().toString().isEmpty() && validarEmail(txtcorreo.getText().toString())==true &&
                    txtcorreo.getText().toString().equals(txtrecorreo.getText().toString()) &&
                    !txtcontraseña.getText().toString().isEmpty() && txtcontraseña.getText().toString().length()>=6 &&
                    txtcontraseña.getText().toString().equals(txtrecontraseña.getText().toString())){


                usuario.setCorreo_electronico(txtcorreo.getText().toString());
                usuario.setContraseña(txtcontraseña.getText().toString());
                usuario.setTelefono(txttelefono.getText().toString());
                usuario.setNombre_usuario(txtnombre.getText().toString());

                ParseUser user=new ParseUser();
                user.setUsername(usuario.getCorreo_electronico());
                user.setEmail(usuario.getCorreo_electronico());
                user.setPassword(usuario.getContraseña());
                user.put("telephone", usuario.getTelefono());
                user.put("nombre_usuario", usuario.getNombre_usuario());
                user.pinInBackground();
                user.saveInBackground();
                user.saveEventually();
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(RegistrarUsuario.this,"Registro Completado, Por Favor Confirme Correo Electronico", Toast.LENGTH_SHORT)
                                    .show();
                            ParseUser.logOut();
                            Intent intent = new Intent(RegistrarUsuario.this, Login.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegistrarUsuario.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            ParseUser.logOut();
                        }
                    }
                });
            }

        }
    }

    private boolean validarEmail(String email){
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
}
