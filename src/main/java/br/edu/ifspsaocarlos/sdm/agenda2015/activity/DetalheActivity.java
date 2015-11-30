package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.data.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.agenda2015.data.SQLiteHelper;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Atributo;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Contato;

public class DetalheActivity extends AppCompatActivity implements DatePickerFragment.DateListerner{

    private Contato c;
    private ContatoDAO cDAO;
    private Button dtNasc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dtNasc = (Button) findViewById(R.id.dtNasc);

        if (getIntent().hasExtra("contato")){
            this.c = (Contato) getIntent().getSerializableExtra("contato");
            EditText nameText = (EditText) findViewById(R.id.nome);
            nameText.setText(c.getNome());
            EditText foneText = (EditText) findViewById(R.id.fone);
            foneText.setText(c.listFones());
            EditText mailText = (EditText) findViewById(R.id.email);
            mailText.setText(c.listEmails());

            dtNasc.setText(c.getFormattedDtNasc());

            int pos =c.getNome().indexOf(" ");
            if (pos==-1)
                pos=c.getNome().length();
            setTitle(c.getNome().substring(0,pos));
        }
        cDAO = new ContatoDAO(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if(!getIntent().hasExtra("contato")){
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
                 cDAO.deleteContact(c);
                 Toast.makeText(getApplicationContext(),"Removido com sucesso",Toast.LENGTH_SHORT).show();
                 Intent resultIntent = new Intent();
                 setResult(RESULT_OK, resultIntent);
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
        if (c==null){
            c = new Contato();
            c.setNome(name);
            c.setFormattedDtNasc(dateNasc);
            c = cDAO.createContact(c);
            c.addAttr(cDAO.createAttr(new Atributo(c.getId(), SQLiteHelper.KEY_FONE, fone)));
            c.addAttr(cDAO.createAttr(new Atributo(c.getId(), SQLiteHelper.KEY_EMAIL, email)));
            Toast.makeText(this, "Incluído com sucesso", Toast.LENGTH_SHORT).show();
        }  else {

            c.setNome(name);
            c.setFormattedDtNasc(dateNasc);
            cDAO.updateContact(c);
            cDAO.updateAttrs(c.getAtributos());
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
