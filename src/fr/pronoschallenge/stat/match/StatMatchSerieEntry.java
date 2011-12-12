package fr.pronoschallenge.stat.match;

public class StatMatchSerieEntry {
	private int butDom;
	private String nomClubAdverse;
	private int butExt;
	private String matchDomExt;
	private String typeResultat;

	public String getNomClubAdverse() {
		return nomClubAdverse;
	}
	public void setNomClubAdverse(String nomClubAdverse) {
		this.nomClubAdverse = nomClubAdverse;
	}
	public String getMatchDomExt() {
		return matchDomExt;
	}
	public void setMatchDomExt(String matchDomExt) {
		this.matchDomExt = matchDomExt;
	}
	public int getButDom() {
		return butDom;
	}
	public void setButDom(int butDom) {
		this.butDom = butDom;
	}
	public int getButExt() {
		return butExt;
	}
	public void setButExt(int butExt) {
		this.butExt = butExt;
	}
	public String getTypeResultat() {
		return typeResultat;
	}
	public void setTypeResultat(String typeResultat) {
		this.typeResultat = typeResultat;
	}
}
