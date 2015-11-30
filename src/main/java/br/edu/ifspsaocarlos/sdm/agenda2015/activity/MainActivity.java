package br.edu.ifspsaocarlos.sdm.agenda2015.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;

/**
 * Created by LeonardoAlmeida on 16/11/15.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DetalheActivity.class);
                startActivityForResult(intent,0);
            }
        });

        buildListView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        buildListView();
        searchView.clearFocus();
    }
}
