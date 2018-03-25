package com.example.nimy.nimy.Clases;

import java.util.ArrayList;

public class Usuario {

    private String nombre_usuario;
    private String correo_electronico;
    private String telefono;
    private String contraseña;
    private ArrayList<Animal> animals;

    public String getNombre_usuario() {  return nombre_usuario;  }

    public void setNombre_usuario(String nombre_usuario) { this.nombre_usuario = nombre_usuario; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContraseña() { return contraseña; }

    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public ArrayList<Animal> getAnimals() { return animals; }

    public void setAnimals(ArrayList<Animal> animals) { this.animals = animals; }

    public String getCorreo_electronico() { return correo_electronico; }

    public void setCorreo_electronico(String correo_electronico) { this.correo_electronico = correo_electronico; }
}
