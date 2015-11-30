package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
//import android.support.design.widget;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.ContatoArrayAdapter;
import br.edu.ifspsaocarlos.sdm.agenda2015.data.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Contato;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private ContatoDAO cDAO = new ContatoDAO(this);
    public ListView list;
    public ContatoArrayAdapter adapter;
    protected SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //verifyCallList();

        list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2,
                                    long arg3) {
                Contato contact = (Contato) adapterView.getAdapter().getItem(arg2);
                Intent inte = new Intent(getApplicationContext(), DetalheActivity.class);
                inte.putExtra("contato", contact);
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
            Log.d(this.TAG, e.toString());
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
        ContatoArrayAdapter adapter = (ContatoArrayAdapter) list.getAdapter();
        Contato contact = adapter.getItem(info.position);
        switch(item.getItemId()){
            case R.id.delete_item:
                cDAO.deleteContact(contact);
                Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
                buildListView();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void buildListView() {
        List<Contato> values = cDAO.selecionaTodosContatosDB();
        adapter = new ContatoArrayAdapter(this, values);
        list.setAdapter(adapter);
    }

    protected void buildSearchListView(String query){
        List<Contato> values = new ArrayList<Contato>();
        if (query.isEmpty()) {
            values = cDAO.selecionaTodosContatosDB();
        }else {
            values = cDAO.selecionaContatoDB(query);
        }
        adapter = new ContatoArrayAdapter(this,values);
        list.setAdapter(adapter);
    }


}
