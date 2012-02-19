package fr.pronoschallenge.club;

import java.util.Date;

public class ClubCalendrierEntry {
	private String butDom;
	private String nomClubExt;
	private String nomClubDom;
	private String butExt;
	private String matchDomExt;
	private String typeResultat;
	private int numJournee;
	private Date dateMatch;

	public String getNomClubExt() {
		return nomClubExt;
	}
	public void setNomClubExt(String nomClubExt) {
		this.nomClubExt = nomClubExt;
	}
	public String getMatchDomExt() {
		return matchDomExt;
	}
	public void setMatchDomExt(String matchDomExt) {
		this.matchDomExt = matchDomExt;
	}
	public String getTypeResultat() {
		return typeResultat;
	}
	public void setTypeResultat(String typeResultat) {
		this.typeResultat = typeResultat;
	}
	public String getNomClubDom() {
		return nomClubDom;
	}
	public void setNomClubDom(String nomClubDom) {
		this.nomClubDom = nomClubDom;
	}
	public int getNumJournee() {
		return numJournee;
	}
	public void setNumJournee(int numJournee) {
		this.numJournee = numJournee;
	}
	public Date getDateMatch() {
		return dateMatch;
	}
	public void setDateMatch(Date dateMatch) {
		this.dateMatch = dateMatch;
	}
	public String getButDom() {
		return butDom;
	}
	public void setButDom(String butDom) {
		this.butDom = butDom;
	}
	public String getButExt() {
		return butExt;
	}
	public void setButExt(String butExt) {
		this.butExt = butExt;
	}
}
