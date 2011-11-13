package fr.pronoschallenge;

import java.util.Date;

public class PronoEntry {
	private int id;
    private Date date;
	private String equipeDom;
	private String equipeExt;
    private String prono;
    private Integer butsDom;
    private Integer butsExt;

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
}
