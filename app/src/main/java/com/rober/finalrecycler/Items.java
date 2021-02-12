package com.rober.finalrecycler;

import android.util.Log;

public class Items {
    int ID;
    int Valor;
    int Color;

    public Items(int[] varargs) {
        ID = varargs[0];
        Valor = varargs[1];
        Color = varargs[2];
    }

    public void mostrarLuces(){
        Log.i("mostrarDatos",
        "\n"+" ID-> " + ID
        +" Lights-> " + Valor
        +" Shaders-> " + Color +"\n"
        +"------------------------------------------------");
    }
}
