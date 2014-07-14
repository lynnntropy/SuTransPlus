package com.omegavesko.sutransplus;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omega_000 on 7/12/2014.
 */
public class LineActivityListAdapter extends ArrayAdapter<Polazak>
{

//    static class ViewHolderItem{
//        TextView lineName;
//    }

    private final Context context;
    private final Polazak[] values;

//    private List<String> lineList = new ArrayList<String>();
//    private List<String> cityLineList = new ArrayList<String>();
//    private List<String> commuterLineList = new ArrayList<String>();

    private List<String> city_toWeekday = new ArrayList<String>();
    private List<String> city_toWeekend = new ArrayList<String>();
    private List<String> city_fromWeekday = new ArrayList<String>();
    private List<String> city_fromWeekend = new ArrayList<String>();

    private List<String> commuter_toWeekday = new ArrayList<String>();
    private List<String> commuter_toSaturday = new ArrayList<String>();
    private List<String> commuter_toSunday = new ArrayList<String>();
    private List<String> commuter_fromWeekday = new ArrayList<String>();
    private List<String> commuter_fromSaturday = new ArrayList<String>();
    private List<String> commuter_fromSunday = new ArrayList<String>();

    private Typeface robotoLight;


    public LineActivityListAdapter(Context context, Polazak[] values) {
        super(context, R.layout.home_listitem_layout, values);
        this.context = context;
        this.values = values;

        robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

        if (values[0].gradski) // handle city buses
        {
            // populate the lists with values for each column

            for(Polazak bus: values)
            {
                if (bus.day == Polazak.Day.RADNIDAN && bus.odlazak)
                {
                    city_toWeekday.add(bus.busInfo);
                }
                else if (bus.day == Polazak.Day.VIKEND && bus.odlazak)
                {
                    city_toWeekend.add(bus.busInfo);
                }
                else if (bus.day == Polazak.Day.RADNIDAN && !bus.odlazak)
                {
                    city_fromWeekday.add(bus.busInfo);
                }
                else if (bus.day == Polazak.Day.VIKEND && !bus.odlazak)
                {
                    city_fromWeekend.add(bus.busInfo);
                }
            }
        }
        else // handle commuter buses
        {
            for (Polazak bus: values)
            {
                if (bus.odlazak)
                {
                    if (bus.day == Polazak.Day.RADNIDAN)
                    {
                        commuter_toWeekday.add(bus.busInfo);
                    }
                    else if (bus.day == Polazak.Day.SUBOTA)
                    {
                        commuter_toSaturday.add(bus.busInfo);
                    }
                    else
                    {
                        commuter_toSunday.add(bus.busInfo);
                    }
                }
                else
                {
                    if (bus.day == Polazak.Day.RADNIDAN)
                    {
                        commuter_fromWeekday.add(bus.busInfo);
                    }
                    else if (bus.day == Polazak.Day.SUBOTA)
                    {
                        commuter_fromSaturday.add(bus.busInfo);
                    }
                    else
                    {
                        commuter_fromSunday.add(bus.busInfo);
                    }
                }
            }
        }



    }

