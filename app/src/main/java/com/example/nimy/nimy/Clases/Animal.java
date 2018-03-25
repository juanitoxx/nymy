package com.example.nimy.nimy.Clases;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Animal implements Serializable{

    private String email_dueño;
    private String dueño;
    private String numero_tel;
    private String nombre;
    private String fecha_nacimiento;
    private String tipo,raza, color;
    private String sexo;
    private Bitmap codigoQR;
    private String direccion;
    private Bitmap foto;
    private int edad;
    private ArrayList<Ubicacion> ubicaciones;
    private ArrayList<Evento> eventos;

    public void generarQR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode((this.getEmail_dueño()+":" + this.getDueño() + ":" + this.getNumero_tel()
                    + ":" + this.getNombre() + ":" + this.color + ":" + this.getDireccion()), BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            this.codigoQR = bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void calcularEdad(){
        String fecha[]=this.getFecha_nacimiento().split("/");
            Calendar hoy = Calendar.getInstance();
            int diffYear = hoy.get(Calendar.YEAR) - Integer.parseInt(fecha[2]);
            int diffMonth = hoy.get(Calendar.MONTH) - Integer.parseInt(fecha[1]);
            int diffDay = hoy.get(Calendar.DAY_OF_MONTH) - Integer.parseInt(fecha[0]);
            this.edad=diffYear;
            if (diffMonth < 0 || (diffMonth == 0 && diffDay < 0)) {
                this.edad = diffYear - 1;
            }
    }

    public String getSexo() {  return sexo;    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    public Bitmap getCodigoQR() {
        return codigoQR;
    }

    public void setCodigoQR(Bitmap codigoQR) {
        this.codigoQR = codigoQR;
    }

    public String getDueño() { return dueño; }

    public void setDueño(String dueño) { this.dueño = dueño; }

    public String getNumero_tel() { return numero_tel; }

    public void setNumero_tel(String numero_tel) { this.numero_tel = numero_tel; }

    public String getDireccion() { return direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Bitmap getFoto() {  return foto; }

    public void setFoto(Bitmap foto) { this.foto = foto; }

    public String getEmail_dueño() { return email_dueño; }

    public void setEmail_dueño(String email_dueño) { this.email_dueño = email_dueño; }

    public ArrayList<Ubicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(ArrayList<Ubicacion> ubicaciones) { this.ubicaciones = ubicaciones; }

    public ArrayList<Evento> getEventos() { return eventos; }

    public void setEventos(ArrayList<Evento> eventos) { this.eventos = eventos; }

    public int getEdad() {  return edad; }

    public void setEdad(int edad) { this.edad = edad; }
}
