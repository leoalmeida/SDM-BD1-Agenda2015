package br.edu.ifspsaocarlos.sdm.agenda2015.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBAtributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;

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

    public List<FBContato> selecionaTodosContatosAparelho(){
        Uri contatos = ContactsContract.Contacts.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI};
        return buscaContatosAparelho(contatos, projecao, null);
    }

    public List<FBContato> selecionaContatosAparelho(String nome){
        Uri      contatos = ContactsContract.Contacts.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
        String   whereClause = ContactsContract.Contacts.DISPLAY_NAME + "=" + nome;

        return buscaContatosAparelho(contatos, projecao, whereClause);
    }

    @NonNull
    private List<FBContato> buscaContatosAparelho(Uri contatos, String[] projecao, String whereClause) {
        List<FBContato> contatosList = new ArrayList<FBContato>();

        Cursor cursor = context.getContentResolver().query(contatos, projecao, whereClause, null, ContactsContract.Contacts.DISPLAY_NAME);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                Log.d(TAG, "NOME: " + cursor.getString(1));
                //if (cursor.getString(2).equals("1")){

                    FBContato contato = new FBContato();
                    contato.setNome(cursor.getString(1));
                    contato.setFormattedDtNasc("01/01/1970");
                    //contato.setThumb_foto(cursor.getString(3));
                    contato.setThumb_foto("");
                    buscaListaEmailsContato(cursor.getLong(0) ,contato.getAtributos());
                    buscaListaFonesContato(cursor.getLong(0), contato.getAtributos());

                    contatosList.add(contato);
                //}
                cursor.moveToNext();
                if (cursor.getPosition() > 20) break;
            }
            cursor.close();
        }
        return contatosList;
    }

    private void buscaListaEmailsContato(Long idContato, List<FBAtributo> attrs) {

        Uri      emailsContato = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.LABEL, ContactsContract.CommonDataKinds.Email.MIMETYPE};
        String   whereClause = ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + idContato;

        Cursor cursor = context.getContentResolver().query(emailsContato, projecao, whereClause, null, null);


        searchAtributes(cursor, attrs);

    }

    private void buscaListaFonesContato(Long idContato,List<FBAtributo> attrs) {

        Uri      fonesContato = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projecao =  new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.LABEL, ContactsContract.CommonDataKinds.Phone.MIMETYPE};
        String   whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + idContato;

        Cursor cursor  = context.getContentResolver().query(fonesContato, projecao, whereClause, null, null);

        searchAtributes(cursor, attrs);

    }

    private void searchAtributes(Cursor cursor, List<FBAtributo> attrs) {

        if (cursor != null){

            cursor.moveToNext();
            while (!cursor.isAfterLast()){

                Log.d(TAG, "Value: " + cursor.getString(0));
                Log.d(TAG, "Type: " + cursor.getInt(1));
                Log.d(TAG, "Label: " + cursor.getString(2));
                Log.d(TAG, "Item Type: " + cursor.getString(3));

                attrs.add(new FBAtributo(cursor.getString(0), cursor.getInt(1), cursor.getString(2),cursor.getString(3)));

                cursor.moveToNext();

            }
            cursor.close();
        }
    }
}
