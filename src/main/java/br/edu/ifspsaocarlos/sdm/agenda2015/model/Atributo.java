package br.edu.ifspsaocarlos.sdm.agenda2015.model;

import java.io.Serializable;

/**
 * Created by LeonardoAlmeida on 29/11/15.
 */
public class Atributo implements Serializable {

    private static final long serialVersionUID = 1L;
    private long attrID = -1;
    private long id;
    private String attrType;

    public Atributo(long attrID, long id, String attrType, String attrValue) {
        this.attrID = attrID;
        this.id = id;
        this.attrType = attrType;
        this.attrValue = attrValue;
    }

    public Atributo(long id, String attrType, String attrValue) {
        this.id = id;
        this.attrType = attrType;
        this.attrValue = attrValue;
    }

    public long getAttrID() {
        return attrID;
    }

    public void setAttrID(long attrID) {
        this.attrID = attrID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    private String attrValue;

}
