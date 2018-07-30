package com.example.gonza.trabajofinal2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gonza.trabajofinal2.Objetos.Cliente;
import com.example.gonza.trabajofinal2.Objetos.Coche;
import com.example.gonza.trabajofinal2.Objetos.FirebaseReferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistroActivity extends AppCompatActivity {
    private static final String TEXTO_INTRODUCIDO = "texto";
    EditText nUsuario, aUsuario, mailUsuario, passUsuario;
    Button registrar, verificado;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseUser user;
    String TAG="ENREGISTRO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        nUsuario = (EditText) findViewById(R.id.textNombre);
        aUsuario = (EditText) findViewById(R.id.textApellido);
        mailUsuario = (EditText) findViewById(R.id.textEmail);
        passUsuario = (EditText) findViewById(R.id.textContra);
        registrar = (Button) findViewById(R.id.botonRegistro);





        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseReferences.PRUEBA_REFERENCE);
        myRef.child(FirebaseReferences.CLIENTE_REFERENCE).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Coche coche = dataSnapshot.getValue(Coche.class);
                // Log.i(TAG, coche.getPropietario());
                Log.i(TAG, dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

    }

    public void registroUsuario(View view) {
        String nombre = nUsuario.getText().toString();
        String apellido = aUsuario.getText().toString();

        String email = mailUsuario.getText().toString();
        String pass = passUsuario.getText().toString();

        Log.i(TAG, "registroUsuario: "
        +email);

        createAccount(email, pass,nombre, apellido);


    }

    private void createAccount(final String email, final String pass, final String nombre, final String apellido) {

        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(FirebaseReferences.PRUEBA_REFERENCE);
                            Cliente cliente = new Cliente(nombre, apellido, email);
                            myRef.child(FirebaseReferences.CLIENTE_REFERENCE).push().setValue(cliente);
                            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent principal = new Intent(getApplicationContext(), Principal.class);
                                        startActivity(principal);


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(RegistroActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }

                                    // [START_EXCLUDE]

                                    // [END_EXCLUDE]
                                }
                            });




                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistroActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }
    private boolean validateForm() {
        boolean valid = true;

        String nombreUSer = nUsuario.getText().toString();
        if (TextUtils.isEmpty(nombreUSer)) {
            nUsuario.setError("Required.");
            valid = false;
        } else {
            nUsuario.setError(null);
        }

        String password = passUsuario.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passUsuario.setError("Required.");
            valid = false;
        } else {
            passUsuario.setError(null);
        }


        String emailUser = mailUsuario.getText().toString();
        if (TextUtils.isEmpty(emailUser)) {
            mailUsuario.setError("Required.");
            valid = false;
        } else {
            mailUsuario.setError(null);
        }

        String apellidoUser = aUsuario.getText().toString();
        if (TextUtils.isEmpty(apellidoUser)) {
            aUsuario.setError("Required.");
            valid = false;
        } else {
            aUsuario.setError(null);
        }

        return valid;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String nombre = nUsuario.getText().toString();
        String apellido = aUsuario.getText().toString();

        String email = mailUsuario.getText().toString();
        String pass = passUsuario.getText().toString();

        outState.putString(TEXTO_INTRODUCIDO,nombre);
        outState.putString(TEXTO_INTRODUCIDO,apellido);
        outState.putString(TEXTO_INTRODUCIDO,email);
        outState.putString(TEXTO_INTRODUCIDO,pass);
    }
}
