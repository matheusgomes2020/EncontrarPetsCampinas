package com.cursoandroid.encontrarpetscampinas.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityDescricaoPetBinding;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.ImageListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DescricaoPetActivity extends AppCompatActivity {

    private ActivityDescricaoPetBinding binding;
    private Boolean favorito = true;
    private Pet pet;
   // private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDescricaoPetBinding.inflate(getLayoutInflater());
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




        getSupportActionBar().setTitle( pet.getNome() );


        binding.textNomePet.setText( pet.getNome() );
        binding.textId.setText( pet.getId() );
        binding.textRacaPet.setText( pet. getRaca());
        binding.textIdadePet.setText( pet.getIdade() );
        binding.textDataDesaparecimentoPet.setText( pet.getDataPerdido() );
        binding.textlocalDesaparecimentoPet.setText( pet.getEndereco().getRua() + ", " + pet.getEndereco().getNumero() );
        binding.textBairroDesaparecimentoPet.setText( pet.getEndereco().getBairro() );
        binding.textViewDescricao.setText( pet.getDescricao());

        binding.textNomeUsuario.setText( pet.getUsuario().getNome() );
        binding.textEmailUsuario.setText( pet.getUsuario().getEmail() );
        binding.textTelefoneUsuario.setText( pet.getUsuario().getTelefone() );

        switch ( pet.getTipo() ) {

            case "cachorro" :
                binding.imageTipo.setImageResource( com.cursoandroid.encontrarpetscampinas.R.drawable.dog1 );
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

        if ( pet.getPerdidoOuAvistado().equals("avistado") ){
            binding.linearNome.setVisibility(View.GONE );
            binding.dataDesaparecimento.setText( "Data Avistamento" );
            binding.localDesaparecimento.setText( "Local Avistamento" );

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
                Intent i = new Intent( DescricaoPetActivity.this, VerNoMapaActivity.class);
                i.putExtra( "dadosPet", pet);
                //Toast.makeText(DescricaoPetActivity.this, petPerdido.getEndereco().getRua(), Toast.LENGTH_SHORT).show();
                startActivity( i );
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference favoritosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "favoritos" )
                .child( idUsuarioLogado )
                .child( pet.getId() );

        favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ( snapshot.exists() ){

                    //Já favoritado
                    favorito = true;
                    menu.setGroupVisible( R.id.group_favoritado, true );
                    //Toast.makeText(DescricaoPetActivity.this, "Existe " + favorito.toString(), Toast.LENGTH_SHORT).show();


                }else {

                    //Não favoritado
                    favorito = false;
                    menu.setGroupVisible( R.id.group_nao_favoritado, true );
                    //Toast.makeText(DescricaoPetActivity.this, "Não existe: " + favorito.toString(), Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Toast.makeText(this, "Prepara", Toast.LENGTH_SHORT).show();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_descricao_pet, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        MenuInflater inflater = getMenuInflater();

        switch ( item.getItemId() ){

            case R.id.favorito:

                //Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
                pet.desfavoritar();
                //onRestart();

                break;

            case R.id.nao_favorito:

                //Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
                pet.favoritar();
                //onRestart();

                break;


        }

        return super.onOptionsItemSelected(item);
    }



}