package com.omegavesko.sutransplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class HomeActivity extends Activity {

    class GetLinesTask extends AsyncTask<String, Integer, org.jsoup.nodes.Document> {

        private org.jsoup.nodes.Document downloadedPage;

        protected org.jsoup.nodes.Document doInBackground(String... urls) {

            // download and return the webpage
            try
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                downloadBar.animate().alpha(1f).setDuration(150).setListener(null);
                                downloadText.animate().alpha(1f).setDuration(150).setListener(null);
                            }
                        }, 500);


                    }
                });

                DatabaseHandler db = new DatabaseHandler(homeActivityContext);
                Log.w(LogTag, "CLEARING SQLITE TABLE!");
                db.ClearTable();

                downloadFinishedCleanly = false;

                downloadedPage = Jsoup.connect(urls[0]).get();

                // TODO DEBUG
                Elements gradskiLineLinks = downloadedPage.select("div.schedule-list-wrap").select("ul.schedule-list.local-lines").select("a[href]");
                Log.i(LogTag, "-- GRADSKE LINIJE --");
                for (Element link: gradskiLineLinks)
                {
                    String lineURL = link.attr("abs:href");
                    String lineNumber = link.select("span.line-number").html();
                    String lineName = link.select("span.line-name").html();

                    Log.i(LogTag, "Link: " + lineNumber + ": " + lineName + " | " + lineURL);

                    lineNames.put(lineNumber, lineName);

                    SetDownloadText("Preuzima se: " + lineNumber);

                    org.jsoup.nodes.Document lineSchedulePage = Jsoup.connect(lineURL).get();

                    Elements odlasciRadniDan = lineSchedulePage.select("table.one-way-table").first().select("tbody").get(1).select("table.s-detail");
                    Log.i(LogTag, "-- odlasci radni dan --");

                    Elements odlasciVikend = lineSchedulePage.select("table.one-way-table > tbody > tr > td").get(1).select("table > tbody > tr").select("table.s-detail");
                    Log.i(LogTag, "-- odlasci vikend --");

                    Elements povratciRadniDan = lineSchedulePage.select("table.one-way-table").get(1).select("tbody").get(1).select("table.s-detail");
                    Log.i(LogTag, "-- povratci radni dan --");

                    Elements povratciVikend = lineSchedulePage.select("td > table.one-way-table > tbody > tr > td > table > tbody").last().select("tr").select("table.s-detail");
                    Log.i(LogTag, "-- povratci vikend --");

                    db = new DatabaseHandler(homeActivityContext);
                    List<Polazak> buses = db.getAllBuses();

                    for (Element selectedElement: odlasciRadniDan){

                        Log.i(LogTag, selectedElement.text());
                        db.addBus(new Polazak(selectedElement.text(), lineNumber, true, true, Polazak.Day.RADNIDAN));

                    }
                    for (Element selectedElement: odlasciVikend) {

                        Log.i(LogTag, selectedElement.text());
                        db.addBus(new Polazak(selectedElement.text(), lineNumber, true, true, Polazak.Day.VIKEND));
                    }
                    for (Element selectedElement: povratciRadniDan) {

                        Log.i(LogTag, selectedElement.text());
                        db.addBus(new Polazak(selectedElement.text(), lineNumber, true, false, Polazak.Day.RADNIDAN));
                    }
                    for (Element selectedElement: povratciVikend) {

                        Log.i(LogTag, selectedElement.text());
                        db.addBus(new Polazak(selectedElement.text(), lineNumber, true, false, Polazak.Day.VIKEND));

                    }


                }

                Elements prigradskiLineLinks = downloadedPage.select("div.schedule-list-wrap").select("ul.schedule-list.commuter-lines").select("a[href]");
                Log.i(LogTag, "-- PRIGRADSKE LINIJE --");
                for (Element link: prigradskiLineLinks)
                {
                    String lineURL = link.attr("abs:href");
                    String lineNumber = link.select("span.line-number").html();
                    String lineName = link.select("span.line-name").html();

                    if (!lineURL.contains("Redvoznje-1A")) // ignorišemo liniju 1/1A jer je već prikazana kod gradskih i nema isti raspored kao prigradske tabele
                    {
                        Log.i(LogTag, "Link: " + lineNumber + ": " + lineName + " | " + lineURL);

                        lineNames.put(lineNumber, lineName);

                        SetDownloadText("Preuzima se: " + lineNumber);

                        org.jsoup.nodes.Document lineSchedulePage = Jsoup.connect(lineURL).get();

                        Elements odlasciRadniDan = lineSchedulePage.select("table.one-way-table").first().select("tbody").get(1).select("table.s-detail");
                        Log.i(LogTag, "-- odlasci radni dan --");

                        Elements odlasciSubota = lineSchedulePage.select("table.one-way-table > tbody > tr > td").get(1).select("table.s-detail");
                        Log.i(LogTag, "-- odlasci subota --");

                        Elements odlasciNedelja = lineSchedulePage.select("table.one-way-table > tbody > tr > td").get(2).select("table.s-detail");
                        Log.i(LogTag, "-- odlasci nedelja --");

                        // --

                        Elements povratciRadniDan = lineSchedulePage.select("table.one-way-table").last().select("tbody > tr").get(1).select("td").first().select("table.s-detail");
                        Log.i(LogTag, "-- povratci radni dan --");

                        Elements povratciSubota = lineSchedulePage.select("table.one-way-table").last().select("tbody > tr").get(1).child(1).select("table.s-detail");
                        Log.i(LogTag, "-- povratci subota --");

                        Elements povratciNedelja = lineSchedulePage.select("table.one-way-table").last().select("tbody > tr").get(1).child(2).select("table.s-detail");
                        Log.i(LogTag, "-- povratci nedelja --");

                        for (Element selectedElement : odlasciRadniDan){
                            Log.i(LogTag, selectedElement.text());
                            db.addBus(new Polazak(selectedElement.text(), lineNumber, false, true, Polazak.Day.RADNIDAN));
                        }
                        for (Element selectedElement : odlasciSubota) {
                            Log.i(LogTag, selectedElement.text());
                            db.addBus(new Polazak(selectedElement.text(), lineNumber, false, true, Polazak.Day.SUBOTA));
                        }
                        for (Element selectedElement : odlasciNedelja) {
                            Log.i(LogTag, selectedElement.text());
                            db.addBus(new Polazak(selectedElement.text(), lineNumber, false, true, Polazak.Day.NEDELJA));
                        }

                        for (Element selectedElement : povratciRadniDan){
                            Log.i(LogTag, selectedElement.text());
                            db.addBus(new Polazak(selectedElement.text(), lineNumber, false, false, Polazak.Day.RADNIDAN));
                        }
                        for (Element selectedElement : povratciSubota) {
                            Log.i(LogTag, selectedElement.text());
                            db.addBus(new Polazak(selectedElement.text(), lineNumber, false, false, Polazak.Day.SUBOTA));
                        }
                        for (Element selectedElement : povratciNedelja) {
                            Log.i(LogTag, selectedElement.text());
                            db.addBus(new Polazak(selectedElement.text(), lineNumber, false, false, Polazak.Day.NEDELJA));
                        }
                    }
                }

                downloadFinishedCleanly = true;
            }
            catch (Exception e) {
                Log.e("PageDownloader", e.getMessage() + " | " +  e.toString() + " | " + e.getLocalizedMessage());

                if (true)
                {
                    // gracefully handle losing internet access

                    SetDownloadText("Internet prekinut, pokušajte ponovo");
                    downloadFinishedCleanly = false;
                }
            }

            return new org.jsoup.nodes.Document("");
        }

        protected void onProgressUpdate(Integer... progress) {
            // do nothing
        }

        protected void onPostExecute(org.jsoup.nodes.Document result) {

            // work with the downloaded page

            writeLineDetailsToStorage();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadText.animate().alpha(0f).setDuration(500).setListener(null);
                    downloadBar.animate().alpha(0f).setDuration(500).setListener(null);
                }
            });

            lineListDocument = downloadedPage;
            UpdateListView();
        }
    }

    public final static String LINE_NAME = "com.omegavesko.sutransplus.LINE";
    public final static String LINE_DETAILS = "com.omegavesko.sutransplus.LINE_DETAILS";

    private final String redVoznjeURL = "http://sutrans.rs/Redvoznje.html";
    private final String LogTag = "HomeActivity";

    private org.jsoup.nodes.Document lineListDocument;

    private ListView homeListView;
    private Button syncButton;
    private TextView appName;

    private ProgressBar downloadBar;
    private TextView downloadText;

    private Context homeActivityContext;

    private boolean downloadFinishedCleanly = false;

    private Map<String, String> lineNames = new HashMap<String, String>();

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

        setContentView(R.layout.activity_home);

        homeActivityContext = this;

        syncButton = (Button) findViewById(R.id.syncButton);
        appName = (TextView) findViewById(R.id.appName);
        homeListView = (ListView) findViewById(R.id.homeListView);

        downloadBar = (ProgressBar) findViewById(R.id.downloadBar);
        downloadText = (TextView) findViewById(R.id.downloadText);

        welcomeText = (TextView) findViewById(R.id.welcomeText);

        downloadText.setAlpha(0f);
        downloadBar.setAlpha(0f);
        welcomeText.setAlpha(0f);

        Typeface robotoLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
        appName.setTypeface(robotoLight);
        welcomeText.setTypeface(robotoLight);
        downloadText.setTypeface(robotoLight);

        lineNames = readLineDetailsFromStorage();

        try {
            DatabaseHandler db = new DatabaseHandler(this);

            List<Polazak> buses = db.getAllBuses();

//            Log.i(LogTag, "Database contents:");
//
//            for(Polazak pol: buses) {
//
//                Log.i(LogTag, pol.toString());
//
//            }

            if (buses.size() > 0) {
                // database is populated, show the ListView
                downloadFinishedCleanly = true;
                UpdateListView();
            }
            else
            {
                welcomeText.animate().alpha(1f).setDuration(1000).setListener(null);
            }

        } catch (Exception e) {Log.e("DatabaseHandler", "DB Error! " + e.getMessage()); }

        DatabaseHandler db = new DatabaseHandler(this);
        List<Polazak> allBuses = db.getAllBuses();

        if (allBuses.size() > 0) {
            // database is populated, show the ListView
            UpdateListView();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    public void Synchronize (View view)
    {
        // synchronize button pressed

        if (haveNetworkConnection()) {

            syncButton.setClickable(false);
            homeListView.setOnItemClickListener(null);

            if (welcomeText.getAlpha() > 0f) welcomeText.animate().alpha(0f).setDuration(750).setListener(null);

            syncButton.animate().alpha(0.2f).setDuration(500).setListener(null);

            homeListView.animate().alpha(0f).setDuration(500).setListener(null);
            homeListView.setVisibility(View.VISIBLE);

            downloadText.setText("");

            try {
                Log.i(LogTag, "Starting webpage download");

                new GetLinesTask().execute(redVoznjeURL);

                Log.i(LogTag, "Webpage downloaded, about to parse page");
            } catch (Exception e) {
                Log.e(LogTag, "Exception!" + e.toString());
            }
        } else {
            // TODO show popup dialog here

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(
                    "Niste povezani na Internet. Morate biti povezani na Internet (preko WiFi konekcije ili mobilne mreže) da biste preuzeli red vožnje. \n\n Ne treba vam Internet veza da biste pregledali red vožnje koji ste već preuzeli.");
            builder1.setCancelable(true);
            builder1.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();


            alert11.show();

            TextView messageText = (TextView) alert11.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER_HORIZONTAL);
            messageText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));
        }
    }

    public void UpdateListView()
    {

        DatabaseHandler db = new DatabaseHandler(this);
        List<Polazak> allBuses = db.getAllBuses();
        List<String> allLines = new ArrayList<String>();

        for(Polazak bus: allBuses)
        {
            if (!allLines.contains(bus.linija))
            {
                allLines.add(bus.linija);
            }
        }

        Log.i(LogTag, "Webpage parse complete.");

        RelativeLayout activityLayout = (RelativeLayout) findViewById(R.id.homeActivityLayout);

        HomeActivityListAdapter homeListCustomAdapter = new HomeActivityListAdapter(this, allBuses.toArray(new Polazak[allBuses.size()]), lineNames);
        homeListView.setAdapter(homeListCustomAdapter);

//        ArrayAdapter<String> homeListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allLines);
//        homeListView.setAdapter(homeListAdapter);

        syncButton.animate().alpha(1f).setDuration(1000).setListener(null);

        syncButton.setClickable(true);

        if (downloadFinishedCleanly) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    homeListView.animate().alpha(1f).setDuration(1000).setListener(null);
                }
            }, 500);

            homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//
                    if (view.findViewById(R.id.mainTextView) != null)
                    {
                        TextView textView = (TextView) view.findViewById(R.id.mainTextView);
                        String lineNumber = (String) textView.getText();

                        OpenLineActivity(lineNumber);
                    }
                }

            });
        }
    }

    public void OpenLineActivity(String lineName)
    {
        Intent intent = new Intent(this, LineActivity.class);
        intent.putExtra(LINE_NAME, lineName);
        intent.putExtra(LINE_DETAILS, lineNames.get(lineName));
        startActivity(intent);

    }

    void SetDownloadText(final String lineName)
    {
        final int animationDuration = 100; // in ms

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                downloadText.animate().alpha(0f).setDuration(animationDuration).setListener(null);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadText.setText(lineName);
                        downloadText.animate().alpha(1f).setDuration(100).setListener(null);
                    }
                }, animationDuration);
            }
        });

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    void writeLineDetailsToStorage()
    {
        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(lineNames);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) { Log.e(LogTag, e.toString()); }
    }

    Map<String, String> readLineDetailsFromStorage()
    {
        Map<String, String> returnMap = new HashMap<String, String>();
        Object readObject = null;
        try{
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            readObject = inputStream.readObject();
            inputStream.close();

            returnMap = (Map<String, String>) readObject;

        } catch(Exception e) { Log.e(LogTag, e.toString()); }

        return returnMap;
    }
}
