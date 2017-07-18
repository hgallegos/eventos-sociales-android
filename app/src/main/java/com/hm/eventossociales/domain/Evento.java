package com.hm.eventossociales.domain;

import android.databinding.BaseObservable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

/**
 * Created by hans6 on 30-06-2017.
 */
@Resource
@JsonIgnoreProperties(ignoreUnknown = true)
public class Evento extends BaseObservable implements Serializable {

    private String nombre;
    private String descripcion;
    private Date fechaRegistro;
    private Date fechaInicio;
    private Date fechaFin;
    private String visibilidad;
    private String pNombre;
    private String pDireccion;
    private double pLat;
    private double pLng;
    private String pTipo;
    private List<AsignaCategorias> asignaCategorias;

    @Link
    private HALLink self;

    @Link("evento")
    private HALLink evento;

    @Link("usuario")
    private HALLink usuario;

    @Link("eventoFotos")
    private HALLink fotos;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(String visibilidad) {
        this.visibilidad = visibilidad;
    }

    public String getpNombre() {
        return pNombre;
    }

    public void setpNombre(String pNombre) {
        this.pNombre = pNombre;
    }

    public String getpDireccion() {
        return pDireccion;
    }

    public void setpDireccion(String pDireccion) {
        this.pDireccion = pDireccion;
    }

    public double getpLat() {
        return pLat;
    }

    public void setpLat(double pLat) {
        this.pLat = pLat;
    }

    public double getpLng() {
        return pLng;
    }

    public void setpLng(double pLng) {
        this.pLng = pLng;
    }

    public String getpTipo() {
        return pTipo;
    }

    public void setpTipo(String pTipo) {
        this.pTipo = pTipo;
    }

    public HALLink getSelf() {
        return self;
    }

    public void setSelf(HALLink self) {
        this.self = self;
    }

    public HALLink getEvento() {
        return evento;
    }

    public void setEvento(HALLink evento) {
        this.evento = evento;
    }

    public HALLink getUsuario() {
        return usuario;
    }

    public void setUsuario(HALLink usuario) {
        this.usuario = usuario;
    }

    public HALLink getFotos() {
        return fotos;
    }

    public void setFotos(HALLink fotos) {
        this.fotos = fotos;
    }

    public List<AsignaCategorias> getAsignaCategorias() {
        return asignaCategorias;
    }

    public void setAsignaCategorias(List<AsignaCategorias> asignaCategorias) {
        this.asignaCategorias = asignaCategorias;
    }
}
