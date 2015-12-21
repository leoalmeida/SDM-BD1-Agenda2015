package br.edu.ifspsaocarlos.sdm.agenda2015.model;

/**
 * Created by LeonardoAlmeida on 12/12/15.
 * Classe de atribuição dos atributos da agenda
 */
public class FBAtributo {

    private String attrValue;
    private int attrType;
    private String attrLabel;
    private String contentItemType;

    public FBAtributo(String attrValue, int attrType, String attrLabel,String contentItemType) {
        this.attrValue = attrValue;
        this.attrType = attrType;
        this.attrLabel = attrLabel;
        this.contentItemType = contentItemType;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public int getAttrType() {
        return attrType;
    }

    public void setAttrType(int attrType) {
        this.attrType = attrType;
    }

    public String getAttrLabel() {
        return attrLabel;
    }

    public void setAttrLabel(String attrLabel) {
        this.attrLabel = attrLabel;
    }


    public String getContentItemType() {
        return contentItemType;
    }

    public void setContentItemType(String contentItemType) {
        this.contentItemType = contentItemType;
    }

    public FBAtributo(){};

}
