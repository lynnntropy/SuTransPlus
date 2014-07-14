package com.omegavesko.sutransplus;

/**
 * Created by omega_000 on 7/10/2014.
 */
public class Polazak
{
    static enum Day
    {
        RADNIDAN,
        VIKEND,
        SUBOTA,
        NEDELJA
    }

    int id;
    String linija;

    public String busInfo;
    public boolean odlazak;
    public boolean gradski;
    public Day day;


    Polazak() {}

    Polazak (String info, String line, boolean gradski, boolean odlazak, Day day)
    {
        this.busInfo = info;
        this.linija = line;
        this.gradski = gradski;
        this.odlazak = odlazak;
        this.day = day;
    }

    public String toString()
    {
        String returnString;

        if (odlazak) returnString = "Odlazak u ";
        else returnString = "Povratak u ";

        returnString += busInfo;
        returnString += " | Gradski: " + gradski;
        returnString+= " | Linija: " + linija;

        return returnString;
    }

    public int getID() { return this.id; }
    public void setID(int id) { this.id = id; }
}
