package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBAtributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.Constants;

public class DetalheActivity extends AppCompatActivity implements DatePickerFragment.DateListerner{

    private FBContato contato;
    private Button dtNasc;
    private String firebaseID;
    private Firebase mFireBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        Firebase.setAndroidContext(this);
        mFireBaseRef = new Firebase(Constants.FIREBASE_URL);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dtNasc = (Button) findViewById(R.id.dtNasc);

        if (getIntent().hasExtra("FirebaseID")){

            firebaseID = getIntent().getStringExtra("FirebaseID");
            Firebase refContato = mFireBaseRef.child(firebaseID);

            ValueEventListener refContatoListener = refContato.addValueEventListener(new ValueEventListener() {
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
            });


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if(firebaseID == null){
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
                 salvar();
                 return true;
             case R.id.delContato:
                 mFireBaseRef.child(firebaseID).removeValue();
                 Toast.makeText(getApplicationContext(),"Removido com sucesso",Toast.LENGTH_SHORT).show();
                 finish();
                 return true;
             default:
                 return super.onOptionsItemSelected(item);
         }

    }

    public void salvar(){
        String name = ((EditText) findViewById(R.id.nome)).getText().toString();
        String dateNasc = ((Button) findViewById(R.id.dtNasc)).getText().toString();
        String fone = ((EditText) findViewById(R.id.fone)).getText().toString();
        String email = ((EditText) findViewById(R.id.email)).getText().toString();

        if (contato==null){
            contato = new FBContato();
            contato.setNome(name);
            contato.setFormattedDtNasc(dateNasc);
            contato.addAttr(new FBAtributo(email,  ContactsContract.CommonDataKinds.Email.TYPE_HOME, ContactsContract.CommonDataKinds.Email.LABEL, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE));
            contato.addAttr(new FBAtributo(fone,  ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, ContactsContract.CommonDataKinds.Phone.LABEL, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE));

            mFireBaseRef.push().setValue(contato);

            Toast.makeText(this, "Incluído com sucesso", Toast.LENGTH_SHORT).show();
        }  else {

            contato.setNome(name);
            contato.setFormattedDtNasc(dateNasc);

            mFireBaseRef.child(firebaseID).setValue(contato);

            Toast.makeText(this, "Alterado com sucesso", Toast.LENGTH_SHORT).show();
        }
        Intent resultIntent = new Intent();
        setResult(RESULT_OK,resultIntent);
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
