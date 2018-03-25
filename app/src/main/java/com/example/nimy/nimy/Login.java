package com.example.nimy.nimy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nimy.nimy.Clases.Usuario;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String PATTERN_EMAIL = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";

    private Button btnLogin;
    private Button btnRegistrase;
    private EditText txtUsuario;
    private EditText txtContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("jBJh6jKCJipl2TdDTcTYt51Ftyg9jOBhfcfhubjj")
                .clientKey("chJvmPMPKY0QDIuklkvqmAfrWgxRkBNUiy8dEmoe")
                .server("https://parseapi.back4app.com")
                .enableLocalDataStore()
                .build() );
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
        txtUsuario=(EditText)findViewById(R.id.txtUsuario);
        txtContrasena=(EditText)findViewById(R.id.txtContrasena);
        btnLogin=(Button)findViewById(R.id.btnIniciarsesion);
        btnLogin.setOnClickListener(this);
        btnRegistrase=(Button)findViewById(R.id.btnRegistrarse);
        btnRegistrase.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnLogin){
            if(txtUsuario.getText().toString().isEmpty() || validarEmail(txtUsuario.getText().toString())==false){
                Toast.makeText(Login.this,"Por Favor Ingrese un Correo Valido", Toast.LENGTH_SHORT)
                        .show();
            }if(txtContrasena.getText().toString().isEmpty()){
                Toast.makeText(Login.this,"Por Favor Ingrese una Contraseña", Toast.LENGTH_SHORT)
                        .show();
            }

            if(!txtUsuario.getText().toString().isEmpty() && validarEmail(txtUsuario.getText().toString())==true
                    && !txtContrasena.getText().toString().isEmpty()){

                ParseUser.logInInBackground(txtUsuario.getText().toString(),txtContrasena.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            if(user.getBoolean("emailVerified")) {
                                Toast.makeText(Login.this,"Inicio de Sesion", Toast.LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                ParseUser.logOut();
                                Toast.makeText(Login.this,"Por Favor Verfique Correo", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            ParseUser.logOut();
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }

/*Funciones para el boton registrarse*/
        }if(v==btnRegistrase){
            Intent intent = new Intent(Login.this, RegistrarUsuario.class);
            startActivity(intent);
        }
    }

    private boolean validarEmail(String email){
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
}
