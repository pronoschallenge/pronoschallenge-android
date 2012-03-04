package fr.pronoschallenge.afreechart;

import java.util.ArrayList;
import java.util.List;

public class EvolutionClassementEntry {
	private String typeChamp;	
	
	public String getTypeChamp() {
		return typeChamp;
	}

	public void setTypeChamp(String typeChamp) {
		this.typeChamp = typeChamp;
	}

	private List<EvolutionClassementDetailEntry> evolutionClassementDetail = new ArrayList<EvolutionClassementDetailEntry>();

    public List<EvolutionClassementDetailEntry> getEvolutionDetail() {
        return evolutionClassementDetail;
    }

    public void setEvolutionDetail(EvolutionClassementDetailEntry evolutionDetail) {
        this.evolutionClassementDetail.add(evolutionDetail);
    }
}
