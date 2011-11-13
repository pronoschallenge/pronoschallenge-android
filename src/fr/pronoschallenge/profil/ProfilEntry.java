package fr.pronoschallenge.profil;

public class ProfilEntry {
	private int id_membre;
	private String pseudo;
	private String url_avatar;
	private String nom;
	private String prenom;
	private String club_favori;
	private String ville;
	private String departement;
	private String url_logo;
	
	public int getId_membre() {
		return id_membre;
	}
	public void setId_membre(int id_membre) {
		this.id_membre= id_membre;
	}
	
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	public String getUrl_avatar() {
		return url_avatar;
	}
	public void setUrl_avatar(String url_avatar) {
		this.url_avatar= url_avatar;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		if (nom == null || nom == "null") {
			this.nom = "";
		} else {
			this.nom = nom;	
		}		
	}
	
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		if (prenom == null || prenom == "null") {
			this.prenom = "";
		} else {
			this.prenom = prenom;	
		}		
	}
	
	public String getClub_favori() {
		return club_favori;
	}
	public void setClub_favori(String club_favori) {
		if (club_favori == null || club_favori == "null") {
			this.club_favori = "";
		} else {
			this.club_favori = club_favori;	
		}		
	}
	
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		if (ville == null || ville == "null") {
			this.ville = "";
		} else {
			this.ville = ville;	
		}		
	}	
	public String getDepartement() {
		return departement;
	}
	public void setDepartement(String departement) {
		if (departement == null || departement == "null") {
			this.departement = "";
		} else {
			this.departement = departement;	
		}		
	}
	
	public String getUrl_logo() {
		return url_logo;
	}
	public void setUrl_logo(String url_logo) {
		this.url_logo= url_logo;
	}
	
}
