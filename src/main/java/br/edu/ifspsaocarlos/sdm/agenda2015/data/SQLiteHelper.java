package br.edu.ifspsaocarlos.sdm.agenda2015.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Iterator;
import java.util.ListIterator;

import br.edu.ifspsaocarlos.sdm.agenda2015.model.Atributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Contato;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.AtributesProvider;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.ContatoProvider;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.EquipContatoProvider;

/**
 * Created by LeonardoAlmeida on 30/10/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "agenda.db";
    public static final String DB_TABLE_CONTATO = "contatos";
    public static final String DB_TABLE_ATTRS = "atributes";
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_CREATE = "CREATE TABLE " + DB_TABLE_CONTATO + " (" +
            ContatoProvider.Contatos.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ContatoProvider.Contatos.KEY_NAME + " TEXT NOT NULL, " +
            AtributesProvider.Atributos.KEY_FONE + " TEXT " +
            AtributesProvider.Atributos.KEY_EMAIL + " TEXT)";

    public static final String DB_CONTATO_RENAME = "ALTER TABLE " + DB_TABLE_CONTATO + " RENAME TO "+ DB_TABLE_CONTATO +"_BKP ";

    public static final String DB_CONTATO_CREATE = "CREATE TABLE " + DB_TABLE_CONTATO + " (" +
            ContatoProvider.Contatos.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ContatoProvider.Contatos.KEY_NAME + " TEXT NOT NULL) ";

    public static final String DB_CONTATO_ALTER = "ALTER TABLE " + DB_TABLE_CONTATO + " ADD COLUMN " + ContatoProvider.Contatos.KEY_DT_NIVER + " DATE  ";

    public static final String DB_BKP_DROP = "DROP TABLE "+ DB_TABLE_CONTATO +"_BKP ";

    public static final String DB_ATTRS_CREATE = "CREATE TABLE " + DB_TABLE_ATTRS + " (" +
            AtributesProvider.Atributos.KEY_ATTR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            AtributesProvider.Atributos.KEY_ID + " INTEGER NOT NULL, " +
            AtributesProvider.Atributos.KEY_TYPE + " TEXT NOT NULL, " +
            AtributesProvider.Atributos.KEY_ATTR + " TEXT NOT NULL);";

    public static final String DB_CONTATO_MIGRATE = "INSERT INTO " + DB_TABLE_CONTATO + " SELECT " + ContatoProvider.Contatos.KEY_ID +", " + ContatoProvider.Contatos.KEY_NAME + " FROM " + DB_TABLE_CONTATO +"_BKP ";
    public static final String DB_ATTR_MIGRATE = "INSERT INTO " + DB_TABLE_ATTRS +  "( " + AtributesProvider.Atributos.KEY_ID + "," + AtributesProvider.Atributos.KEY_TYPE + "," + AtributesProvider.Atributos.KEY_ATTR +
            ") SELECT " + ContatoProvider.Contatos.KEY_ID +", '" +  AtributesProvider.Atributos.KEY_FONE +"' AS " + AtributesProvider.Atributos.KEY_TYPE + " ," + AtributesProvider.Atributos.KEY_FONE +
            " FROM " + DB_TABLE_CONTATO +"_BKP" +
            " UNION ALL SELECT " + ContatoProvider.Contatos.KEY_ID +", '" +  AtributesProvider.Atributos.KEY_EMAIL +"' AS " + AtributesProvider.Atributos.KEY_TYPE + "," + AtributesProvider.Atributos.KEY_EMAIL +
            " FROM " + DB_TABLE_CONTATO +"_BKP";

    EquipContatoProvider provider;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        provider = new EquipContatoProvider(context);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_ATTRS_CREATE);
        db.execSQL(DB_CONTATO_CREATE);


        startContactList(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if ((oldVersion == 1) && (newVersion == 2)) {
            db.execSQL(DB_ATTRS_CREATE);
            db.execSQL(DB_CONTATO_RENAME);
            db.execSQL(DB_CONTATO_CREATE);
            db.execSQL(DB_CONTATO_MIGRATE);
            db.execSQL(DB_ATTR_MIGRATE);
            db.execSQL(DB_BKP_DROP);
        }
        if ((oldVersion == 2) && (newVersion == 3)) {
            db.execSQL(DB_CONTATO_ALTER);
        }
    }

    private void startContactList (SQLiteDatabase db) {

        Iterator<Contato> listIterator = provider.selecionaTodosContatosAparelho().iterator();
        while (listIterator.hasNext()) {
            Contato contato = listIterator.next();
            ContentValues values = new ContentValues();
            values.put(ContatoProvider.Contatos.KEY_ID, contato.getId());
            values.put(ContatoProvider.Contatos.KEY_NAME, contato.getNome());
            values.put(ContatoProvider.Contatos.KEY_DT_NIVER, contato.getDtNasc());

            db.insert(SQLiteHelper.DB_TABLE_CONTATO, null, values);

            Iterator<Atributo> iterator = contato.getAtributos().listIterator();

            while(iterator.hasNext()) {

                Atributo attr = iterator.next();

                values = new ContentValues();
                values.put(AtributesProvider.Atributos.KEY_ATTR_ID, attr.getAttrID());
                values.put(AtributesProvider.Atributos.KEY_ID, attr.getId());
                values.put(AtributesProvider.Atributos.KEY_TYPE, attr.getAttrType());
                values.put(AtributesProvider.Atributos.KEY_ATTR, attr.getAttrValue());
                db.insert(SQLiteHelper.DB_TABLE_ATTRS, null, values);
            }

        }
    }

}
