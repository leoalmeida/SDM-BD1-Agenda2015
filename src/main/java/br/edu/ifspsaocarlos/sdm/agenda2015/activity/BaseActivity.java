package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.ContatoFBAdapter;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.EquipContatoProvider;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.Constants;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    public ListView list;
    protected SearchView searchView;
    protected Firebase mFirebaseRef;
    protected ContatoFBAdapter mAdapter;

    private EquipContatoProvider provider;

    public enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
    }
    private static final String LAST_APP_VERSION = "last_app_version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        provider = new EquipContatoProvider(this);

        if (checkAppStart() == AppStart.FIRST_TIME) {
            startContactList();
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2,
                                    long arg3) {
                //FBContato contact = (FBContato) adapterView.getAdapter().getItem(arg2);
                Intent inte = new Intent(getApplicationContext(), DetalheActivity.class);
                inte.putExtra("FirebaseID", mAdapter.getRef(arg2).getKey());
                startActivityForResult(inte, 0);
            }
        });

        registerForContextMenu(list);

    }



    private void verifyCallList() {
        Uri ligacoes = CallLog.Calls.CONTENT_URI;

        String[] projecao =  new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE};

        try {

            Cursor lig = getContentResolver().query(ligacoes, projecao, null, null, null);

            if (lig == null) {
                lig.moveToFirst();

                while (!lig.isAfterLast()){

                    String num = lig.getString(0);
                    String duracao = lig.getString(1);
                    String tipo = null;
                    int typoCall = lig.getInt(2);
                    switch (typoCall){
                        case 1:
                            tipo = "Recebida";
                            break;
                        case 2:
                            tipo = "Efetuada";
                            break;
                        case 3:
                            tipo = "Perdida";
                            break;

                    }

                    Long data = lig.getLong(3);
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm");
                    String dataLigacao = format.format(new Date(data));

                    Log.d(TAG , "Numero:" + num);
                    Log.d(TAG , "Duracao:" + duracao);
                    Log.d(TAG , "Tipo:" + tipo);
                    Log.d(TAG , "Data:" + dataLigacao);
                    lig.moveToNext();

                }
            }

            lig.close();

        }catch (SecurityException e){
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.pesqContato).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        return true;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ContatoFBAdapter adapter = (ContatoFBAdapter) list.getAdapter();
        String FirebaseID = adapter.getRef(info.position).getKey().toString();
        switch(item.getItemId()){
            case R.id.delete_item:
                mFirebaseRef.child(FirebaseID).removeValue();
                Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
                buildListView();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void buildListView() {
        mAdapter = new ContatoFBAdapter(this, mFirebaseRef);
        list.setAdapter(mAdapter);
    }

    protected void buildSearchListView(String query){
        if (query.isEmpty()) {
            mAdapter = new ContatoFBAdapter(this,mFirebaseRef);
        }else {
            Query fbQuery = null;
            mAdapter = new ContatoFBAdapter(this,fbQuery);
        }
        list.setAdapter(mAdapter);
    }

    private void startContactList () {
        Iterator<FBContato> listIterator = provider.selecionaTodosContatosAparelho().iterator();
        while (listIterator.hasNext()) {
            FBContato contato = listIterator.next();

            mFirebaseRef.push().setValue(contato);

        }
    }

    public AppStart checkAppStart() {
        PackageInfo pInfo;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        AppStart appStart = AppStart.NORMAL;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int lastVersionCode = sharedPreferences
                    .getInt(LAST_APP_VERSION, -1);
            int currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);
            // Update version in preferences
            sharedPreferences.edit()
                    .putInt(LAST_APP_VERSION, currentVersionCode).commit();
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(Constants.LOG,
                    "Unable to determine current app version from pacakge manager. Defenisvely assuming normal app start.");
        }
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            Log.w(Constants.LOG, "Current version code (" + currentVersionCode
                    + ") is less then the one recognized on last startup ("
                    + lastVersionCode
                    + "). Defenisvely assuming normal app start.");
            return AppStart.NORMAL;
        } else {
            return AppStart.NORMAL;
        }
    }


}
