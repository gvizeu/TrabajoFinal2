package com.example.gonza.trabajofinal2.Objetos;


public class Cliente {
    String nombre;
    String apellidos;
    String email;
    String pass;

    public Cliente(String nombre, String apellidos, String email) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;

    }

    public Cliente() {
    }

    public String getNombre() {return nombre;}

    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getApellidos() {return apellidos;}

    public void setApellidos(String apellidos) {this.apellidos = apellidos;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPass() {return pass;}

    public void setPass(String pass) {this.pass = pass;}
}
