package fr.pronoschallenge.pronos;

import java.util.Date;

public class PronoEntry {
	private int id;
    private Date date;
	private String equipeDom;
	private String equipeExt;
    private String prono;
    private Integer butsDom;
    private Integer butsExt;
    private Integer cote1;
    private Integer coteN;
    private Integer cote2;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEquipeDom() {
        return equipeDom;
    }

    public void setEquipeDom(String equipeDom) {
        this.equipeDom = equipeDom;
    }

    public String getEquipeExt() {
        return equipeExt;
    }

    public void setEquipeExt(String equipeExt) {
        this.equipeExt = equipeExt;
    }

    public String getProno() {
        return prono;
    }

    public void setProno(String prono) {
        this.prono = prono;
    }

    public Integer getButsDom() {
        return butsDom;
    }

    public void setButsDom(int butsDom) {
        this.butsDom = butsDom;
    }

    public Integer getButsExt() {
        return butsExt;
    }

    public void setButsExt(Integer butsExt) {
        this.butsExt = butsExt;
    }

	public Integer getCote1() {
		return cote1;
	}

	public void setCote1(Integer cote1) {
		this.cote1 = cote1;
	}

	public Integer getCoteN() {
		return coteN;
	}

	public void setCoteN(Integer coteN) {
		this.coteN = coteN;
	}

	public Integer getCote2() {
		return cote2;
	}

	public void setCote2(Integer cote2) {
		this.cote2 = cote2;
	}

	public void setButsDom(Integer butsDom) {
		this.butsDom = butsDom;
	}
}
