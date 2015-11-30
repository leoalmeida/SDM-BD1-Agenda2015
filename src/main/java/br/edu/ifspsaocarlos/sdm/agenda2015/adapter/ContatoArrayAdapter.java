package br.edu.ifspsaocarlos.sdm.agenda2015.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.Contato;

/**
 * Created by LeonardoAlmeida on 02/11/15.
 */
public class ContatoArrayAdapter extends ArrayAdapter<Contato>{
    private LayoutInflater inflater;

    public ContatoArrayAdapter(Activity activity, List<Contato> objects) {
        super(activity, R.layout.contato_celula, objects);
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.contato_celula, null);
            holder = new ViewHolder();
            holder.nome =  (TextView) convertView.findViewById(R.id.nome);
            holder.fone =  (TextView) convertView.findViewById(R.id.fone);
            holder.dtNasc = (TextView) convertView.findViewById(R.id.dtNasc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contato c = getItem(position);
        holder.nome.setText(c.getNome());
        holder.fone.setText(c.listFones());
        holder.dtNasc.setText(c.getFormattedDtNasc());
        return convertView;
    }

    static class ViewHolder {
        public TextView nome;
        public TextView fone;
        public TextView dtNasc;
    }

}
