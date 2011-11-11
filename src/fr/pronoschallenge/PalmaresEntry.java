package fr.pronoschallenge;

import java.util.ArrayList;
import java.util.List;

public class PalmaresEntry {
	private String nomSaison;	
	private List<PalmaresDetailEntry> palmaresDetail = new ArrayList<PalmaresDetailEntry>();

    public String getNomSaison() {
        return nomSaison;
    }

    public void setNomSaison(String nomSaison) {
        this.nomSaison = nomSaison;
    }

    public List<PalmaresDetailEntry> getPalmaresDetail() {
        return palmaresDetail;
    }

    public void setPalmaresDetail(PalmaresDetailEntry palmaresDetail) {
        this.palmaresDetail.add(palmaresDetail);
    }
}
