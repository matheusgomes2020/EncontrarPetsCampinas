package com.cursoandroid.encontrarpetscampinas.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.activity.DescricaoPetActivity;
import com.cursoandroid.encontrarpetscampinas.adapter.PetsAdapter;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.FragmentPetsAvistadosBinding;
import com.cursoandroid.encontrarpetscampinas.helper.RecyclerItemClickListener;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PetsAvistadosFragment extends Fragment {

    private FragmentPetsAvistadosBinding binding;
    private DatabaseReference petsRef;
    private ValueEventListener valueEventListenerPets;
    private PetsAdapter adapter;
    private ArrayList<Pet> listaPets = new ArrayList<>();
    private ArrayList<String> listaFiltrosAtivos = new ArrayList<>();
    private ArrayList<String> listaFiltrosAtivosTipo = new ArrayList<>();
    private ArrayList<String> listaFiltrosAtivosIdade = new ArrayList<>();
    private ArrayList<String> listaFiltrosAtivosTempoDesaparecido = new ArrayList<>();
    private ArrayList<Pet> listaTemporaria1 = new ArrayList<>();

    private android.app.AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPetsAvistadosBinding.inflate( inflater, container, false );
        View root = binding.getRoot();

        petsRef = ConfiguracaoFirebase.getFirebaseDatabase().child("petspublicos");

        //configurar adapter
        adapter = new PetsAdapter(listaPets, getActivity());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewListaPets.setLayoutManager(layoutManager);
        binding.recyclerViewListaPets.setHasFixedSize(true);
        binding.recyclerViewListaPets.setAdapter(adapter);

        binding.imageFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheetFiltros();
            }
        });

        binding.searchPets.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if ( listaFiltrosAtivosTipo.size() <1 && listaFiltrosAtivosIdade.size() <1 &&
                        listaFiltrosAtivosTempoDesaparecido.size() <1 ){
                    pesquisarPets( s );
                }else {
                    pesquisarPetsComFiltros( s );
                }
                return true;
            }
        });

        //Configurar evento de clique no recyclerview
        binding.recyclerViewListaPets.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        binding.recyclerViewListaPets,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<Pet> listaPetsAtualizada = adapter.getPets();
                                Pet petSelecionado = listaPetsAtualizada.get(position);
                                Intent i = new Intent(getActivity(), DescricaoPetActivity.class);
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
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        listaPets.clear();
        recuperarPets();
    }

    public void recarregarPets(){
        adapter = new PetsAdapter(listaPets, getActivity());
        binding.recyclerViewListaPets.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recuperarPets() {

        dialog = new SpotsDialog.Builder()
                .setContext( getActivity() )
                .setMessage( "Carregando pets" )
                .setCancelable( false )
                .build();


        dialog.show();

        listaPets.clear();

        valueEventListenerPets = petsRef.orderByChild("dataCriacao").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()) {

                    Pet pet = dados.getValue(Pet.class);
                    Log.d("keyUsuario", pet.getId());
                    pet.setId(dados.getKey());
                    Log.d("keyUsuario", pet.getId());

                    if ( pet.getSituacao().equals( "perdido" ) ){

                        if ( pet.getPerdidoOuAvistado().equals( "avistado" ) ){
                            listaPets.add(pet);
                        }

                    }


                }
                Collections.reverse(listaPets);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void filtrosAtivosNovo(  ) {
        ArrayList<Pet> listaTemporaria = new ArrayList<>();
        ArrayList<Pet> listaTemporaria2 = new ArrayList<>();

        //Verifica se a team algum filtro de tipo ativo.
        if ( !listaFiltrosAtivosTipo.isEmpty() ){
            for ( String tipo : listaFiltrosAtivosTipo ){
                for ( Pet p : listaPets){
                    if ( p.getTipo().contains( tipo ) ){
                        listaTemporaria.add( p );
                    }
                }
            }
        } else{
            listaTemporaria = listaPets;
        }

        //Verifica se a team algum filtro de idade ativo.
        if ( !listaFiltrosAtivosIdade.isEmpty() ){
            for ( String idade : listaFiltrosAtivosIdade ){
                for ( Pet p : listaTemporaria ){
                    if ( p.getIdade().contains( idade ) ){
                        listaTemporaria2.add( p );
                    }
                }
            }
        }else {
            listaTemporaria2 = listaTemporaria;
        }

        listaTemporaria1.clear();
        listaTemporaria1 = listaTemporaria2;

        adapter = new PetsAdapter(listaTemporaria1, getActivity());
        binding.recyclerViewListaPets.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    //Pesquisar pets sem filtros de busca ativos.
    public void pesquisarPets( String texto ){
        List<Pet> listaPetsPesquisa = new ArrayList<>();

        for ( Pet pet : listaPets){
            String raca = pet.getRaca().toLowerCase();
            String bairro = pet.getEndereco().getBairro().toLowerCase();
            String rua = pet.getEndereco().getRua().toLowerCase();
            if ( raca.contains( texto ) || bairro.contains( texto ) || rua.contains( texto ) ){
                listaPetsPesquisa.add( pet );
            }
        }

        adapter = new PetsAdapter(listaPetsPesquisa, getActivity());
        binding.recyclerViewListaPets.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //Pesquisar pets com filtros de busca ativos.
    public void pesquisarPetsComFiltros(String texto ){
        List<Pet> lista01 = new ArrayList<>();

        for ( Pet pet : listaTemporaria1 ){
            String raca = pet.getRaca().toLowerCase();
            String bairro = pet.getEndereco().getBairro().toLowerCase();
            String rua = pet.getEndereco().getRua().toLowerCase();
            if ( raca.contains( texto ) || bairro.contains( texto ) || rua.contains( texto ) ){
                lista01.add( pet );
            }
        }

        adapter = new PetsAdapter(lista01, getActivity());
        binding.recyclerViewListaPets.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void showSheetFiltros(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( getActivity() );
        bottomSheetDialog.setContentView( com.cursoandroid.encontrarpetscampinas.R.layout.fragment_bottom_filtros );
        Button botaoOk = bottomSheetDialog.findViewById( R.id.buttonOk );
        Button botaoLimpar = bottomSheetDialog.findViewById( R.id.buttonLimpar );

        ChipGroup chipGroup = bottomSheetDialog.findViewById( R.id.chip_group_filtro );
        ChipGroup chipGroupIdade = bottomSheetDialog.findViewById( R.id.chip_group_idade );
        ChipGroup chipGroupTempoDesaparecido = bottomSheetDialog.findViewById( R.id.chip_group_tempo_desaparecimento );

        for ( String fTipo : listaFiltrosAtivosTipo ){
            for (int i=0; i<chipGroup.getChildCount();i++){
                Chip chip = (Chip)chipGroup.getChildAt(i);

                String a = chip.getText().toString();
                if ( fTipo.equals( a ) ){
                    chip.setChecked( true );
                }
            }
        }

        for ( String fTipo : listaFiltrosAtivosIdade ){

            for (int i=0; i<chipGroupIdade.getChildCount();i++){
                Chip chip = (Chip)chipGroupIdade.getChildAt(i);

                String a = chip.getText().toString();

                if ( fTipo.equals( a ) ){
                    chip.setChecked( true );
                }
            }
        }

        for ( String fTipo : listaFiltrosAtivosTempoDesaparecido ){

            for (int i=0; i<chipGroupTempoDesaparecido.getChildCount();i++){
                Chip chip = (Chip)chipGroupTempoDesaparecido.getChildAt(i);

                String a = chip.getText().toString();
                if ( fTipo.equals( a ) ){
                    chip.setChecked( true );
                }
            }
        }

        botaoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaFiltrosAtivosTipo.clear();
                listaFiltrosAtivosIdade.clear();
                listaFiltrosAtivosTempoDesaparecido.clear();

                List<String> filtrosString = new ArrayList<>();
                for (int i=0; i<chipGroup.getChildCount();i++){
                    Chip chip = (Chip)chipGroup.getChildAt(i);

                    String a = chip.getText().toString();

                    for (String isAtivo : listaFiltrosAtivosTipo){
                        if ( isAtivo == a ){
                        }
                    }

                    if (chip.isChecked()){
                        listaFiltrosAtivosTipo.add( a );
                    }
                }

                for (int i=0; i<chipGroupIdade.getChildCount();i++){
                    Chip chip = (Chip)chipGroupIdade.getChildAt(i);

                    String a = chip.getText().toString();

                    for (String isAtivo : listaFiltrosAtivosIdade){
                        if ( isAtivo == a ){
                        }
                    }

                    if (chip.isChecked()){
                        listaFiltrosAtivosIdade.add( a );
                    }
                }

                for (int i=0; i<chipGroupTempoDesaparecido.getChildCount();i++){
                    Chip chip = (Chip)chipGroupTempoDesaparecido.getChildAt(i);

                    String a = chip.getText().toString();

                    for (String isAtivo : listaFiltrosAtivosTempoDesaparecido){
                        if ( isAtivo == a ){
                        }
                    }

                    if (chip.isChecked()){
                        listaFiltrosAtivosTempoDesaparecido.add( a );
                    }
                }
                filtrosAtivosNovo();
                //filtros( filtrosString );
                bottomSheetDialog.dismiss();
            }
        });

        botaoLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaFiltrosAtivosTipo.clear();
                listaFiltrosAtivosIdade.clear();
                listaFiltrosAtivosTempoDesaparecido.clear();
                listaFiltrosAtivos.clear();
                recarregarPets();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }
}