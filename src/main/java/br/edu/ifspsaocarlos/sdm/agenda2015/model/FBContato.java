package br.edu.ifspsaocarlos.sdm.agenda2015.model;

import android.net.Uri;
import android.provider.ContactsContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by LeonardoAlmeida on 11/12/15.
 */
public class FBContato {
    private String  nome;
    private Long    dtNasc;
    private String  thumb_foto;
    private List<FBAtributo> atributos = new ArrayList<FBAtributo>();


    public String getThumb_foto() {
        return thumb_foto;
    }

    public void setThumb_foto(String thumb_foto) {
        this.thumb_foto = thumb_foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setFormattedDtNasc (String dtNasc){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (dtNasc != null) this.dtNasc = sdf.parse(dtNasc).getTime();
            else this.dtNasc = new Date().getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getFormattedDtNasc() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date(dtNasc));
    }

    public Long getDtNasc() {
        return this.dtNasc;
    }

    public void setDtNasc(Long dtNasc) {
        this.dtNasc = dtNasc;
    }

    public List<FBAtributo> getAtributos() { return atributos;}

    public void setAtributos(List<FBAtributo> atributos) {
        this.atributos = atributos;
    }


    public void addAttr(FBAtributo atributo) {
        this.atributos.add(atributo);
    }

    public void updAttr(int attrID, FBAtributo atributo) {
        this.atributos.set(attrID, atributo);
    }

    //public List<FBAtributo>  listaEmails() {return atributos;}

    public String listaFones() {
        Iterator<FBAtributo> iterator = atributos.listIterator();

        String retorno = "";
        while (iterator.hasNext()){
            FBAtributo attr = iterator.next();
            if (attr.getContentItemType().equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                if (!retorno.isEmpty()) retorno = retorno.concat(", ");
                retorno = retorno.concat(attr.getAttrValue());
            }
        }

        return retorno;
    }


    public String listaEmails() {
        Iterator<FBAtributo> iterator = atributos.listIterator();

        String retorno = "";
        while (iterator.hasNext()){
            FBAtributo attr = iterator.next();
            if (attr.getContentItemType().equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                if (!retorno.isEmpty()) retorno = retorno.concat("; ");
                retorno = retorno.concat(attr.getAttrValue());
            }
        }

        return retorno;
    }

    public FBContato(){};

}
