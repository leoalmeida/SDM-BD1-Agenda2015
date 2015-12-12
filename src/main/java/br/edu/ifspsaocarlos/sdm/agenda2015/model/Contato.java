package br.edu.ifspsaocarlos.sdm.agenda2015.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import br.edu.ifspsaocarlos.sdm.agenda2015.data.SQLiteHelper;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.AtributesProvider;

/**
 * Created by LeonardoAlmeida on 30/10/15.
 */
public class Contato implements Serializable{

    private static final long serialVersionUID = 1L;
    private long id;
    private String nome;
    private Long dtNasc;;
    private List<Atributo> atributos = new ArrayList<Atributo>();

    public Contato() {
    }

    public Contato(long id, String nome, Long dtNasc ,List<Atributo> attrs) {
        this.id = id;
        this.nome = nome;
        this.atributos = attrs;
        this.dtNasc = dtNasc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<Atributo> getAtributos() { return atributos;}

    public void addAttr(Atributo atributo) {
        this.atributos.add(atributo);
    }

    public void updAttr(int attrID, Atributo atributo) {
        this.atributos.set(attrID, atributo);
    }

    /*public void addFone(String fone) {
        this.fones.add(fone);
    }

    public void updFone(String fone) {
        this.fones.set(0, fone);
    }

    public void addFoneList(List<String> list) {
        this.fones.addAll(list);
    }*/

    public List<Atributo>  getEmails() {
        return atributos;
    }

    /*public void addEmail(String email) {
        this.emails.add(email);
    }

    public void updEmail(String email) {
        this.emails.set(0, email);
    }

    public void addEmailList(List<String> list) {
        this.emails.addAll(list);
    }*/


    public String listFones() {
        Iterator<Atributo> iterator = atributos.listIterator();

        String retorno = "";
        while (iterator.hasNext()){
            Atributo attr = iterator.next();
            if (attr.getAttrType().equals(AtributesProvider.Atributos.KEY_FONE)) {
                if (!retorno.isEmpty()) retorno = retorno.concat("/");
                retorno = retorno.concat(attr.getAttrValue());
            }
        }

        return retorno;
    }


    public String listEmails() {
        Iterator<Atributo> iterator = atributos.listIterator();

        String retorno = "";
        while (iterator.hasNext()){
            Atributo attr = iterator.next();
            if (attr.getAttrType().equals(AtributesProvider.Atributos.KEY_EMAIL)) {
                if (!retorno.isEmpty()) retorno = retorno.concat("/");
                retorno = retorno.concat(attr.getAttrValue());
            }
        }

        return retorno;
    }

}
