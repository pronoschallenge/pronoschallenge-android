package fr.pronoschallenge.amis.ajout;


public class AmisAjoutEntry {
	private String pseudo;
	private String nom;
	private String prenom;
	private Boolean ami;

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public Boolean getAmi() {
		return ami;
	}

	public void setAmi(String ami) {
		if (ami.compareTo("1") == 0) {
			this.ami = true;
		} else {
			this.ami = false;
		}
	}
}
