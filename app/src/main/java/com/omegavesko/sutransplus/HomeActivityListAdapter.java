package com.omegavesko.sutransplus;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by omega_000 on 7/12/2014.
 */
public class HomeActivityListAdapter extends ArrayAdapter<Polazak> {

//    static class ViewHolderItem{
//        TextView lineName;
//    }

    private final Context context;
    private final Polazak[] values;

    private List<String> lineList = new ArrayList<String>();
    private List<String> cityLineList = new ArrayList<String>();
    private List<String> commuterLineList = new ArrayList<String>();

    private Map<String, String> lineDetails;

    private Typeface robotoLight;

    public HomeActivityListAdapter(Context context, Polazak[] values, Map<String, String> lineDetails) {
        super(context, R.layout.home_listitem_layout, values);
        this.context = context;
        this.values = values;

        this.lineDetails = lineDetails;

        robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        for(Polazak bus: values)
        {
            String lineNumber = bus.linija;

            if (bus.gradski)
            {
                if (!cityLineList.contains(lineNumber))
                {
                    cityLineList.add(lineNumber);
                }
            }
            else
            {
                if (!commuterLineList.contains(lineNumber))
                {
                    commuterLineList.add(lineNumber);
                }
            }
        }
    }

    // prevent out of bounds exception at the end of the list
    @Override
    public int getCount() {
        return cityLineList.size() + commuterLineList.size() + 1 + 1;
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;


//        View rowView = convertView;
//        TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

        if (position == 0)
        {
            rowView = inflater.inflate(R.layout.home_titleitem_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

            textView.setText("GRADSKE LINIJE");
        }
        else if (position > 0 && position <= cityLineList.size())
        {
            rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

            textView.setText(cityLineList.get(position - 1));

            TextView smallTextView = (TextView) rowView.findViewById(R.id.smallTextView);
            smallTextView.setText(lineDetails.get(cityLineList.get(position - 1)));

            textView.setTypeface(robotoLight);
            smallTextView.setTypeface(robotoLight);
        }
        else if (position == cityLineList.size() + 1)
        {
            rowView = inflater.inflate(R.layout.home_titleitem_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

            textView.setText("PRIGRADSKE LINIJE");
        }
        else if (position <= cityLineList.size() + commuterLineList.size() + 1)
        {
            rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

            textView.setText(commuterLineList.get(position - cityLineList.size() - 2));

            TextView smallTextView = (TextView) rowView.findViewById(R.id.smallTextView);
            smallTextView.setText(lineDetails.get(commuterLineList.get(position - cityLineList.size() - 2)));

            textView.setTypeface(robotoLight);
            smallTextView.setTypeface(robotoLight);
        }
        else rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);

        return rowView;
    }



}
