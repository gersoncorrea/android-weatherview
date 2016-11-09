package weather.com.weatherview.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gerson on 10/6/16.
 */

/**
 * Load weather icons in a separate thread
 */
public class LoadImageTask extends AsyncTask<String,Void,Bitmap> {
    private ImageView imageView;
    protected Map<String, Bitmap> bitmaps = new HashMap<>();

    /**
     * Store imageView
     * @param imageView
     */
    public LoadImageTask(ImageView  imageView){
        this.imageView = imageView;
    }

    /**
     * Load image
     * @param params
     * @return
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;

        try{
            // url for image
            URL url = new URL(params[0]);

            // open connection and download image
            connection = (HttpURLConnection)url.openConnection();
            try (InputStream inputStream = connection.getInputStream()){
                bitmap = BitmapFactory.decodeStream(inputStream);
                bitmaps.put(params[0],bitmap);
            } catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            // close connection
            connection.disconnect();
        }
        return bitmap;
    }

    /**
     * Set image in a list item
     * @param bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
