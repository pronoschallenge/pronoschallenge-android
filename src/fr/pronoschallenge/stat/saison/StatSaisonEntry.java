package fr.pronoschallenge.stat.saison;

import java.util.ArrayList;
import java.util.List;

public class StatSaisonEntry {
	private String type;	
	private List<StatSaisonDetailEntry> statDetail = new ArrayList<StatSaisonDetailEntry>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<StatSaisonDetailEntry> getStatDetail() {
		return statDetail;
	}
	public void setStatDetail(StatSaisonDetailEntry statDetail) {
		this.statDetail.add(statDetail);
	}
}
