package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.ContatoFBAdapter;
import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.FakerInterface;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBAtributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;
import br.edu.ifspsaocarlos.sdm.agenda2015.provider.EquipContatoProvider;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.Constants;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.FakerProvider;


public class BaseActivity extends AppCompatActivity implements FakerInterface {

    private static final String TAG = "BaseActivity";

    public ListView list;
    protected SearchView searchView;
    protected Firebase refFirebase;
    protected Query refQuery;
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
        refFirebase = new Firebase(Constants.FIREBASE_URL);


        // Inicia lista com dados aleatorios para teste
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
                refFirebase.child(FirebaseID).removeValue();
                Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
                buildListView();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    //Preeche lista da tela
    protected void buildListView() {
        mAdapter = new ContatoFBAdapter(this, refFirebase);
        list.setAdapter(mAdapter);
    }

    //Preeche lista da tela com Filtro escolhido
    protected void buildSearchListView(String query){
        if (query.isEmpty()) {
            mAdapter = new ContatoFBAdapter(this, refFirebase);
        }else {
            refQuery = refFirebase.orderByChild(Constants.SEARCH_COLUMN).startAt(query);
            mAdapter = new ContatoFBAdapter(this, refQuery);
        }
        list.setAdapter(mAdapter);
    }

    //Verifica se e a primeira execucao do app
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

    //Verifica a troca de versao do app
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


    // Testa conectividade com a internet e
    // Gera informacoes iniciais para o App invocando o Faker
    private void startContactList () {

        //Teste utilizando as informacoes coletadas do host faker
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            int quant = 0;
            while (quant<5) {

                new FakerProvider(this, this).execute(FakerProvider.PROPERTY.FINDNAME.toString(),
                        FakerProvider.PROPERTY.PAST.toString(),
                        FakerProvider.PROPERTY.AVATAR.toString(),
                        FakerProvider.PROPERTY.PHONENUMBER.toString(),
                        FakerProvider.PROPERTY.EMAIL.toString());

                quant++;
            }

        } else {
            String msg = "Sem conexão com a internet";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    //Interface de retorno do Faker
    public void fakeContactResponse( Map<String,String> parametros ){
        if (parametros.size() > 0) {
            FBContato contato = new FBContato();
            contato.setNome(parametros.get(FakerProvider.PROPERTY.FINDNAME.toString()));
            try {
                String date_s = parametros.get(FakerProvider.PROPERTY.PAST.toString());
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date date = dt.parse(date_s);
                long milisecs = date.getTime() / 1000;
                contato.setDtNasc(milisecs);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            contato.setThumb_foto(parametros.get(FakerProvider.PROPERTY.AVATAR.toString()));

            FBAtributo atributo = new FBAtributo();
            atributo.setAttrType(0);
            atributo.setAttrValue(parametros.get(FakerProvider.PROPERTY.PHONENUMBER.toString()));
            atributo.setAttrLabel(Constants.TYPE_FONE);
            atributo.setContentItemType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            contato.addAttr(atributo);

            atributo = new FBAtributo();
            atributo.setAttrType(1);
            atributo.setAttrValue(parametros.get(FakerProvider.PROPERTY.EMAIL.toString()));
            atributo.setAttrLabel(Constants.TYPE_EMAIL);
            atributo.setContentItemType(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            contato.addAttr(atributo);

            refFirebase.push().setValue(contato);
        }
    }


}
