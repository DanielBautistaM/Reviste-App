package com.example.reviste_app;

import java.io.Serializable;

public class PagoInfo implements Serializable {
    private String numeroTarjeta;
    private int mesVencimiento;
    private int anioVencimiento;
    private String cvv;

    // Constructor vacío necesario para Firestore
    public PagoInfo() {
    }

    public PagoInfo(String numeroTarjeta, int mesVencimiento, int anioVencimiento, String cvv) {
        this.numeroTarjeta = numeroTarjeta;
        this.mesVencimiento = mesVencimiento;
        this.anioVencimiento = anioVencimiento;
        this.cvv = cvv;
    }

    // Métodos getter y setter

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public int getMesVencimiento() {
        return mesVencimiento;
    }

    public void setMesVencimiento(int mesVencimiento) {
        this.mesVencimiento = mesVencimiento;
    }

    public int getAnioVencimiento() {
        return anioVencimiento;
    }

    public void setAnioVencimiento(int anioVencimiento) {
        this.anioVencimiento = anioVencimiento;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
