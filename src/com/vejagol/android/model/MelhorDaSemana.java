package com.vejagol.android.model;

import java.io.Serializable;

public class MelhorDaSemana implements Serializable {

	private static final long serialVersionUID = -1249134453711742244L;

	private int id;
	
	private int mes;
	
	private int semana;
	
	private int ano;
	
	private String link;
	
	public MelhorDaSemana() {
	}

	public int getId() {
		return id;
	}

	public MelhorDaSemana(int mes, int semana, int ano, String link) {
		this.mes = mes;
		this.semana = semana;
		this.ano = ano;
		this.link = link;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getSemana() {
		return semana;
	}

	public void setSemana(int semana) {
		this.semana = semana;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
}
