package fr.pronoschallenge.stat.match;

import java.util.List;

public class StatMatchSerieListeEntry {
	
	List<StatMatchSerieEntry> statMatchSerieDom;
	List<StatMatchSerieEntry> statMatchSerieExt;

	public List<StatMatchSerieEntry> getStatMatchSerieDom() {
		return statMatchSerieDom;
	}
	public void setStatMatchSerieDom(List<StatMatchSerieEntry> statMatchSerieDom) {
		this.statMatchSerieDom = statMatchSerieDom;
	}
	public List<StatMatchSerieEntry> getStatMatchSerieExt() {
		return statMatchSerieExt;
	}
	public void setStatMatchSerieExt(List<StatMatchSerieEntry> statMatchSerieExt) {
		this.statMatchSerieExt = statMatchSerieExt;
	}

	
}
