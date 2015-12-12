package br.edu.ifspsaocarlos.sdm.agenda2015.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.agenda2015.data.SQLiteHelper;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Atributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Contato;

/**
 * Created by LeonardoAlmeida on 28/11/15.
 */
public class EquipContatoProvider {

    public static final String TAG = "EquipContatoProvider";

    private Context context;

    private static int attrID = 0;

    public EquipContatoProvider(Context context){
        this.context = context;
    }

    public List<Contato> selecionaTodosContatosAparelho(){
        Uri contatos = ContactsContract.Contacts.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
        return buscaContatosAparelho(contatos, projecao, null);
    }

    public List<Contato> selecionaContatosAparelho(String nome){
        Uri      contatos = ContactsContract.Contacts.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
        String   whereClause = ContactsContract.Contacts.DISPLAY_NAME + "=" + nome;

        return buscaContatosAparelho(contatos, projecao, whereClause);
    }

    @NonNull
    private List<Contato> buscaContatosAparelho(Uri contatos, String[] projecao, String whereClause) {
        List<Contato> contatosList = new ArrayList<Contato>();

        Cursor cursor = context.getContentResolver().query(contatos, projecao, whereClause, null, ContactsContract.Contacts.DISPLAY_NAME);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                Log.d(TAG, "NOME: " + cursor.getString(1));
                if (cursor.getString(2).equals("1")){

                    Contato contato = new Contato();
                    contato.setNome(cursor.getString(1));
                    contato.setId(cursor.getLong(0));
                    buscaListaEmailsContato(cursor.getLong(0), contato.getAtributos());
                    buscaListaFonesContato(cursor.getLong(0), contato.getAtributos());

                    contatosList.add(contato);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return contatosList;
    }

    private void buscaListaFonesContato(long idContato, List<Atributo> attrs) {

        Uri      fonesContato = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        String   whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + idContato;

        Cursor cursor  = context.getContentResolver().query(fonesContato, projecao, whereClause, null, null);

        searchAtributes(cursor, idContato, AtributesProvider.Atributos.KEY_FONE, attrs);

    }

    private void buscaListaEmailsContato(long idContato, List<Atributo> attrs) {

        Uri      emailsContato = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
        String   whereClause = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + idContato;

        Cursor cursor = context.getContentResolver().query(emailsContato, projecao, whereClause, null, null);


        searchAtributes(cursor, idContato, AtributesProvider.Atributos.KEY_FONE, attrs);

    }

    private void searchAtributes(Cursor cursor, long idContato, String attrType, List<Atributo> attrs) {

        if (cursor != null){

            cursor.moveToNext();
            while (!cursor.isAfterLast()){

                Log.d(TAG, "ATTR ID: " + attrID);
                Log.d(TAG, "CTR ID: " + idContato);
                Log.d(TAG, "TYPE: " + attrType);
                Log.d(TAG, "Value: " + cursor.getString(0));

                attrs.add(new Atributo(attrID++ ,idContato, attrType, cursor.getString(0)));

                cursor.moveToNext();

            }
            cursor.close();
        }
    }
}
