package com.cursoandroid.encontrarpetscampinas.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.adapter.EnderecoAdaper;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityEdirarDadosPetBinding;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class EditarDadosPetActivity extends AppCompatActivity implements
        View.OnClickListener {

    private ActivityEdirarDadosPetBinding binding;
    private Pet pet = new Pet();

    private EnderecoAdaper adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEdirarDadosPetBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot() );

        setSupportActionBar(findViewById(com.cursoandroid.encontrarpetscampinas.R.id.toolbarCadastroPets));
        getSupportActionBar().setTitle( "Editar dados pet" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_black_24p);

        Bundle bundle = getIntent().getExtras();

        Pet pet =  (Pet) bundle.getSerializable( "dadospet" );

        this.pet = pet;
            binding.editTextNome.setText( pet.getNome() );
            binding.editTextTipo.setText( pet.getTipo() );
            binding.editTextRaca.setText( pet.getRaca() );
            binding.editTextIdade.setText( pet.getIdade() );
            binding.editTextUltimaLocalizacao.setText( pet.getEndereco().getRua() + ", "
                    + pet.getEndereco().getNumero() + " - " + pet.getEndereco().getBairro() );
            //binding.editTextUltimaLocalizacao.setClickable( false );
            binding.editTextDataDesaparecimento.setText( pet.getDataPerdido() );
            binding.editTextDescricao.setText( pet.getDescricao() );

            binding.imageViewNomeEditar.setOnClickListener( this );
            binding.imageTipoEditar.setOnClickListener( this );
            binding.imageViewIdadeEditar.setOnClickListener( this );
            binding.imageRacaEditar.setOnClickListener( this );
            binding.imageViewLocalizacaoEditar.setOnClickListener( this );
            binding.imageViewLocalizacaoMapaEditar.setOnClickListener( this );
            binding.imageDataDesaparecimentoEditar.setOnClickListener( this );
            binding.imageDescricaoEditar.setOnClickListener( this );




        binding.editTextTipo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheetDialog();
            }
        });

        binding.editTextIdade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheetDialogIdade();
            }
        });



    }

    @Override
    public void onClick(View view) {
        switch ( view.getId() ){
            case R.id.imageViewNomeEditar :
                String nomePet = binding.editTextNome.getText().toString();
                if ( !nomePet.isEmpty() ){
                    pet.setNome( nomePet );
                    pet.atualizar( "nome" );
                    Toast.makeText(this, "Nome atualizado com sucesso!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Nome não pode ser vazio!!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageTipoEditar :
                String tipoPet = binding.editTextTipo.getText().toString();
                if ( !tipoPet.isEmpty() ){
                    pet.setTipo( tipoPet );
                    pet.atualizar( "tipo" );
                    Toast.makeText(this, "Tipo atualizado com sucesso!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Tipo não pode ser vazio!!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageRacaEditar :
                String racaPet = binding.editTextRaca.getText().toString();
                if ( !racaPet.isEmpty() ){
                    pet.setRaca( racaPet );
                    pet.atualizar( "raca" );
                    Toast.makeText(this, "Raça atualizada com sucesso!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Raça não pode ser vazia!!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageViewIdadeEditar :
                String idadePet = binding.editTextIdade.getText().toString();
                if ( !idadePet.isEmpty() ){
                    pet.setIdade( idadePet );
                    pet.atualizar( "idade" );
                    Toast.makeText(this, "Idade atualizada com sucesso!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Idade não pode ser vazia!!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageDataDesaparecimentoEditar :
                String dataPerdido = binding.editTextDataDesaparecimento.getText().toString();
                if ( !dataPerdido.isEmpty() ){
                    pet.setDataPerdido( dataPerdido );
                    pet.atualizar( "dataPerdido" );
                    Toast.makeText(this, "Data de desaparecimento atualizada com sucesso!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Data de desaparecimento não pode ser vazia!!!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageDescricaoEditar :
                String descricaoPet = binding.editTextDescricao.getText().toString();
                if ( !descricaoPet.isEmpty() ){
                    pet.setDescricao( descricaoPet );
                    pet.atualizar( "descricao" );
                    Toast.makeText(this, "Descrição atualizada com sucesso!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Descrição não pode ser vazia!!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }

    private void showSheetDialog(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( this );
        bottomSheetDialog.setContentView( R.layout.bootom_sheet_categorias );

        LinearLayout cachorro = bottomSheetDialog.findViewById(R.id.linearCachorro);
        LinearLayout gato = bottomSheetDialog.findViewById(R.id.linearGato);
        LinearLayout hamster = bottomSheetDialog.findViewById(R.id.linearHaster);
        LinearLayout coelho = bottomSheetDialog.findViewById(R.id.linearCoelho);
        LinearLayout tartaruga = bottomSheetDialog.findViewById(R.id.linearTartaruga);
        LinearLayout aves = bottomSheetDialog.findViewById(R.id.linearAves);

        cachorro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextTipo.setText( "cachorro" );
                bottomSheetDialog.dismiss();
            }
        });

        gato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextTipo.setText( "gato" );
                bottomSheetDialog.dismiss();
            }
        });

        hamster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextTipo.setText( "hamster" );
                bottomSheetDialog.dismiss();
            }
        });

        coelho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextTipo.setText( "coelho" );
                bottomSheetDialog.dismiss();
            }
        });
        tartaruga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextTipo.setText( "tartaruga" );
                bottomSheetDialog.dismiss();
            }
        });

        aves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextTipo.setText( "aves" );
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();

    }

    private void showSheetDialogIdade(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( this );
        bottomSheetDialog.setContentView( R.layout.bottom_sheet_idade );

        LinearLayout novo = bottomSheetDialog.findViewById(R.id.linearNovo);
        LinearLayout normal = bottomSheetDialog.findViewById(R.id.linearNormal);
        LinearLayout senhor = bottomSheetDialog.findViewById(R.id.linearSenhor);

        novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextIdade.setText( "jovvem" );
                bottomSheetDialog.dismiss();
            }
        });

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextIdade.setText( "normal" );
                bottomSheetDialog.dismiss();
            }
        });

        senhor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextIdade.setText( "idoso" );
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();

    }



}