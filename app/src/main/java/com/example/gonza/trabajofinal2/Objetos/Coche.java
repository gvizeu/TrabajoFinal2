package com.example.gonza.trabajofinal2.Objetos;

/**
 * Created by gonza on 18/02/2018.
 */

public class Coche {
    String marca;
    String propietario;
    String email;
    String estado;
    String modelo;
    String imagenURL;



    public Coche() {


    }

    public Coche(String marca, String propietario, String email, String estado, String modelo, String imagenURL) {
        this.marca = marca;
        this.propietario = propietario;
        this.email = email;
        this.estado = estado;
        this.modelo = modelo;
        this.imagenURL = imagenURL;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }
}
