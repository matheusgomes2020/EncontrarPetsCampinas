package com.cursoandroid.encontrarpetscampinas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityFavoritosBinding;
import com.cursoandroid.encontrarpetscampinas.helper.RecyclerItemClickListener;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class FavoritosActivity extends AppCompatActivity {

    private ActivityFavoritosBinding binding;
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

        binding = ActivityFavoritosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById( R.id.toolbarFav );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        petsRef = ConfiguracaoFirebase.getFirebaseDatabase().child("favoritos").child(identificadorUsuario);



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

                                Pet petSelecionado = listaPets.get(position);
                                Intent i = new Intent( getApplicationContext(), DescricaoPetActivity.class );
                                i.putExtra("petPerdidoIntent", petSelecionado);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

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
                    pet.setId(dados.getKey());
                    Log.d("keyUsuario", pet.getId());

                    //listaPetPerdidos.add(petPerdido);
                    listaPetString.add( pet.getId() );


                }
                recuperarPetsPublicos();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });





    }

    public void recuperarPetsPublicos(){


        listaPets.clear();

        petsRef2 = ConfiguracaoFirebase.getFirebaseDatabase().child("petspublicos");

        petsRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for ( DataSnapshot ss : snapshot.getChildren() ){
                    Pet pet2 = ss.getValue(Pet.class);

                    for ( String id : listaPetString ){

                        if ( id.equals( pet2.getId() ) ){

                            listaPets.add(pet2);
                            Toast.makeText(FavoritosActivity.this, "Pets ", Toast.LENGTH_SHORT).show();


                        }

                    }


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