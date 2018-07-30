package com.example.gonza.trabajofinal2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.gonza.trabajofinal2.Objetos.Cliente;
import com.example.gonza.trabajofinal2.Objetos.Coche;
import com.example.gonza.trabajofinal2.Objetos.FirebaseReferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class NuevoCoche extends Fragment{
    private static final String TAG = "FragmentNewCar";
    private static final int MY_PERMISSIONS_REQUEST_CAMARA = 0;
    private static final java.lang.String TEXTO_ESTADO = "estado";
    private static final java.lang.String TEXTO_MARCA = "marca";
    boolean cochePublicado = false;
    Button botonMarca, botonEstado, botonPublicar;
    FloatingActionButton botonFoto;
    TextView marcaSeleccionada, estadoCoche;
    ImageView imagen;
    EditText modelo;

    String marca, estado,modeloCoche, imagenURL;
    String [] marcas;
    String [] estados;
    String nombreUser, emailUser;
    Uri tempUri;


    private ProgressDialog progress;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View rootView = inflater.inflate(R.layout.nuevocoche, container, false);
            marcas= getResources().getStringArray(R.array.marcas);
            estados = getResources().getStringArray(R.array.estados);

            botonMarca = rootView.findViewById(R.id.botonmarca);
            botonEstado = rootView.findViewById(R.id.botonestado);
            botonFoto = rootView.findViewById(R.id.botonfoto);
            botonPublicar = rootView.findViewById(R.id.botonpublicar);


            marcaSeleccionada = rootView.findViewById(R.id.marcaseleccionada);
            estadoCoche = rootView.findViewById(R.id.estadoselect);
            modelo = rootView.findViewById(R.id.modelocoche);
            imagen = rootView.findViewById(R.id.imagenCochecito);

            if (savedInstanceState != null) {
                this.estado = savedInstanceState.getString(TEXTO_ESTADO, "");
                this.marca = savedInstanceState.getString(TEXTO_MARCA, "");
            }

            marcaSeleccionada.setText(marca);
            estadoCoche.setText(estado);


            botonMarca.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Selecciona marca:")

                            .setItems(R.array.marcas, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    marca = marcas[i];
                                    marcaSeleccionada.setText(marca);
                                    Log.i(TAG, "onClick: "+ marcaSeleccionada.getText());

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.getWindow().setLayout(500,600);
                }

            });

            botonEstado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Selecciona estado:")

                            .setItems(R.array.estados, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    estado=estados[i];
                                    estadoCoche.setText(estado);
                                    Log.i(TAG, "onClick: "+ estadoCoche.getText());

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.getWindow().setLayout(500,600);
                }
            });

            botonFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {// Marshmallow+
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

                            } else {
                                // No se necesita dar una explicación al usuario, sólo pedimos el permiso.
                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMARA );
                                // MY_PERMISSIONS_REQUEST_CAMARA es una constante definida en la app. El método callback obtiene el resultado de la petición.
                            }
                        }else{ //have permissions
                            abrirCamara ();
                        }
                    }else{ // Pre-Marshmallow
                        abrirCamara ();
                    }

                }
            });
            
            botonPublicar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!validateForm()) {
                        return;
                    }



                    progress=new ProgressDialog(getActivity());
                    progress.setMessage("Publicando coche....");
                    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    // progress.setIndeterminate(true);
                    progress.setProgress(0);
                    progress.show();

                    final int totalProgressTime = 100;
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            int jumpTime = 0;
                            subirCoche();
                            while(cochePublicado && jumpTime < totalProgressTime) {
                                try {
                                    jumpTime += 5;
                                    progress.setProgress(jumpTime);
                                    sleep(200);
                                }
                                catch (InterruptedException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }

                            try {
                                sleep(100);
                                progress.dismiss();
                                Intent principal = new Intent(getActivity(), Principal.class);
                                startActivity(principal);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    t.start();



                }
            });




            return rootView;
        }

    private boolean validateForm() {
        boolean ok = true;
        if (tempUri==null){
            Toast.makeText(getActivity(), "debes insertar una foto" , Toast.LENGTH_LONG).show();
            ok=false;
        }
        return ok;

    }

    private void subirCoche() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Log.i(TAG, "paso 1 ");

        StorageReference riversRef = storageRef.child(tempUri.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(tempUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.i(TAG, "paso 2 ");
                Log.i(TAG, "onSuccess: foto subida! " +downloadUrl );
                imagenURL = String.valueOf(taskSnapshot.getDownloadUrl());


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                // DatabaseReference myRefCoche = database.getReference(FirebaseReferences.PRUEBA_REFERENCE).child(FirebaseReferences.COCHE_REFERENCE);
                DatabaseReference myRefCliente = database.getReference(FirebaseReferences.PRUEBA_REFERENCE).child(FirebaseReferences.CLIENTE_REFERENCE);
                Log.i(TAG, "paso 3 ");
                myRefCliente.orderByChild("email").equalTo(user.getEmail()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.i(TAG, "onChildAdded: "+ dataSnapshot.toString() );
                        Cliente cliente = dataSnapshot.getValue(Cliente.class);
                        Log.i(TAG, "onChildAdded: "+ cliente.getNombre());
                        nombreUser = cliente.getNombre();
                        emailUser = cliente.getEmail();
                        modeloCoche = modelo.getText().toString();
                        Log.i(TAG, "paso 4 ");

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRefCoche = database.getReference(FirebaseReferences.PRUEBA_REFERENCE);
                        Coche coche = new Coche(marca, nombreUser, emailUser, estado,modeloCoche , imagenURL);
                        myRefCoche.child(FirebaseReferences.COCHE_REFERENCE).push().setValue(coche);
                        Log.i(TAG, "onClick: coche creado!!!!!!!");

                        cochePublicado = true;
                        Toast toast =  Toast.makeText(getActivity(), "Coche subido" , Toast.LENGTH_LONG);

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
        });
        cochePublicado = true;


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMARA : {
                // Si la petición es cancelada, el array resultante estará vacío.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // El permiso ha sido concedido.
                    abrirCamara ();
                } else {
                    // Permiso denegado, deshabilita la funcionalidad que depende de este permiso.
                }
                return;
            }
            // otros bloques de 'case' para controlar otros permisos de la aplicación
        }
    }

    private void abrirCamara() {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i,MY_PERMISSIONS_REQUEST_CAMARA);
        //ocultar();

        
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMARA) {
            if (data == null){
                Toast.makeText(getActivity(), "no ha realizado fotografia", Toast.LENGTH_LONG);
            }else{
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imagen.setImageBitmap(photo);

                tempUri = getImageUri(getActivity(), photo);
                Log.i(TAG, "onActivityResult: " + tempUri);
            }





        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXTO_MARCA, marca);
        outState.putString(TEXTO_ESTADO, estado);


    }


}
