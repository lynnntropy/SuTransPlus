package com.omegavesko.sutransplus;

import java.util.List;

/**
 * Created by omega_000 on 7/10/2014.
 */

public class Linija
{
    public String imeLinije;

    public List<Polazak> radniDan;
    public List<Polazak> vikend;

    public Linija(String ime, List<Polazak> radniDan, List<Polazak> vikend)
    {
        this.imeLinije = ime;

        this.radniDan = radniDan;
        this.vikend = vikend;
    }
}
