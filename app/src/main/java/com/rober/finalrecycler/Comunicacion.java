package com.rober.finalrecycler;

import android.os.Build;
import android.os.Trace;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.rober.finalrecycler.GlobalInfo.ListaHabitaciones;
import static com.rober.finalrecycler.GlobalInfo.ListaLuces;

public class Comunicacion extends Thread {

    DatagramSocket SocketCliente;
    private MainActivity _main;
    public Comunicacion(MainActivity main){
        _main = main;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    SocketCliente = new DatagramSocket(GlobalInfo.PORT);
                    SocketCliente.setBroadcast(true);
                    while (true){
                        byte[] Trama = new byte[1500];
                        //preparo un paquete para recibir la respuesta del servidor
                        DatagramPacket reciboDatos = new DatagramPacket(Trama, 0, Trama.length);;
                        SocketCliente.receive(reciboDatos);
                        String a = GlobalInfo.getIP();
                        String b = reciboDatos.getAddress().getHostName();
                        if (!a.equals(b) && !b.equals("192.168.88.87")){ //quiere decir que viene del servidor
                            String msg = PrimerParteTrama(reciboDatos.getData(), ' ');
                            comprobarTramas(msg,reciboDatos);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //Convierte un Array de tipo int a tipo String
    public int[] convertarraysIntStr(String[] array){
        int i = 0;
        int [] arrayint = new int[array.length];
        while(i < array.length) {
            try {
                arrayint[i]=Integer.parseInt(array[i]);
            }catch (Exception e){
                e.getMessage();
            }
            i++;
        }
        return arrayint;
    }
    //descubrir el servidor sophia
    public void Discover() {
        try{
            String strtrama = "DISCOVER ";
            SendTrama(strtrama);
            Log.i("DISCOVER",strtrama);
        } catch (Exception e){
            e.getMessage();
        }
    }
    //Envia la trama Discover a toda la red
    public void SendTrama(String strtrama){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DatagramPacket Paquete = null;
                    byte [] Datos = strtrama.getBytes();
                    Paquete = new DatagramPacket(Datos, Datos.length, InetAddress.getByName("255.255.255.255"), GlobalInfo.PORT);
                    SocketCliente.send(Paquete);
                } catch (Exception e){
                    e.getMessage();
                }
            }
        }).start();

    }
    // muestra en pantalla el texto recibido
    public void addItemsInRecycler(byte[] buffer, int punteroPpal) throws IOException {
        String message = "";
        message += new String(buffer).substring(0, punteroPpal);

        TextView TxtMensaje = new TextView(_main);
        String finalMessage = message;
        _main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TxtMensaje.setText("Repuesta: " + " "+ finalMessage +"\r\n");
                TxtMensaje.setTextSize(15);
                TxtMensaje.setGravity(Gravity.LEFT);
//                RecyclerView chat = _main.findViewById(R.id.RecyclerPanel);
//                chat.addItemDecoration();
            }
        });
    }
    //Envia la trama Login al servidor
    public void SendLogin(){
        String user = "Roberto";
        String mipassword = "123456", miHashPass = GlobalInfo.sha1Hash(mipassword);
        String login2 = String.format("APP_LOGIN2 USER=%s PASS=%s PASS_SHA1=SHA1(%s)", user, mipassword, miHashPass);
        SendTrama(login2);
    }
    //Envia la trama Status al servidor
    public void Sendroomstatus(){
        String room_status = "ROOM_STATUS ";
        SendTrama(room_status);
    }
    //Envia la trama Status al servidor
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void SendTramaLights(int posHabitacion){
        if (ListaHabitaciones.size() > 0){
            if(ListaHabitaciones.get(posHabitacion).Lights > 0){
                ListaHabitaciones.get(posHabitacion).Lights = 0;
            }else{
                ListaHabitaciones.get(posHabitacion).Lights = 100;
            }
            int vaLights = ListaHabitaciones.get(posHabitacion).Lights, ramp = 2;
            String lights_set2 = String.format("LIGTHS_SET UID=385 ID=%d VALUE=%d RAMP=%d TIMER_OFF=0 COLOR=-168778", ListaHabitaciones.get(posHabitacion).ID, vaLights, ramp);
            SendTrama(lights_set2);
        }
    }
    //Envia la trama roomitems al servidor
    public void SendPeticionRoomItems(){
        String room_items = "ROOMITEMS_STATUS ID=212";
        SendTrama(room_items);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void UpDownoneLight(){
        if (ListaLuces != null){
            ListaLuces.forEach(Luz -> {
                if(Luz.ID==140) {
                    if (Luz.Valor > 0) {
                        Luz.Valor = 0;
                        String LightsVal = "APP_CMD UID=385 ID=143 VALUE=0 RAMP=4 TIMER_OFF=0 COLOR=-168778";
                        SendTrama(LightsVal);
                    } else {
                        Luz.Valor = 100;
                        String LightsVal = "APP_CMD UID=385 ID=143 VALUE=100 RAMP=4 TIMER_OFF=0 COLOR=-168778";
                        SendTrama(LightsVal);
                    }
                }
            });
        }
    }
    //corta el encabezado de la trama basandose en el byte 0
    public String PrimerParteTrama(byte[] buffer2, byte hasta){
        String encabezado= "";
        int i = 0;
        while (buffer2[i] != hasta){
            encabezado += (char)(buffer2[i]);
            i++;
        }
        return encabezado;
    }
    //corta el encabezado de la trama basandose en el char ' '
    public String PrimerParteTrama(byte[] buffer2, char hasta){
        String encabezado= "";
        int i = 0;
        while (buffer2[i] != hasta){
            encabezado += (char)(buffer2[i]);
            i++;
        }
        return encabezado;
    }
    //Comprueba las tramas que envia el servido y actua en consecuencia
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void comprobarTramas(String msg, DatagramPacket reciboDatos){
        try{
            Log.i("MSG",msg);
            if (msg.startsWith("APP_BRIDGE")){
//                System.out.println("APP_BRIDGE");
                SendLogin();
            }
            if (msg.startsWith("LOGIN2_REPLY")) {
//                System.out.println("LOGIN2_REPLY");
                Sendroomstatus();
            }
            if (msg.startsWith("ROOM_R_STATUS")) {
//                System.out.println("ROOMITEMS_STATUS ");
                String[] strArray = Datos(reciboDatos);
                HabitacionItemList(strArray);
//                ListaHabitaciones.forEach(habitacion -> {
//                    habitacion.mostrarDatos();
//                });
            }
            if (msg.startsWith("LIGTHS_SET ")) {
//                System.out.println("LIGTHS_SET");
            }
            if (msg.startsWith("ROOMITEMS_R_STATUS")) {
                String[] strarray = Datos(reciboDatos);
                if (ListaLuces.size() > 0){
                    ListaLuces.clear();
                }
                LightsItemList(strarray);
                ListaLuces.forEach(Luz -> {
                    Luz.mostrarLuces();
                });
            }

        }catch (Exception e){
            e.getMessage();
        }
    }
    //devolvemos un String[] de la trama recibida sin el encabezado
    public String[] Datos (DatagramPacket reciboDatos){
        String Info_Status = new String(reciboDatos.getData()).substring(0, reciboDatos.getLength());
        //seleccionamos el encabezado
        String Encabezado = PrimerParteTrama(Info_Status.getBytes(), (byte) 0);
        int posDatos = Encabezado.length() + 1;
        //obtenemos los datos sin encabezado
        Info_Status = new String(reciboDatos.getData()).substring(posDatos, reciboDatos.getLength());
        //pasamos el string a un array
        String[] strArray = Info_Status.split("\\r\\n");
        return strArray;
    }
    //creo una habitacion a partir de un String[] lo convierto a int[] y creo el nuevo objeto
    public void HabitacionItemList(String[] strArray) {
        int j = 0;
        String[] arrayFinal;
        Habitacion habitacion = null;
        try {
            while (j < strArray.length) {
                arrayFinal = strArray[j].split(";"); //paso un array
                int[] arrayFint = convertarraysIntStr(arrayFinal); //lo convierto a tipo integer

                if(encontrarItem(arrayFint[0]) == true) {
                    ListaHabitaciones.get(j).Lights = arrayFint[1];
                    ListaHabitaciones.get(j).Shaders = arrayFint[2];
                    ListaHabitaciones.get(j).Temp = arrayFint[5];
                } else {
                    habitacion = new Habitacion(arrayFint);
                    ListaHabitaciones.add(habitacion);
                }
                j++;
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    public boolean encontrarItem(int encontrarID){
        int i = 0;
        boolean encontrado = false;
        while (i < ListaHabitaciones.size()){
            if(ListaHabitaciones.get(i).ID == encontrarID){
                encontrado = true;
                return encontrado;
            }
            i++;
        }
        return encontrado;
    }
    //creo una luz a partir de un String[] lo convierto a int[] y creo el nuevo objeto
    public void LightsItemList(String[] strArray) {
        int j = 0;
        String[] arrayfinal;
        Items luz = null;
        try {
            while (j < strArray.length) {
                arrayfinal = strArray[j].split(";"); //paso un array
                int[] arrayfint = convertarraysIntStr(arrayfinal); //lo convierto a tipo integer
                luz = new Items(arrayfint);
                ListaLuces.add(luz);
                j++;
            }
        }catch (Exception e){
            e.getMessage();
        }
    }
}
