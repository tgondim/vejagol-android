package com.vejagol.android.model;

import java.io.Serializable;
import java.util.Calendar;

public class Jogo implements Serializable {

	private static final long serialVersionUID = -3323680372985319633L;
	
	private int id;
	private Calendar data;
	private String timeCasa;
	private String timeVisitante;
	private int placarCasa;
	private int placarVisitante;
	private String campeonato;
	private String liga;
	private String link;
	
	public Jogo() {
		super();
	}
	
	public Jogo(Calendar data, String timeCasa,
			String timeVisitante, int placarCasa, int placarVisitante,
			String campeonato, String liga, String link) {
		super();
		this.data = data;
		this.timeCasa = timeCasa;
		this.timeVisitante = timeVisitante;
		this.placarCasa = placarCasa;
		this.placarVisitante = placarVisitante;
		this.campeonato = campeonato;
		this.liga = liga;
		this.link = link;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public String getTimeCasa() {
		return timeCasa;
	}

	public void setTimeCasa(String timeCasa) {
		this.timeCasa = timeCasa;
	}

	public String getTimeVisitante() {
		return timeVisitante;
	}

	public void setTimeVisitante(String timeVisitante) {
		this.timeVisitante = timeVisitante;
	}

	public int getPlacarCasa() {
		return placarCasa;
	}

	public void setPlacarCasa(int placarCasa) {
		this.placarCasa = placarCasa;
	}

	public int getPlacarVisitante() {
		return placarVisitante;
	}

	public void setPlacarVisitante(int placarVisitante) {
		this.placarVisitante = placarVisitante;
	}

	public String getCampeonato() {
		return campeonato;
	}

	public void setCampeonato(String campeonato) {
		this.campeonato = campeonato;
	}

	public String getLiga() {
		return liga;
	}

	public void setLiga(String liga) {
		this.liga = liga;
	}

	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	public boolean equals(Object o) {
		return (this.getData().equals(((Jogo)o).getData()) 
				&& this.getTimeCasa().equals(((Jogo)o).getTimeCasa())
				&& this.getTimeVisitante().equals(((Jogo)o).getTimeVisitante()));
	}
	
	@Override
	public String toString() {
		return this.getTimeCasa() + " " + this.getPlacarCasa() + " x " 
			+ this.getPlacarVisitante() + " " + this.getTimeVisitante();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Jogo(this.data, this.timeCasa, this.timeVisitante, this.placarCasa, this.placarVisitante, this.campeonato, this.liga, this.link);
	}
	
}
