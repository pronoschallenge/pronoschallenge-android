package fr.pronoschallenge.classement;

public class ClassementEntry {
	private int place;
	private int placePrec;
	private String pseudo;
	private double points;
	public int getPlace() {
		return place;
	}
	public void setPlace(int place) {
		this.place = place;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public double getPoints() {
		return points;
	}
	public void setPoints(double points) {
		this.points = points;
	}
	public int getPlacePrec() {
		return placePrec;
	}
	public void setPlacePrec(int placePrec) {
		this.placePrec = placePrec;
	}
}
