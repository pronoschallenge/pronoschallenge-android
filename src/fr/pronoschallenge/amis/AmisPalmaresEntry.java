package fr.pronoschallenge.amis;

import java.util.ArrayList;
import java.util.List;

public class AmisPalmaresEntry {
	private String pseudo;	
	private List<AmisPalmaresDetailEntry> palmaresDetail = new ArrayList<AmisPalmaresDetailEntry>();

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public List<AmisPalmaresDetailEntry> getPalmaresDetail() {
        return palmaresDetail;
    }

    public void setPalmaresDetail(AmisPalmaresDetailEntry palmaresDetail) {
        this.palmaresDetail.add(palmaresDetail);
    }
}
