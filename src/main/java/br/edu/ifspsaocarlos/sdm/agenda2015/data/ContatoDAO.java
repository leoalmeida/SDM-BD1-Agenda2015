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
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.AtributesProvider;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.ContatoProvider;

/**
 * Created by LeonardoAlmeida on 30/10/15.
 */
public class ContatoDAO {

    private static final String TAG = "ContatoDAO";

    private Context context;
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private ContatoProvider contatoProvider = new ContatoProvider();
    private AtributesProvider atributesProvider = new AtributesProvider();

    public ContatoDAO(Context context) {
        this.context = context;
        this.dbHelper = new SQLiteHelper(context);
    }

    public List<Contato> selecionaTodosContatosDB(){
        database = dbHelper.getReadableDatabase();
        List<Contato> contatosList = new ArrayList<Contato>();

        //Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[]{SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL}, null, null, null, null, SQLiteHelper.KEY_NAME);
        //Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[]{ContatoProvider.Contatos.KEY_ID, ContatoProvider.Contatos.KEY_NAME, ContatoProvider.Contatos.KEY_DT_NIVER}, null, null, null, null, ContatoProvider.Contatos.KEY_NAME);
        Cursor cursor = contatoProvider.query(ContatoProvider.Contatos.CONTENT_URI, new String[]{ContatoProvider.Contatos.KEY_ID, ContatoProvider.Contatos.KEY_NAME, ContatoProvider.Contatos.KEY_DT_NIVER}, null, null, ContatoProvider.Contatos.KEY_NAME);

        if (cursor != null) {
            while (cursor.moveToNext()){

                Contato contato = new Contato();
                contato.setId(cursor.getInt(0));
                contato.setNome(cursor.getString(1));
                contato.setFormattedDtNasc(cursor.getString(2));

                //Busca informacoes de Fone e Email
                //Cursor cursorAttrs = database.query(SQLiteHelper.DB_TABLE_ATTRS, new String[]{AtributesProvider.Atributos.KEY_ATTR_ID, AtributesProvider.Atributos.KEY_ID, AtributesProvider.Atributos.KEY_TYPE, AtributesProvider.Atributos.KEY_ATTR}, AtributesProvider.Atributos.KEY_ID +"="+ cursor.getInt(0), null, null, null, AtributesProvider.Atributos.KEY_ID);
                Cursor cursorAttrs = atributesProvider.query(AtributesProvider.Atributos.CONTENT_URI, new String[]{AtributesProvider.Atributos.KEY_ATTR_ID, AtributesProvider.Atributos.KEY_ID, AtributesProvider.Atributos.KEY_TYPE, AtributesProvider.Atributos.KEY_ATTR}, AtributesProvider.Atributos.KEY_ID +"="+ cursor.getInt(0), null, AtributesProvider.Atributos.KEY_ID);

                if (cursorAttrs != null) {
                    while (cursorAttrs.moveToNext()) {
                        contato.addAttr(new Atributo(cursorAttrs.getLong(0), cursorAttrs.getLong(1), cursorAttrs.getString(2), cursorAttrs.getString(3)));
                    }
                    cursorAttrs.close();
                }
                contatosList.add(contato);
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
        //Exercicio 4 - Utilizando provider
        //Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[] { SQLiteHelper.KEY_ID, SQLiteHelper.KEY_NAME, SQLiteHelper.KEY_FONE, SQLiteHelper.KEY_EMAIL}, SQLiteHelper.KEY_NAME + " like ? or " + SQLiteHelper.KEY_FONE + " like ? or " + SQLiteHelper.KEY_EMAIL + " like ?", new String[] { "%"+search+"%", "%"+search+"%", "%"+search+"%" },null, null, SQLiteHelper.KEY_NAME);
        //Cursor cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, new String[] { ContatoProvider.Contatos.KEY_ID, ContatoProvider.Contatos.KEY_NAME, ContatoProvider.Contatos.KEY_DT_NIVER}, ContatoProvider.Contatos.KEY_NAME + " like ? ", new String[] { "%"+search+"%"},null, null, ContatoProvider.Contatos.KEY_NAME);
        Cursor cursor = contatoProvider.query(ContatoProvider.Contatos.CONTENT_URI, new String[] { ContatoProvider.Contatos.KEY_ID, ContatoProvider.Contatos.KEY_NAME, ContatoProvider.Contatos.KEY_DT_NIVER}, ContatoProvider.Contatos.KEY_NAME + " like ? ", new String[] { "%"+search+"%"}, ContatoProvider.Contatos.KEY_NAME);

        if (cursor!= null){
            while (cursor.moveToNext()){
                Contato contato = new Contato();
                contato.setId(cursor.getInt(0));
                contato.setNome(cursor.getString(1));
                contato.setFormattedDtNasc(cursor.getString(2));

                //Busca informacoes de Fone e Email
                //Cursor cursorAttrs = database.query(SQLiteHelper.DB_TABLE_ATTRS, new String[]{AtributesProvider.Atributos.KEY_ATTR_ID,AtributesProvider.Atributos.KEY_ID , AtributesProvider.Atributos.KEY_TYPE, AtributesProvider.Atributos.KEY_ATTR}, AtributesProvider.Atributos.KEY_ID +"="+ cursor.getInt(0), null, null, null, AtributesProvider.Atributos.KEY_ID);
                Cursor cursorAttrs = atributesProvider.query(AtributesProvider.Atributos.CONTENT_URI, new String[]{AtributesProvider.Atributos.KEY_ATTR_ID,AtributesProvider.Atributos.KEY_ID , AtributesProvider.Atributos.KEY_TYPE, AtributesProvider.Atributos.KEY_ATTR}, AtributesProvider.Atributos.KEY_ID +"="+ cursor.getInt(0), null, AtributesProvider.Atributos.KEY_ID);
                if (cursorAttrs != null) {
                    while (cursorAttrs.moveToNext()) {

                        contato.addAttr(new Atributo(cursorAttrs.getLong(0), cursorAttrs.getLong(1), cursorAttrs.getString(2) , cursorAttrs.getString(3)));

                    }
                    cursorAttrs.close();
                }

                contatos.add(contato);
            }
            cursor.close();

        }
        database.close();
        return contatos;
    }

    public void updateContact (Contato c) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(ContatoProvider.Contatos.KEY_NAME,c.getNome());
        updateValues.put(ContatoProvider.Contatos.KEY_DT_NIVER, c.getFormattedDtNasc());
        //updateValues.put(SQLiteHelper.KEY_EMAIL,c.getEmails().get(0));
        contatoProvider.update(ContatoProvider.Contatos.CONTENT_URI, updateValues, ContatoProvider.Contatos.KEY_ID + "=" + c.getId(), null);
    }

    public Contato createContact(Contato contato) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContatoProvider.Contatos.KEY_NAME, contato.getNome());
        values.put(ContatoProvider.Contatos.KEY_DT_NIVER, contato.getFormattedDtNasc());
        //values.put(SQLiteHelper.KEY_EMAIL, c.getEmails().get(0));
        contato.setId(new Integer(contatoProvider.insert(ContatoProvider.Contatos.CONTENT_URI, values).getLastPathSegment()));

