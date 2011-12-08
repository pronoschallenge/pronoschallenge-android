package fr.pronoschallenge.classement.club;

public class ClassementClubEntry {
	private int place;
	private String club;
	private String urlLogo;
	private int points;
	private int matchJoue;
	private int diff;
	
	public int getPlace() {
		return place;
	}
	public void setPlace(int place) {
		this.place = place;
	}
	public String getClub() {
		return club;
	}
	public void setClub(String club) {
		this.club = club;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getMatchJoue() {
		return matchJoue;
	}
	public void setMatchJoue(int matchJoue) {
		this.matchJoue = matchJoue;
	}
	public int getDiff() {
		return diff;
	}
	public void setDiff(int diff) {
		this.diff = diff;
	}
	public String getUrlLogo() {
		return urlLogo;
	}
	public void setUrlLogo(String urlLogo) {
		this.urlLogo = urlLogo;
	}

}
