package com.rober.finalrecycler;

class InfoHabitaciones implements onTick{

    @Override
    public void tarea (MainActivity _main) {
        _main.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    _main.cliente.Sendroomstatus();  // LinearLayoutManager is used here, this will layout the elements in a similar fashion to the way ListView would layout elements. The RecyclerView.LayoutManager defines how elements are laid out.
                    _main.mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }   // BEGIN_INCLUDE(initializeRecyclerView)
        });
    }
}
