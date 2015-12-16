package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.replicator.Replication;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.adapter.CouchSyncArrayAdapter;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBAtributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.Constants;

public class DetalheActivity extends BaseActivity implements DatePickerFragment.DateListerner{


    private static final String TAG = "DetalheActivity";
    private FBContato contato;
    private Document document;
    Map<String, Object> properties;
    private Button dtNasc;
    private String documentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        //Firebase.setAndroidContext(this);
        //mFireBaseRef = new Firebase(Constants.FIREBASE_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dtNasc = (Button) findViewById(R.id.dtNasc);

        if (getIntent().hasExtra("DocumentID")){
            documentID = getIntent().getStringExtra("DocumentID");
        }

        document = buildUpdateListView(documentID);

        properties = new HashMap<String, Object>(document.getProperties());


            //Firebase refContato = mFireBaseRef.child(firebaseID);

            /*ValueEventListener refContatoListener = refContato.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contato = dataSnapshot.getValue(FBContato.class);

                    if (contato != null) {

                        EditText nameText = (EditText) findViewById(R.id.nome);
                        nameText.setText(contato.getNome());

                        EditText foneText = (EditText) findViewById(R.id.fone);
                        foneText.setText(contato.listaFones());

                        EditText mailText = (EditText) findViewById(R.id.email);
                        mailText.setText(contato.listaEmails());

                        dtNasc.setText(contato.getFormattedDtNasc());

                        int pos = contato.getNome().indexOf(" ");
                        if (pos == -1)
                            pos = contato.getNome().length();
                        setTitle(contato.getNome().substring(0, pos));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("LOG", firebaseError.getMessage());
                }
            });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if(documentID == null){
            MenuItem item = menu.findItem(R.id.delContato);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         switch (item.getItemId()){
             case R.id.salvarContato:
                 try {
                     salvar();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
                 return true;
             case R.id.delContato:
                 //mFireBaseRef.child(firebaseID).removeValue();

                 try {
                     document.delete();
                     syncAdapter.notifyDataSetChanged();
                     Toast.makeText(getApplicationContext(),"Removido com sucesso",Toast.LENGTH_SHORT).show();
                 } catch (Exception e) {
                     Toast.makeText(getApplicationContext(), "Error updating database, see logs for details", Toast.LENGTH_LONG).show();
                     com.couchbase.lite.util.Log.e(TAG, "Error updating database", e);
                 }
                 finish();
                 return true;
             default:
                 return super.onOptionsItemSelected(item);
         }

    }

    public void salvar() throws Exception {
        String name = ((EditText) findViewById(R.id.nome)).getText().toString();
        String dateNasc = ((Button) findViewById(R.id.dtNasc)).getText().toString();
        String fone = ((EditText) findViewById(R.id.fone)).getText().toString();
        String email = ((EditText) findViewById(R.id.email)).getText().toString();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        UUID uuid = UUID.randomUUID();
        Calendar calendar = GregorianCalendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        String id = currentTime + "-" + uuid.toString();

        if (contato==null){
            contato = new FBContato();
            contato.setNome(name);
            contato.setFormattedDtNasc(dateNasc);
            contato.addAttr(new FBAtributo(email,  ContactsContract.CommonDataKinds.Email.TYPE_HOME, ContactsContract.CommonDataKinds.Email.LABEL, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE));
            contato.addAttr(new FBAtributo(fone,  ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, ContactsContract.CommonDataKinds.Phone.LABEL, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE));

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(id, contato);

            document.putProperties(properties);

            com.couchbase.lite.util.Log.d(TAG, "Created new grocery item with id: %s", document.getId());

            //mFireBaseRef.push().setValue(contato);

            Toast.makeText(this, "Incluído com sucesso", Toast.LENGTH_SHORT).show();
        }  else {

            contato.setNome(name);
            contato.setFormattedDtNasc(dateNasc);

            properties.put(document.getId(), contato);

            try {
                document.putProperties(properties);
                localAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error updating database, see logs for details", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error updating database", e);
            }

            //mFireBaseRef.child(firebaseID).setValue(contato);

            Toast.makeText(this, "Alterado com sucesso", Toast.LENGTH_SHORT).show();
        }
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onReturnDate(String date) {
        dtNasc.setText(date);
    }

}
