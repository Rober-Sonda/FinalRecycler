package com.rober.finalrecycler;

import android.util.Log;

public class Habitacion {

    int ID;
    int Lights;
    int Shaders;
    int Items;
    int Ir_devs;
    int Temp;
    int Bright;
    int Humidity;
    int Watts;
    int Precence;
    int Offline;
    int Chairs;

    public Habitacion(int[] varargs) {
        ID = varargs[0];
        Lights = varargs[1];
        Shaders = varargs[2];
        Items = varargs[3];
        Ir_devs = varargs[4];
        Temp = varargs[5];
        Bright = varargs[6];
        Humidity = varargs[7];
        Watts = varargs[8];
        Precence = varargs[9];
        Offline = varargs[10];
        Chairs = varargs[11];
    }

    public void mostrarDatos(){
        Log.i("mostrarDatos",
                "\n"+" ID-> " + ID
            +" Lights-> " + Lights
            +" Shaders-> " + Shaders
            +" Items-> " + Items
            +" Ir_devs-> " + Ir_devs
            +" Temp-> "  + Temp
            +" Bright-> " + Bright
            +" Humidity-> " + Humidity
            +" Watts-> " + Watts
            +" Precence-> " + Precence
            +" Offline-> " + Offline
            +" Chairs-> " + Chairs+"\n"
            +"¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
    }
}

