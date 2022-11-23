package com.example.uniplus;

public class Reserva {
    private String nombreLugar;
    private String fechaInicio;
    private String horaInicio;
    private String fechaFinal;
    private String horaFinal;

    public String getNombreLugar() {
        return nombreLugar;
    }

    public void setNombreLugar(String nombreLugar) {
        this.nombreLugar = nombreLugar;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public Reserva() {
    }

    public Reserva(String nombreLugar, String fechaInicio, String horaInicio, String fechaFinal, String horaFinal) {
        this.nombreLugar = nombreLugar;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.fechaFinal = fechaFinal;
        this.horaFinal = horaFinal;
    }

}
