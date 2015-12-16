package br.edu.ifspsaocarlos.sdm.agenda2015.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;
import com.couchbase.lite.SavedRevision;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;

/**
 * Created by LeonardoAlmeida on 15/12/15.
 */
public class CouchSyncArrayAdapter extends ArrayAdapter<QueryRow>{

    private List<QueryRow> list;
    private final Context context;

    public CouchSyncArrayAdapter(Context applicationContext, int contato_celula, int nome, ArrayList<QueryRow> queryRows) {
        super(applicationContext, R.layout.contato_celula, nome, queryRows);
        this.context = applicationContext;
    }

    static class ViewHolder {
        public TextView nome;
        public TextView fone;
        public TextView dtNasc;
        public ImageView thumb;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater vi = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.contato_celula, null);

            holder = new ViewHolder();
            holder.nome =  (TextView) convertView.findViewById(R.id.nome);
            holder.fone =  (TextView) convertView.findViewById(R.id.fone);
            holder.dtNasc = (TextView) convertView.findViewById(R.id.dtNasc);
            holder.thumb = (ImageView) convertView.findViewById(R.id.thumbFoto);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try{
            QueryRow row = getItem(position);
            SavedRevision currentRevision = row.getDocument().getCurrentRevision();

            TextView nome = ((ViewHolder)convertView.getTag()).nome;
            TextView fone = ((ViewHolder)convertView.getTag()).fone;
            TextView dtNasc = ((ViewHolder)convertView.getTag()).dtNasc;
            ImageView thumb = ((ViewHolder)convertView.getTag()).thumb;

            String itemNome = (String) currentRevision.getProperty("nome");
            String itemfone = (String) currentRevision.getProperty("fone");
            String itemDtNasc = (String) currentRevision.getProperty("dtNasc");
            String itemThumb = (String) currentRevision.getProperty("thumb");

            nome.setText(itemNome);
            fone.setText(itemfone);
            dtNasc.setText(itemDtNasc);
            if (itemThumb != null) thumb.setImageURI(Uri.parse(itemThumb));
            else thumb.setImageResource(R.drawable.foto_ndisp);

        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

}
