package br.edu.ifspsaocarlos.sdm.agenda2015.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import br.edu.ifspsaocarlos.sdm.agenda2015.model.Atributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Contato;

/**
 * Created by LeonardoAlmeida on 30/10/15.
 */
public class ContatoDAO {

    private static final String TAG = "ContatoDAO";

    private Context context;
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public ContatoDAO(Context context) {
        this.context = context;
        this.dbHelper = new SQLiteHelper(context);
    }

    public List<Contato> selecionaTodosContatosDB(){
        database = dbHelper.getReadableDatabase();
        List<Contato> contatosList = new ArrayList<Contato>();

        //Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[]{SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL}, null, null, null, null, SQLiteHelper.KEY_NAME);
        Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[]{SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_DT_NIVER}, null, null, null, null, SQLiteHelper.KEY_NAME);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){

                Contato contato = new Contato();
                contato.setId(cursor.getInt(0));
                contato.setNome(cursor.getString(1));
                contato.setFormattedDtNasc(cursor.getString(2));

                //Busca informacoes de Fone e Email
                Cursor cursorAttrs = database.query(SQLiteHelper.DB_TABLE_ATTRS, new String[]{SQLiteHelper.KEY_ATTR_ID, SQLiteHelper.KEY_ID, SQLiteHelper.KEY_TYPE, SQLiteHelper.KEY_ATTR}, SQLiteHelper.KEY_ID +"="+ cursor.getInt(0), null, null, null, SQLiteHelper.KEY_ID);

                if (cursorAttrs != null) {
                    cursorAttrs.moveToFirst();
                    while (!cursorAttrs.isAfterLast()) {

                        contato.addAttr(new Atributo(cursorAttrs.getLong(0), cursorAttrs.getLong(1), cursorAttrs.getString(2) , cursorAttrs.getString(3)));

                        cursorAttrs.moveToNext();
                    }
                    cursorAttrs.close();
                }
                contatosList.add(contato);
                cursor.moveToNext();
            }
            cursor.close();
        }
        database.close();
        return contatosList;
    }

    public List<Contato> selecionaContatoDB(String search){
        database = dbHelper.getReadableDatabase();
        List<Contato> contatos = new ArrayList<Contato>();

        //Exercicio 1 - Busca pelo Campo Nome ou Telefone
        //Exercicio 2 - Inclusao da busca pelo Campo Email
        //Exercicio 3 - Inclusao dos multiplos Fones e Emails (Busca apenas por nome)
        //Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[] { SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL}, SQLiteHelper.KEY_NAME + " like ? or " + SQLiteHelper.KEY_FONE + " like ? or " + SQLiteHelper.KEY_EMAIL + " like ?", new String[] { "%"+search+"%", "%"+search+"%", "%"+search+"%" },null, null, SQLiteHelper.KEY_NAME);
        Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[] { SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_DT_NIVER}, SQLiteHelper.KEY_NAME + " like ? ", new String[] { "%"+search+"%"},null, null, SQLiteHelper.KEY_NAME);

        if (cursor!= null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                Contato contato = new Contato();
                contato.setId(cursor.getInt(0));
                contato.setNome(cursor.getString(1));
                contato.setFormattedDtNasc(cursor.getString(2));

                //Busca informacoes de Fone e Email
                Cursor cursorAttrs = database.query(SQLiteHelper.DB_TABLE_ATTRS, new String[]{SQLiteHelper.KEY_ATTR_ID,SQLiteHelper.KEY_ID , SQLiteHelper.KEY_TYPE, SQLiteHelper.KEY_ATTR}, SQLiteHelper.KEY_ID +"="+ cursor.getInt(0), null, null, null, SQLiteHelper.KEY_ID);
                if (cursorAttrs != null) {
                    cursorAttrs.moveToFirst();
                    while (!cursorAttrs.isAfterLast()) {

                        contato.addAttr(new Atributo(cursorAttrs.getLong(0), cursorAttrs.getLong(1), cursorAttrs.getString(2) , cursorAttrs.getString(3)));

                        cursorAttrs.moveToNext();
                    }
                    cursorAttrs.close();
                }

                contatos.add(contato);
                cursor.moveToNext();
            }
            cursor.close();

        }
        database.close();
        return contatos;
    }

    public void updateContact (Contato c) {
        database = dbHelper.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(SQLiteHelper.KEY_NAME,c.getNome());
        updateValues.put(SQLiteHelper.KEY_DT_NIVER,c.getFormattedDtNasc());
        //updateValues.put(SQLiteHelper.KEY_EMAIL,c.getEmails().get(0));
        database.update(SQLiteHelper.DB_TABLE_CONTATO, updateValues, SQLiteHelper.KEY_ID + "=" + c.getId(), null);
        database.close();
    }

    public Contato createContact(Contato contato) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.KEY_NAME, contato.getNome());
        values.put(SQLiteHelper.KEY_DT_NIVER, contato.getFormattedDtNasc());
        //values.put(SQLiteHelper.KEY_EMAIL, c.getEmails().get(0));
        contato.setId(database.insert(SQLiteHelper.DB_TABLE_CONTATO, null, values));

        database.close();
        return contato;
    }

    public void deleteContact (Contato c) {
        database = dbHelper.getWritableDatabase();
        database.delete(SQLiteHelper.DB_TABLE_CONTATO, SQLiteHelper.KEY_ID + "=" + c.getId(), null);
        database.delete(SQLiteHelper.DB_TABLE_ATTRS, SQLiteHelper.KEY_ID + "=" + c.getId(), null);
        database.close();
    }


    public Atributo createAttr(Atributo atributo) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.KEY_ID, atributo.getId());
        values.put(SQLiteHelper.KEY_TYPE, atributo.getAttrType());
        values.put(SQLiteHelper.KEY_ATTR, atributo.getAttrValue());
        atributo.setId(database.insert(SQLiteHelper.DB_TABLE_ATTRS, null, values));

        database.close();
        return atributo;
    }

    public void updateAttrs(List<Atributo> atributos) {
        database = dbHelper.getWritableDatabase();
        Iterator<Atributo> iterator = atributos.listIterator();
        while (iterator.hasNext()) {
            Atributo attr = iterator.next();
            ContentValues updateValues = new ContentValues();
            updateValues.put(SQLiteHelper.KEY_ID, attr.getId());
            updateValues.put(SQLiteHelper.KEY_TYPE,attr.getAttrType());
            updateValues.put(SQLiteHelper.KEY_ATTR,attr.getAttrValue());
            database.update(SQLiteHelper.DB_TABLE_ATTRS, updateValues, SQLiteHelper.KEY_ATTR_ID + "=" + attr.getAttrID(), null);
        }
        database.close();

    }
}
