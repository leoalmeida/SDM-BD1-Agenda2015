package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.app.ProgressDialog;
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
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.ContatoFBAdapter;
import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.CouchSyncArrayAdapter;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.EquipContatoProvider;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.Constants;


public class BaseActivity extends AppCompatActivity implements Replication.ChangeListener,
        OnItemClickListener, OnItemLongClickListener, OnKeyListener{

    public static String TAG = "Agenda2015";


    //Main Screen
    public ListView list;
    protected SearchView searchView;
    private EquipContatoProvider provider;

    //Firebase sync
    protected Firebase mFirebaseRef;
    protected ContatoFBAdapter syncAdapter;

    //couch internals
    protected static Manager manager;
    private Database database;
    private LiveQuery liveQuery;
    protected CouchSyncArrayAdapter localAdapter;

    @Override
    public void changed(Replication.ChangeEvent event) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

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

        try {
            startCBLite();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Initializing CBLIte, see logs for details", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error initializing CBLite", e);
        }



        list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(this);

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

    /**
     * Handle click on item in list
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //FBContato contact = (FBContato) adapterView.getAdapter().getItem(arg2);
        Intent inte = new Intent(getApplicationContext(), DetalheActivity.class);
        inte.putExtra("DocumentID", localAdapter.getItem(position).getDocumentId());
        startActivityForResult(inte, 0);
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
        //ContatoFBAdapter adapter = (ContatoFBAdapter) list.getAdapter();
        CouchSyncArrayAdapter adapter = (CouchSyncArrayAdapter) list.getAdapter();
        switch(item.getItemId()){
            case R.id.delete_item:
                //mFirebaseRef.child(FirebaseID).removeValue();
                Document document = adapter.getItem(info.position).getDocument();

                try {
                    document.delete();
                    syncAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error updating database, see logs for details", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error updating database", e);
                }

                Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
                buildListView();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    protected Document buildUpdateListView(String documentID) {
        //syncAdapter = new ContatoFBAdapter(this, mFirebaseRef);
        //list.setAdapter(syncAdapter);
        Document document;
        if (documentID == null) {
            document = database.getDocument(documentID);
        }else {
            document = database.createDocument();
        }

        initItemListAdapter();

        return document;
    }

    protected void buildListView() {
        //syncAdapter = new ContatoFBAdapter(this, mFirebaseRef);
        //list.setAdapter(syncAdapter);
        initItemListAdapter();
    }

    protected void buildSearchListView(String query){
        if (query.isEmpty()) {
            //syncAdapter = new ContatoFBAdapter(this,mFirebaseRef);
            initItemListAdapter();
        }else {
            //syncAdapter = new ContatoFBAdapter(this,fbQuery);

            initItemListAdapter();
        }
        //list.setAdapter(syncAdapter);

    }

    private void startContactList () {
        Iterator<FBContato> listIterator = provider.selecionaTodosContatosAparelho().iterator();
        while (listIterator.hasNext()) {
            FBContato contato = listIterator.next();

            //mFirebaseRef.push().setValue(contato);

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


    protected void startCBLite() throws Exception {

        Manager.enableLogging(TAG, Log.VERBOSE);
        Manager.enableLogging(Log.TAG, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_SYNC_ASYNC_TASK, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_SYNC, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_QUERY, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_VIEW, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_DATABASE, Log.VERBOSE);

        manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);

        //install a view definition needed by the application
        database = manager.getDatabase(Constants.CDB_NAME);
        com.couchbase.lite.View viewItemsByDate = database.getView(String.format("%s/%s", Constants.designDocName, Constants.byDateViewName));
        viewItemsByDate.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object createdAt = document.get("created_at");
                if (createdAt != null) {
                    emitter.emit(createdAt.toString(), null);
                }
            }
        }, "1.0");

        initItemListAdapter();

        startLiveQuery(viewItemsByDate);

        startSync();

    }

    private void initItemListAdapter() {
        localAdapter = new CouchSyncArrayAdapter(
                getApplicationContext(),
                R.layout.contato_celula,
                R.id.nome,
                new ArrayList<QueryRow>()
        );
        list.setAdapter(localAdapter);
        list.setOnItemClickListener(BaseActivity.this);
        list.setOnItemLongClickListener(BaseActivity.this);
    }

    private void startLiveQuery(com.couchbase.lite.View view) throws Exception {

        final ProgressDialog progressDialog = showLoadingSpinner();

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();

            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                public void changed(final LiveQuery.ChangeEvent event) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            localAdapter.clear();
                            for (Iterator<QueryRow> it = event.getRows(); it.hasNext();) {
                                localAdapter.add(it.next());
                            }
                            localAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    });
                }
            });

            liveQuery.start();

        }

    }

    private ProgressDialog showLoadingSpinner() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        return progress;
    }

    private void startSync() {

        URL syncUrl;
        try {
            syncUrl = new URL(Constants.FIREBASE_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Replication pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);

        Replication pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);

        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener(this);
        pushReplication.addChangeListener(this);

    }


}