    // prevent out of bounds exception at the end of the list
    @Override
    public int getCount()
    {
        if (values[0].gradski) return city_fromWeekday.size() + city_fromWeekend.size() + city_toWeekday.size() + city_toWeekend.size() + 6;
        else return commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + commuter_fromSaturday.size() + commuter_fromSunday.size() + 8;
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int categoryTitleLayout = R.layout.home_titleitem_layout;
        final int subcategoryTitleLayout = R.layout.line_subcategory_layout;
        final int busInfoViewLayout = R.layout.line_info_row_layout;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;


//        View rowView = convertView;
//        TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

//        if (position == 0)
//        {
//            rowView = inflater.inflate(R.layout.home_titleitem_layout, parent, false);
//            TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);
//
//            textView.setText("GRADSKE LINIJE");
//        }
//        else if (position > 0 && position <= cityLineList.size())
//        {
//            rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);
//            TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);
//
//            textView.setText(cityLineList.get(position - 1));
//        }
//        else if (position == cityLineList.size() + 1)
//        {
//            rowView = inflater.inflate(R.layout.home_titleitem_layout, parent, false);
//            TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);
//
//            textView.setText("PRIGRADSKE LINIJE");
//        }
//        else if (position <= cityLineList.size() + commuterLineList.size() + 1)
//        {
//            rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);
//            TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);
//
//            textView.setText(commuterLineList.get(position - cityLineList.size() - 1));
//        }
//        else rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);

        if (values[0].gradski)
        {
            // handle city buses

            if (position == 0)
            {
                rowView = inflater.inflate(categoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("U ODLASKU");
            }
            else if (position == 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("RADNI DAN");
            }
            else if (position > 1 && position <= 1 + city_toWeekday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(city_toWeekday.get(position - 2));
                textView.setTypeface(robotoLight);
            }
            else if (position == 1 + city_toWeekday.size() + 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("VIKEND I PRAZNICI");
            }
            else if (position > 1 + city_toWeekday.size() + 1 && position <= 1 + city_toWeekday.size() + 1 + city_toWeekend.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(city_toWeekend.get(position - (1 + 1 + city_toWeekday.size() + 1)));
                textView.setTypeface(robotoLight);
            }
            else if (position == 1 + city_toWeekday.size() + 1 + city_toWeekend.size() + 1)
            {
                rowView = inflater.inflate(categoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("U POVRATKU");
            }
            else if (position == 1 + city_toWeekday.size() + 1 + city_toWeekend.size() + 1 + 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("RADNI DAN");
            }
            else if (position > city_toWeekday.size() + city_toWeekend.size() + 4 && position <= city_toWeekday.size() + city_toWeekend.size() + 4 + city_fromWeekday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(city_fromWeekday.get(position - (city_toWeekday.size() + city_toWeekend.size() + 4 + 1)));
                textView.setTypeface(robotoLight);
            }
            else if (position == city_toWeekday.size() + city_toWeekend.size() + 4 + city_fromWeekday.size() + 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("VIKEND I PRAZNICI");
            }
            else if (position > city_toWeekday.size() + city_toWeekend.size() + city_fromWeekday.size() + 5 && position <= city_toWeekday.size() + city_toWeekend.size() + city_fromWeekday.size() + 5 + city_fromWeekend.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(city_fromWeekend.get(position - (city_toWeekday.size() + city_toWeekend.size() + city_fromWeekday.size() + 5 + 1)));
                textView.setTypeface(robotoLight);
            }
            else rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);
        }
        else
        {
            // handle commuter buses

            if (position == 0)
            {
                rowView = inflater.inflate(categoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("U ODLASKU");
            }
            else if (position == 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("RADNI DAN");
            }
            else if (position > 1 && position <= 1 + commuter_toWeekday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(commuter_toWeekday.get(position - 2));
                textView.setTypeface(robotoLight);
            }
            else if (position == 1 + commuter_toWeekday.size() + 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("SUBOTA");
            }
            else if (position > 1 + commuter_toWeekday.size() + 1 && position <= commuter_toWeekday.size() + 2 + commuter_toSaturday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(commuter_toSaturday.get(position - (commuter_toWeekday.size() + 2 + 1)));
                textView.setTypeface(robotoLight);
            }
            else if (position == commuter_toWeekday.size() + 2 + commuter_toSaturday.size() + 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("NEDELJA (PRAZNICI)");
            }
            else if (position > commuter_toWeekday.size() + commuter_toSaturday.size() + 3 && position <= commuter_toWeekday.size() + commuter_toSaturday.size() + 3 + commuter_toSunday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(commuter_toSunday.get(position - (commuter_toWeekday.size() + commuter_toSaturday.size() + 3 + 1)));
                textView.setTypeface(robotoLight);
            }
            else if (position == commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + 4)
            {
                rowView = inflater.inflate(categoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("U POVRATKU");
            }
            else if (position == commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + 5)
            {
                rowView = inflater.inflate(categoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("RADNI DAN");
            }
            else if (position > commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + 5 && position <= commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + 5 + commuter_fromWeekday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(commuter_fromWeekday.get(position - (commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + 5 + 1)));
                textView.setTypeface(robotoLight);
            }
            else if (position == commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + 5 + commuter_fromWeekday.size() + 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("SUBOTA");
            }
            else if (position > commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + 6 && position <= commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + 6 + commuter_fromSaturday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(commuter_fromSaturday.get(position - (commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + 6 + 1)));
                textView.setTypeface(robotoLight);
            }
            else if (position == commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + 6 + commuter_fromSaturday.size() + 1)
            {
                rowView = inflater.inflate(subcategoryTitleLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTitleTextView);

                textView.setText("NEDELJA (PRAZNICI)");
            }
            else if (position > commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + commuter_fromSaturday.size() + 7 && position <= commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + commuter_fromSaturday.size() + 7 + commuter_fromSunday.size())
            {
                rowView = inflater.inflate(busInfoViewLayout, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.mainTextView);

                textView.setText(commuter_fromSunday.get(position - (commuter_toWeekday.size() + commuter_toSaturday.size() + commuter_toSunday.size() + commuter_fromWeekday.size() + commuter_fromSaturday.size() + 7 + 1)));
                textView.setTypeface(robotoLight);
            }

            else rowView = inflater.inflate(R.layout.home_listitem_layout, parent, false);
        }


        return rowView;
    }

}
