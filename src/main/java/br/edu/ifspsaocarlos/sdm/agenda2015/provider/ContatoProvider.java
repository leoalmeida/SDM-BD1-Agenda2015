package br.edu.ifspsaocarlos.sdm.agenda2015.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


import br.edu.ifspsaocarlos.sdm.agenda2015.data.SQLiteHelper;

/**
 * Created by LeonardoAlmeida on 27/11/15.
 */
public class ContatoProvider extends ContentProvider {


    private static final String TAG = "ContatoProvider";


    private static final int TODOS = 0;
    private static final int REGISTRO = 1;
    
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    static {
        sUriMatcher.addURI(Contatos.AUTHORITY, "contatos", TODOS);
        sUriMatcher.addURI(Contatos.AUTHORITY, "contatos/#", REGISTRO);
    }

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    @Override
    public boolean onCreate() {

        dbHelper = new SQLiteHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        database = dbHelper.getWritableDatabase();

        Cursor cursor = null;

        switch (sUriMatcher.match(uri)){
            case TODOS:
                cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, projection, selection, selectionArgs, null, null, sortOrder );
                break;

            case REGISTRO:
                cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, projection, Contatos.KEY_ID + " = " + uri.getPathSegments(), null, null, null, sortOrder );
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");
        }


        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)){
            case TODOS:
                return Contatos.CONTENT_TYPE;

            case REGISTRO:
                return Contatos.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Tipo desconhecido");
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        database = dbHelper.getWritableDatabase();
        
        int uriType = sUriMatcher.match(uri);
        long id;
        
        switch (uriType){
            case TODOS:
                id = database.insert(SQLiteHelper.DB_TABLE_CONTATO, null, values);
                break;
            default:
                throw new IllegalArgumentException("URI desconhecida");
        }
        
        uri = ContentUris.withAppendedId(uri, id);

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        database = dbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case TODOS:
                count = database.delete(SQLiteHelper.DB_TABLE_CONTATO, selection, selectionArgs);
                break;

            case REGISTRO:
                count = database.delete(SQLiteHelper.DB_TABLE_CONTATO, Contatos.KEY_ID + " = " + uri.getPathSegments().get(1), null);
                break;

            default:
                throw new IllegalArgumentException("URI inválida");
        }

        return count;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        database = dbHelper.getReadableDatabase();

        int uriType = sUriMatcher.match(uri);

        int count = 0;

        switch (uriType){
            case TODOS:
                count=database.update(SQLiteHelper.DB_TABLE_CONTATO, values, selection, selectionArgs);
                break;

            case REGISTRO:
                count=database.update(SQLiteHelper.DB_TABLE_CONTATO, values, Contatos.KEY_ID + " = " + uri.getPathSegments().get(1), null );
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");
        }

        return count;
    }
    
    public static final class Contatos{
        public static final String AUTHORITY = "br.edu.ifspsaocarlos.provider";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/contatos");
        public static final String CONTENT_TYPE= "vnd.android.cursor.dir/vnd.br.edu.ifspsaocarlos.agenda2015.contatos";
        public static final String CONTENT_ITEM_TYPE=
        "vnd.android.cursor.item/vnd.br.edu.ifspsaocarlos.agenda2015.contatos";
        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "nome";
        public static final String KEY_DT_NIVER = "data_niver";
    }
}
