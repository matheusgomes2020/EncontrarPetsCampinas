package com.cursoandroid.encontrarpetscampinas.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.adapter.PetsAdapter;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityArquivadosBinding;
import com.cursoandroid.encontrarpetscampinas.helper.RecyclerItemClickListener;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ArquivadosActivity extends AppCompatActivity {

    private ActivityArquivadosBinding binding;
    private DatabaseReference petsRef;
    private DatabaseReference petsRef2;
    private PetsAdapter adapter;
    private ArrayList<Pet> listaPets = new ArrayList<>();
    private ArrayList<String> listaPetString = new ArrayList<>();
    private String identificadorUsuario;
    private ValueEventListener valueEventListenerPets;

    private android.app.AlertDialog dialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityArquivadosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById( R.id.toolbarArq );
        setSupportActionBar( toolbar );


        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        petsRef = ConfiguracaoFirebase.getFirebaseDatabase().child("pets").child(identificadorUsuario);



        //configurar adapter
        adapter = new PetsAdapter(listaPets, getApplicationContext());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerViewFavoritos.setLayoutManager(layoutManager);
        binding.recyclerViewFavoritos.setHasFixedSize(true);
        binding.recyclerViewFavoritos.setAdapter(adapter);

        //Configurar evento de clique no recyclerview
        binding.recyclerViewFavoritos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        binding.recyclerViewFavoritos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                /*
                                PetPerdido petPerdidoSelecionado = listaPetPerdidos.get(position);
                                Intent i = new Intent( getApplicationContext(), DescricaoPetActivity.class );
                                i.putExtra("petPerdidoIntent", petPerdidoSelecionado);
                                startActivity(i);

                                 */
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Toast.makeText(ArquivadosActivity.this, "Desarquivar?", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            }
                        }
                )
        );

    }

    @Override
    protected void onStart() {
        recuperarPets();
        super.onStart();
    }

    public void recuperarPets() {



        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage( "Carregando pets" )
                .setCancelable( false )
                .build();


        dialog.show();

        listaPets.clear();

        petsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()) {

                    Pet pet = dados.getValue(Pet.class);


                    if ( pet.getSituacao().equals("encontrado") ){
                        listaPets.add(pet);
                    }

                    //listaPetPerdidos.add(petPerdido);



                }

                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });





    }



    public void recarregarPets() {
        listaPets.clear();
        adapter = new PetsAdapter(listaPets, getApplicationContext().getApplicationContext());
        recuperarPets();
        binding.recyclerViewFavoritos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}

