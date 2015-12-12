package br.edu.ifspsaocarlos.sdm.agenda2015.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.ui.auth.core.FirebaseAuthProvider;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;

/**
 * Created by LeonardoAlmeida on 11/12/15.
 */
public class ContatoFBAdapter extends FirebaseListAdapter<FBContato> {
    static final Class<FBContato> modelClass = FBContato.class;
    static final int modelLayout = R.layout.contato_celula;

    public ContatoFBAdapter(Activity activity, Firebase ref){
        super(activity,modelClass,modelLayout,ref);
    }

    public ContatoFBAdapter(Activity activity, Query query){
        super(activity,modelClass,modelLayout,query);
    }

    @Override
    protected void populateView(View v, FBContato model, int position) {
        ((TextView) v.findViewById(R.id.nome)).setText(model.getNome());
        ((TextView) v.findViewById(R.id.fone)).setText(model.getFone());
        ((TextView) v.findViewById(R.id.email)).setText(model.getEmail());
    }
}
