package com.example.reviste_app;



public class Direccion {
    private String nombre;
    private String departamento;
    private String municipio;
    private String telefono;
    private String direccion;
    private String codigoPostal;

    public Direccion() {
        // Constructor vacío requerido para Firestore
    }

    public Direccion(String nombre, String departamento, String municipio, String telefono, String direccion, String codigoPostal) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.municipio = municipio;
        this.telefono = telefono;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
    }

    // Agrega getters y setters según sea necesario

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
}
