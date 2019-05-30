package com.example.firebasesstorage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private Button btn_subir;
    private static final int GALLERY_INTENT = 1;
    private ProgressDialog progressDialog;
    private ImageView imageView_foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        btn_subir = findViewById(R.id.button_subir);
        progressDialog = new ProgressDialog(this);
        imageView_foto = findViewById(R.id.imageView_foto);

        /*Glide.with(MainActivity.this)
                .load("https://firebasestorage.googleapis.com/v0/b/fir-sstorage-ca3d8.appspot.com/o/fotos%2F1509157613?alt=media&token=cd698537-92ea-45b4-9e6d-aa9aa2e6257c")
                .fitCenter()
                .into(imageView_foto);//.centerCrop()
        */

    }
    public void subirImg(View v)
    {
        //para llamar una imagen de la galeria
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_INTENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressDialog.setTitle("Cargando");
        progressDialog.setMessage("cargando imagen del sorage ");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //para no crashear si toma foto o selecciona imagen
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {   //de parte de firebases
            Uri uri = data.getData();
            final StorageReference filepath = mStorageRef.child("fotos").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Se subio exitosa la foto", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    //para sacar la url de la imagen cargada
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String photoLink = uri.toString();
                            Log.d("ESTE __________MENSAJE ", "URL__________: "+photoLink);

                            //codigo extra editar list view
                                Glide.with(MainActivity.this)
                                        .load(photoLink)
                                        .fitCenter()
                                        .centerCrop().into(imageView_foto);
                            //codigo extra

                        }
                    });
                    //fin de codigo que saca url

                }
            });
        }
    }
}
