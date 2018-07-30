package com.example.gonza.trabajofinal2;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gonza.trabajofinal2.Objetos.Cliente;
import com.example.gonza.trabajofinal2.Objetos.FirebaseReferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECCION_FRAGMENT ="numero" ;
    NavigationView mNavigation;
    TextView textoCabezera, textSub;
    FirebaseUser user;
    private String TAG = "PRINCIPAL";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    String nombreUser, emailUser;
    private View mHeaderView;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            this.id = savedInstanceState.getInt(String.valueOf(SELECCION_FRAGMENT), 0);
        }
        Log.i(TAG, "onCreate: " + id);
        switch (id){

            case R.id.nav_coches:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new CarsFragment()).commit();
                break;
            case R.id.nav_vendecoche:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new NuevoCoche()).commit();
                break;
            case R.id.cierroSesion:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new CarsFragment()).commit();
                break;



        }






        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        mHeaderView = mNavigation.getHeaderView(0);
        textoCabezera = (TextView) mHeaderView.findViewById(R.id.cabezeraMenu);
        textSub = mHeaderView.findViewById(R.id.textoEmail);

        user = FirebaseAuth.getInstance().getCurrentUser();



       // Log.i(TAG, "onCreate: " + user.getEmail());
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(FirebaseReferences.PRUEBA_REFERENCE);
        myRef.child(FirebaseReferences.CLIENTE_REFERENCE).orderByChild("email").equalTo(user.getEmail()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG, "onChildAdded: "+ dataSnapshot.toString() );
                Cliente cliente = dataSnapshot.getValue(Cliente.class);
                Log.i(TAG, "onChildAdded: "+ cliente.getNombre());
                nombreUser = cliente.getNombre();
                emailUser = cliente.getEmail();


                textoCabezera.setText(nombreUser);
                textSub.setText(emailUser);




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECCION_FRAGMENT, id);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.i(TAG, "onNavigationItemSelected: "+ id);
        switch (id){
            case R.id.nav_coches:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new CarsFragment()).commit();
                break;
            case R.id.nav_vendecoche:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new NuevoCoche()).commit();
                break;
            case R.id.cierroSesion:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.contenedor, new CarsFragment()).commit();
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;



    }
}
