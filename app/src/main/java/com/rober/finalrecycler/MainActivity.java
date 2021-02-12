package com.rober.finalrecycler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//556aa3c26e65dafa53b242159f77b0108214f123
public class MainActivity extends AppCompatActivity implements RvOnItemClickListener {
    Comunicacion cliente = null;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    CustomAdapter mAdapter;
    onTick Tick = null;
    onTick doTask = new InfoHabitaciones();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            cliente = new Comunicacion(MainActivity.this);
            cliente.run();
            cliente.Discover();
            setContentView(R.layout.activity_main);
            mRecyclerView = findViewById(R.id.RecyclerRooms_id);
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new CustomAdapter(GlobalInfo.ListaHabitaciones,this);
            mRecyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // opciones disponibles solo para el cliente excepto la opcion salir
        switch (item.getItemId()) {
            case R.id.mnconsultar_id:
                if (doTask != null) {
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            doTask.tarea(MainActivity.this);
                        }
                    };
                    timer.schedule(timerTask, 0, 1000);
                }
                return true;
            case R.id.mnencenderluces_id:
                if (getText(item.getItemId()).equals("Encender Luces")) {
                    //cambiar el texto a Apagar Luces
                    //sino
                    //cambiarlo tambien a Encender Luces
                }
                return true;
            case R.id.mnsalir_id:
                dialogSalir();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(int position) {
        cliente.SendTramaLights(position);
    }


    /*
    @Override
    public void tarea(View v) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    cliente.Sendroomstatus();  // LinearLayoutManager is used here, this will layout the elements in a similar fashion to the way ListView would layout elements. The RecyclerView.LayoutManager defines how elements are laid out.
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }   // BEGIN_INCLUDE(initializeRecyclerView)
        });
    }*/



    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private List<Habitacion> localDataSet = null;
        private RvOnItemClickListener Deleg = null;
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtNombreHabitacion,
                             txtValorLuces,
                             txtCortinas,
                             txtTemperatura;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                txtNombreHabitacion = (TextView) view.findViewById(R.id.txtnombhab_id);
                txtValorLuces = (TextView) view.findViewById(R.id.txtvalluces_id);
                txtCortinas = (TextView) view.findViewById(R.id.txtcortinas_id);
                txtTemperatura = (TextView) view.findViewById(R.id.txttemp_id);
                view.setClickable(true);
                view.setFocusable(true);
            }
        }
        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet List<Habitacion> containing the data to populate views to be used
         * by RecyclerView.
         */

        public CustomAdapter(List<Habitacion> dataSet, RvOnItemClickListener deleg) {
            localDataSet = dataSet;
            Deleg = deleg;
        }

        // Create new views (invoked by the layout manager)

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_habitacion, viewGroup, false);
//            view.setOnClickListener(this); //pongo mi listener a escuchar
            return new ViewHolder(view);
        }
        // Replace the contents of a view (invoked by the layout manager)

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.txtNombreHabitacion.setText(String.valueOf(localDataSet.get(position).ID));
            viewHolder.txtValorLuces.setText(String.valueOf(localDataSet.get(position).Lights));
            viewHolder.txtCortinas.setText(String.valueOf(localDataSet.get(position).Shaders));
            viewHolder.txtTemperatura.setText(String.valueOf(localDataSet.get(position).Temp));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    //llamar a mi onClick implementado en el main
                    if(Deleg!=null){
                        Deleg.onClick(position);
                    }
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }
    void dialogSalir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea salir de la apliación?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                finish();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}


