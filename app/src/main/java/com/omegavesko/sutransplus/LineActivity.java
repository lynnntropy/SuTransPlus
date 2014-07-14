package com.omegavesko.sutransplus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.omegavesko.sutransplus.R;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LineActivity extends Activity {

    private String lineName;

    private TextView lineNameView;
    private ListView lineBusList;

    private TextView lineDetails;

    private List<Polazak> currentBusList;

    private Typeface robotoLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        robotoLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");

        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

        setContentView(R.layout.activity_line);

        lineNameView = (TextView) findViewById(R.id.lineName);
        lineBusList = (ListView) findViewById(R.id.lineBusList);

        Typeface robotoLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
        lineNameView.setTypeface(robotoLight);

        lineDetails = (TextView) findViewById(R.id.lineDetailsTextView);

        Intent intent = getIntent();
        lineName = intent.getStringExtra(HomeActivity.LINE_NAME);
        lineDetails.setText(intent.getStringExtra(HomeActivity.LINE_DETAILS));
        lineDetails.setTypeface(robotoLight);

        lineNameView.setText(lineName);

        DatabaseHandler db = new DatabaseHandler(this);
        List<Polazak> allBusList = db.getAllBuses();

        currentBusList = new ArrayList<Polazak>();

        for(Polazak bus: allBusList)
        {
            if (bus.linija.equals(lineName)) currentBusList.add(bus);
        }

        ArrayList<String> debugBusStrings = new ArrayList<String>();

        for (Polazak bus: currentBusList)
        {
            debugBusStrings.add(bus.busInfo);
        }

//        ArrayAdapter<String> debugListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, debugBusStrings);
//        lineBusList.setAdapter(debugListAdapter);

        LineActivityListAdapter listAdapter = new LineActivityListAdapter(this, currentBusList.toArray((new Polazak[currentBusList.size()])));
        lineBusList.setAdapter(listAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
