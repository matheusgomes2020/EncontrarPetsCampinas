package com.cursoandroid.encontrarpetscampinas.ui.perfil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.activity.CadastroPetActivity;
import com.cursoandroid.encontrarpetscampinas.activity.DescricaoMeuPetActivity;
import com.cursoandroid.encontrarpetscampinas.activity.DescricaoPetActivity;
import com.cursoandroid.encontrarpetscampinas.activity.EditarDadosPetActivity;
import com.cursoandroid.encontrarpetscampinas.activity.FavoritosActivity;
import com.cursoandroid.encontrarpetscampinas.activity.LoginActivity;
import com.cursoandroid.encontrarpetscampinas.adapter.PetsAdapter;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.FragmentPerfilBinding;
import com.cursoandroid.encontrarpetscampinas.helper.RecyclerItemClickListener;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference petsRef;
    private ArrayList<Pet> listaPets = new ArrayList<>();
    Pet pet;
    private String identificadorUsuario;
    private PetsAdapter adapter;
    private ValueEventListener valueEventListenerPets;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu( true );

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Toolbar toolbar = root.findViewById( R.id.toolbarCadastroPets );
        ((AppCompatActivity)getActivity()).setSupportActionBar( toolbar );
        toolbar.setTitle( "Meus Pets" );

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();



        petsRef = ConfiguracaoFirebase.getFirebaseDatabase().child("pets").child(identificadorUsuario);
        DatabaseReference petsRef2 = ConfiguracaoFirebase.getFirebaseDatabase().child("pets").child(identificadorUsuario);

        //configurar adapter
        adapter = new PetsAdapter(listaPets, getActivity());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerListaPetsPerfil.setLayoutManager(layoutManager);
        binding.recyclerListaPetsPerfil.setHasFixedSize(true);
        binding.recyclerListaPetsPerfil.setAdapter(adapter);

        binding.fabRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CadastroPetActivity.class);
                startActivity(i);
            }
        });

        //Configurar evento de clique no recyclerview
        binding.recyclerListaPetsPerfil.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        binding.recyclerListaPetsPerfil,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Pet petSelecionado = listaPets.get(position);
                                pet = petSelecionado;
                                Intent i = new Intent( getActivity(), DescricaoMeuPetActivity.class );
                                i.putExtra("petPerdidoIntent", petSelecionado);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Pet petSelecionado = listaPets.get(position);
                                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( getActivity() );
                                bottomSheetDialog.setContentView( R.layout.bottom_sheet_meus_pets );

                                LinearLayout editar = bottomSheetDialog.findViewById(R.id.linearEditar);
                                LinearLayout excluir = bottomSheetDialog.findViewById(R.id.linearExcluir);
                                LinearLayout encontrado = bottomSheetDialog.findViewById(R.id.linearEncontado);
                                TextView textView = bottomSheetDialog.findViewById( R.id.textNomeSheet );
                                TextView textViewEncontrado = bottomSheetDialog.findViewById( R.id.textViewEncontado );
                                if ( petSelecionado.getPerdidoOuAvistado().equals("perdido") ){
                                    textView.setText( petSelecionado.getNome() );
                                }else {
                                    textView.setText( "Pet Avistado" );
                                    textViewEncontrado.setText( "Resgatado" );

                                }

                                editar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Pet petSelecionado = listaPets.get(position);
                                        Intent i = new Intent( getActivity(), EditarDadosPetActivity.class );
                                        i.putExtra("dadospet", petSelecionado);
                                        startActivity(i);
                                        bottomSheetDialog.dismiss();
                                    }
                                });

                                excluir.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                        dialog.setTitle("Confirmar exclusão");
                                        dialog.setMessage("Deseja excluir  o pet " + petSelecionado.getNome() + " ?");

                                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                petSelecionado.remover();
                                                listaPets.clear();
                                                adapter.notifyDataSetChanged();

                                            }
                                        });

                                        dialog.setNegativeButton("Não", null);
                                        dialog.create();
                                        dialog.show();


                                        bottomSheetDialog.dismiss();
                                    }
                                });

                                encontrado.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                        if ( petSelecionado.getPerdidoOuAvistado().equals("perdido") ){
                                            dialog.setTitle("Confirmar pet encontrado");
                                            dialog.setMessage("Deseja marcar o pet " + petSelecionado.getNome() + " como encontrado?");

                                        }else {
                                            dialog.setTitle("Confirmar pet resgatado");
                                            dialog.setMessage("Deseja marcar como resgatado?");


                                        }


                                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                petSelecionado.setSituacao( "encontrado" );
                                                petSelecionado.atualizar( "situacao" );

                                                listaPets.clear();
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                        dialog.setNegativeButton("Não", null);
                                        dialog.create();
                                        dialog.show();

                                        bottomSheetDialog.dismiss();
                                    }
                                });

                                bottomSheetDialog.show();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            }
                        }
                )
        );

        return root;
    }

    @Override
    public void onStart() {
        listaPets.clear();
        recuperarPets();
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void a(View view) {

        Intent i = new Intent(getActivity(), CadastroPetActivity.class);
        startActivity(i);
    }



    public void recarregarPets() {
        listaPets.clear();
        adapter = new PetsAdapter(listaPets, getActivity().getApplicationContext());
        recuperarPets();
        binding.recyclerListaPetsPerfil.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recuperarPets() {
        listaPets.clear();

        valueEventListenerPets = petsRef.orderByChild("dataCriacao").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()) {

                    Pet pet = dados.getValue(Pet.class);
                    pet.setId(dados.getKey());
                    Log.d("keyUsuario", pet.getId());

                    if ( pet.getSituacao().equals("perdido") ){
                        listaPets.add(pet);
                    }


                }
                Collections.reverse(listaPets);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate( R.menu.menu_perfil, menu );
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ){

            case R.id.filtro :
                startActivity( new Intent(getActivity(), FavoritosActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}