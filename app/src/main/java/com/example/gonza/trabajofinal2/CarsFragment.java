package com.example.gonza.trabajofinal2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.gonza.trabajofinal2.Adapter;
import com.example.gonza.trabajofinal2.Objetos.Coche;
import com.example.gonza.trabajofinal2.Objetos.FirebaseReferences;
import com.example.gonza.trabajofinal2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CarsFragment extends Fragment {

    String[] cars;

    RecyclerView rv;

    List<Coche> coches;
    Adapter adapter;
    protected RecyclerView.LayoutManager mLayoutManager;


    public CarsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_cars, container, false);

        rv = rootView.findViewById(R.id.recycler1);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        coches = new ArrayList<>();

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        adapter = new Adapter(coches, getActivity());
        rv.setAdapter(adapter);


        final GestureDetector mGestureDetector = new GestureDetector(CarsFragment.super.getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {


                        int position = recyclerView.getChildAdapterPosition(child);

                        recyclerView.getAdapter();
                        Log.i("CocheSeleccionado", "Has Seleccionado el coche nº: " + coches.get(position).getMarca());

                        coches.get(position).getMarca();

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Informacion del coche:")
                                .setMessage("Marca: " + coches.get(position).getMarca()
                                        + "\n" + "Modelo: " + coches.get(position).getModelo()
                                        + "\n" + "Estado: " + coches.get(position).getEstado()
                                        + "\n" + ("propietario: " + coches.get(position).getPropietario()
                                        + "\n" + "contacto: " + coches.get(position).getEmail()));

                        AlertDialog alert = builder.create();
                        alert.show();
                        alert.getWindow().setLayout(500, 600);

                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
        });


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.PRUEBA_REFERENCE);
        myRef.child(FirebaseReferences.COCHE_REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coches.removeAll(coches);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.i("LISTA", "ENTRO ");
                    Coche coche = snapshot.getValue(Coche.class);
                    coches.add(coche);
                    Log.i("LISTA", "COCHE AÑADIDO " + coche.getMarca() + coche.getEstado());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }
}
