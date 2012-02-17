package fr.pronoschallenge.profil;

public class ProfilStatEntry {
	private String numPlace;
	private String typeChamp;
	private String nbPoints;
	private String evolution;

    public String getNbPoints() {
		return nbPoints;
	}

	public void setNbPoints(String nbPoints) {
		this.nbPoints = nbPoints;
	}

	public String getEvolution() {
		return evolution;
	}

	public void setEvolution(String evolution) {
		this.evolution = evolution;
	}

	public String getNumPlace() {
        return numPlace;
    }

    public void setNumPlace(String numPlace) {
        this.numPlace = numPlace;
    }

    public String getTypeChamp() {
        return typeChamp;
    }

    public void setTypeChamp(String typeChamp) {
        this.typeChamp = typeChamp;
    }
}
