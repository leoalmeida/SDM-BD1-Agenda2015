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

    SQLiteHelper dbHelper;
    UriMatcher sURImatcher;
    private static final int TODOS = 0;
    private static final int REGISTRO = 1;

    @Override
    public boolean onCreate() {

        dbHelper = new SQLiteHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = null;

        switch (sURImatcher.match(uri)){
            case TODOS:
                cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, projection, selection, selectionArgs, null, null, sortOrder );
                break;

            case REGISTRO:
                cursor = database.query(SQLiteHelper.DB_TABLE_CONTATO, projection, SQLiteHelper.KEY_ID + " = " + uri.getPathSegments(), null, null, null, sortOrder );
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");
        }

        database.close();

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (sURImatcher.match(uri)){
            case TODOS:
                return "vnd.android.cursor.dir/vnd.br.edu.ifspsaocarlos.projeto";

            case REGISTRO:
                return "vnd.android.cursor.item/vnd.br.edu.ifspsaocarlos.projeto";

            default:
                throw new IllegalArgumentException("Tipo desconhecido");
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        long id = database.insert(SQLiteHelper.DB_TABLE_CONTATO, null, values);

        uri = ContentUris.withAppendedId(uri, id);

        database.close();

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int count = 0;
        switch (sURImatcher.match(uri)) {
            case TODOS:
                count = database.delete(SQLiteHelper.DB_TABLE_CONTATO, selection, selectionArgs);
                break;

            case REGISTRO:
                count = database.delete(SQLiteHelper.DB_TABLE_CONTATO, SQLiteHelper.KEY_ID + " = " + uri.getPathSegments().get(1), null);
                break;

            default:
                throw new IllegalArgumentException("URI inválida");
        }

        database.close();
        return count;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        int uriType = sURImatcher.match(uri);

        int count = 0;

        switch (uriType){
            case TODOS:
                count=database.update(SQLiteHelper.DB_TABLE_CONTATO, values, selection, selectionArgs);
                break;

            case REGISTRO:
                count=database.update(SQLiteHelper.DB_TABLE_CONTATO, values, SQLiteHelper.KEY_ID + " = " + uri.getPathSegments().get(1), null );
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");
        }

        database.close();

        return count;
    }
}