        return contato;
    }

    public void deleteContact (Contato c) {
        contatoProvider.delete(ContatoProvider.Contatos.CONTENT_URI, ContatoProvider.Contatos.KEY_ID + "=" + c.getId(), null);
        atributesProvider.delete(AtributesProvider.Atributos.CONTENT_URI, AtributesProvider.Atributos.KEY_ID + "=" + c.getId(), null);
    }


    public Atributo createAttr(Atributo atributo) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AtributesProvider.Atributos.KEY_ID, atributo.getId());
        values.put(AtributesProvider.Atributos.KEY_TYPE, atributo.getAttrType());
        values.put(AtributesProvider.Atributos.KEY_ATTR, atributo.getAttrValue());
        atributo.setId(new Integer(atributesProvider.insert(AtributesProvider.Atributos.CONTENT_URI, values).getLastPathSegment()));

        database.close();
        return atributo;
    }

    public void updateAttrs(List<Atributo> atributos) {
        database = dbHelper.getWritableDatabase();
        Iterator<Atributo> iterator = atributos.listIterator();
        while (iterator.hasNext()) {
            Atributo attr = iterator.next();
            ContentValues updateValues = new ContentValues();
            updateValues.put(AtributesProvider.Atributos.KEY_ID, attr.getId());
            updateValues.put(AtributesProvider.Atributos.KEY_TYPE,attr.getAttrType());
            updateValues.put(AtributesProvider.Atributos.KEY_ATTR,attr.getAttrValue());
            atributesProvider.update(AtributesProvider.Atributos.CONTENT_URI, updateValues, AtributesProvider.Atributos.KEY_ATTR_ID + "=" + attr.getAttrID(), null);
        }
        database.close();

    }
}
