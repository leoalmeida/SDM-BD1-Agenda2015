package br.edu.ifspsaocarlos.sdm.agenda2015.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import java.io.InputStream;

import br.edu.ifspsaocarlos.sdm.agenda2015.R;
import br.edu.ifspsaocarlos.sdm.agenda2015.model.FBContato;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.CircleLoadingView;
import br.edu.ifspsaocarlos.sdm.agenda2015.utils.ImageLoader;

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
        super(activity, modelClass, modelLayout, query);
    }

    @Override
    protected void populateView(View v, FBContato model, int position) {
        ((TextView) v.findViewById(R.id.nome)).setText(model.getNome());
        ((TextView) v.findViewById(R.id.dtNasc)).setText(mActivity.getString(R.string.dtnascLabel) + model.getFormattedDtNasc());
        ((TextView) v.findViewById(R.id.fone)).setText(mActivity.getString(R.string.fonesLabel) + model.listaFones());

        if (model.getThumb_foto() != null) {

            // Loader image - will be shown before loading image
            int loader = R.drawable.foto_ndisp;

            // Imageview to show
            ImageView imageView = (ImageView) v.findViewById(R.id.thumbFoto);

            // Image url
            String image_url = model.getThumb_foto();

            // ImageLoader class instance
            ImageLoader imgLoader = new ImageLoader(mActivity);

            // whenever you want to load an image from url
            // call DisplayImage function
            // url - image url to load
            // loader - loader image, will be displayed before getting image
            // image - ImageView
            imgLoader.DisplayImage(image_url, loader, imageView);

            //new ImageProvider(imageView).execute(model.getThumb_foto());
            //((ImageView) v.findViewById(R.id.thumbFoto)).setImageURI(Uri.parse(model.getThumb_foto()));
        }
        else ((ImageView) v.findViewById(R.id.thumbFoto)).setImageResource(R.drawable.foto_ndisp);
    }

    class ImageProvider extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageProvider(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
