package com.cursoandroid.encontrarpetscampinas.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityDescricaoMeuPetBinding;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.ImageListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DescricaoMeuPetActivity extends AppCompatActivity {

    private ActivityDescricaoMeuPetBinding binding;
    private Boolean favorito = true;
    private Pet pet;
    // private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDescricaoMeuPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById( R.id.toolbar2 );
        toolbar.setTitleTextColor(Color.WHITE );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_baseline_arrow_back_24 );


        Bundle bundle = getIntent().getExtras();

        Pet pet =  (Pet) bundle.getSerializable( "petPerdidoIntent" );
        this.pet = pet;
        //Toast.makeText(this, "Cria pet", Toast.LENGTH_SHORT).show();

        if ( pet.getPerdidoOuAvistado().equals("perdido") ) {
            getSupportActionBar().setTitle( pet.getNome() );
        }else {
            getSupportActionBar().setTitle( "Pet avistado" );

        }



        binding.textNomePet.setText( pet.getNome() );
        binding.textId.setText( pet.getId() );
        binding.textRacaPet.setText( pet. getRaca());
        binding.textIdadePet.setText( pet.getIdade() );
        binding.textDataDesaparecimentoPet.setText( pet.getDataPerdido() );
        binding.textlocalDesaparecimentoPet.setText( pet.getEndereco().getRua() + ", " + pet.getEndereco().getNumero() );
        binding.textBairroDesaparecimentoPet.setText( pet.getEndereco().getBairro() );
        binding.textViewDescricao.setText( pet.getDescricao());


        switch ( pet.getTipo() ) {

            case "cachorro" :
                binding.imageTipo.setImageResource( R.drawable.dog1 );
                break;
            case "gato" :
                binding.imageTipo.setImageResource(R.drawable.cat1);
                break;
            case "hamster" :
                binding.imageTipo.setImageResource(R.drawable.hamster1);
                break;
            case "coelho" :
                binding.imageTipo.setImageResource(R.drawable.rabbit1);
                break;
            case "tartaruga" :
                binding.imageTipo.setImageResource(R.drawable.turtle1);
                break;
            case "aves" :
                binding.imageTipo.setImageResource(R.drawable.bird1);
                break;

        }


        switch ( pet.getIdade() ){

            case "novo" :
                binding.imageIdade.setImageResource(R.drawable.babyi);
                break;

            case "normal" :
                binding.imageIdade.setImageResource(R.drawable.normali);
                break;

            case "senhor" :
                binding.imageIdade.setImageResource(R.drawable.seniori);
                break;

        }


        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dataPostagem = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" ).parse( pet.getDataCriacao() );
            String resultado = "Publicado em: " +  formatter.format( dataPostagem );
            binding.textPublicandoEm.setText( resultado );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                String urlString = pet.getFotos().get( position );
                Picasso.get().load(urlString).into(imageView);
            }
        };

        binding.carouselView2.setPageCount( pet.getFotos().size() );
        binding.carouselView2.setImageListener( imageListener );

        binding.textVerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( DescricaoMeuPetActivity.this, VerNoMapaActivity.class);
                i.putExtra( "dadosPet", pet);
                startActivity( i );
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_descricao_meu_pet, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        MenuInflater inflater = getMenuInflater();

        switch ( item.getItemId() ){

            case R.id.m_editar :

                Toast.makeText(this, "Editar", Toast.LENGTH_SHORT).show();
                Intent i = new Intent( getApplicationContext() , EditarDadosPetActivity.class );
                i.putExtra("dadospet", pet);
                startActivity(i);

                break;

            case R.id.m_excluir :

                Toast.makeText(this, "Excluir", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder dialog = new AlertDialog.Builder( this );
                dialog.setTitle("Confirmar exclusão");
                dialog.setMessage("Deseja excluir  o pet " + pet.getNome() + " ?");

                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        pet.remover();
                    }
                });

                dialog.setNegativeButton("Não", null);
                dialog.create();
                dialog.show();

                break;

            case R.id.m_encontrado :

                pet.setSituacao( "encontrado" );
                pet.atualizar( "situacao" );


                break;


        }

        return super.onOptionsItemSelected(item);
    }



}