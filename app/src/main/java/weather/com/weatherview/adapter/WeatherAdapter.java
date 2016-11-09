package weather.com.weatherview.adapter;

/**
 * Created by gerson on 10/6/16.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weather.com.weatherview.request.LoadImageTask;
import weather.com.weatherview.R;
import weather.com.weatherview.model.Weather;

/**
 * Created by gerson on 10/5/16.
 */

public class WeatherAdapter extends ArrayAdapter<Weather> {
    protected Map<String, Bitmap> bitmaps = new HashMap<>();

    private static class ViewHolder{
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }

    public WeatherAdapter(Context context, List<Weather> objects) {
        super(context, -1, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get object for specified ListView
        Weather day = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item,parent,false);

            viewHolder.conditionImageView =
                    (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView =
                    (TextView) convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView =
                    (TextView) convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView =
                    (TextView) convertView.findViewById(R.id.hitTextView);
            viewHolder.humidityTextView =
                    (TextView) convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        }
        else { // reuse existing ViewHolder stored as the list item's tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (bitmaps.containsKey(day.iconURL)) {
            viewHolder.conditionImageView.setImageBitmap(
                    bitmaps.get(day.iconURL));
        }
        else {
            // download and display weather condition image
            new LoadImageTask(viewHolder.conditionImageView).execute(
                    day.iconURL);
        }
        Context context = getContext(); // for loading String resources
        viewHolder.dayTextView.setText(context.getString(
                R.string.day_description, day.dayOfWeek, day.description));
        viewHolder.lowTextView.setText(
                context.getString(R.string.low_temp, day.minTemp));
        viewHolder.hiTextView.setText(
                context.getString(R.string.high_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(
                context.getString(R.string.humidity, day.humidity));

        return convertView;
    }
}
