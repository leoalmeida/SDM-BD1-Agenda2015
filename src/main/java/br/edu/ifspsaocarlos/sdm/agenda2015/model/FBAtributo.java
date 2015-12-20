package br.edu.ifspsaocarlos.sdm.agenda2015.model;

import android.content.Context;

import br.edu.ifspsaocarlos.sdm.agenda2015.provider.ContactFakerProvider;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.Constants;

/**
 * Created by LeonardoAlmeida on 12/12/15.
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

    public static FBAtributo genFakeAttr(String attrType, Context context){
        FBAtributo atributo = new FBAtributo();

        if (attrType == Constants.TYPE_FONE) {
            atributo.setAttrType(0);
            atributo.setAttrValue(ContactFakerProvider.generatePropertyValue(ContactFakerProvider.PHONE.PHONENUMBER, context));
            atributo.setAttrLabel(Constants.TYPE_FONE);
        }else if (attrType == Constants.TYPE_EMAIL) {
            atributo.setAttrType(1);
            atributo.setAttrValue(ContactFakerProvider.generatePropertyValue(ContactFakerProvider.INTERNET.EMAIL, context));
            atributo.setAttrLabel(Constants.TYPE_EMAIL);
        }

        return atributo;
    }

    public FBAtributo(){};

}
