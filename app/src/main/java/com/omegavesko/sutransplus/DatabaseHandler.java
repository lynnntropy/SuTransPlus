package com.omegavesko.sutransplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omega_000 on 7/11/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "lineManager";

    // Contacts table name
    private static final String TABLE_BUSES = "buses";

    private static final String KEY_ID = "id";
    private static final String KEY_LINE = "line";
    private static final String KEY_INFO = "info";
    private static final String KEY_ODLAZAK = "odlazak";
    private static final String KEY_GRADSKI = "gradski";
    private static final String KEY_DAY = "day";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_BUSES_TABLE = "CREATE TABLE " + TABLE_BUSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LINE + " TEXT,"
                + KEY_INFO + " TEXT,"
                + KEY_ODLAZAK + " INTEGER, "
                + KEY_GRADSKI + " INTEGER, "
                + KEY_DAY + " TEXT" + ")";

        db.execSQL(CREATE_BUSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSES);
        onCreate(db);
    }

    public void addBus(Polazak bus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LINE, bus.linija);
        values.put(KEY_INFO, bus.busInfo);

        int odlazakValue;
        if (bus.odlazak) odlazakValue = 1;
        else odlazakValue = 0;

        values.put(KEY_ODLAZAK, odlazakValue);

        int gradskiValue;
        if (bus.gradski) gradskiValue = 1;
        else gradskiValue = 0;

        values.put(KEY_GRADSKI, gradskiValue);
        values.put(KEY_DAY, bus.day.name());

        db.insert(TABLE_BUSES, null, values);
        db.close();
    }

    public Polazak getBus(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BUSES, new String[] { KEY_ID, KEY_LINE, KEY_INFO, KEY_ODLAZAK, KEY_GRADSKI, KEY_DAY}, KEY_ID + "=?", new String[] {String.valueOf(id) }, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        boolean odlazak;
        try {

            if (cursor.getInt(3) == 1) odlazak = true;
            else odlazak = false;
        } catch(Exception e) { odlazak = false; }

        boolean gradski;
        try {

            if (cursor.getInt(4) == 1) gradski = true;
            else gradski = false;
        } catch(Exception e) { gradski = true; }



//        Polazak bus = new Polazak(Integer.valueOf(cursor.getString(2)), Integer.valueOf(cursor.getString(3)), odlazak, cursor.getString(1), gradski);
        Polazak bus = new Polazak(cursor.getString(2), cursor.getString(1), gradski, odlazak, Polazak.Day.valueOf(cursor.getString(5)));
        return bus;


    }

    public List<Polazak> getAllBuses() {

        List<Polazak> busList = new ArrayList<Polazak>();

        String selectQuery = "SELECT  * FROM " + TABLE_BUSES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                Polazak bus = new Polazak();
                bus.id = Integer.parseInt(cursor.getString(0));
                bus.linija = cursor.getString(1);
                bus.busInfo = cursor.getString(2);

                boolean odlazak;
                if (cursor.getInt(3) == 1) odlazak = true;
                else odlazak = false;
                bus.odlazak = odlazak;

                boolean gradski;
                if (cursor.getInt(4) == 1) gradski = true;
                else gradski = false;
                bus.gradski = gradski;

                bus.day = Polazak.Day.valueOf(cursor.getString(5));

                busList.add(bus);

            } while (cursor.moveToNext());
        }

        return busList;

    }

    public int getBusCount() {
        String countQuery = "SELECT  * FROM " + TABLE_BUSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void ClearTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUSES, null, null);
    }

//    public int updateBus(Polazak bus) {}
//    public void deleteBus(Polazak bus) {}

}
