package com.cursoandroid.encontrarpetscampinas.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.adapter.EnderecoAdaper;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityCadastroPetBinding;
import com.cursoandroid.encontrarpetscampinas.helper.Permissao;
import com.cursoandroid.encontrarpetscampinas.helper.RecyclerItemClickListener;
import com.cursoandroid.encontrarpetscampinas.model.Endereco;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastroPetActivity extends AppCompatActivity implements
        View.OnClickListener {

    private ActivityCadastroPetBinding binding;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private StorageReference storage;
    private ArrayList<Pet> petList = new ArrayList<>();
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();
    Pet pet = new Pet();
    private android.app.AlertDialog dialog;
    Endereco endereco = new Endereco();
    private LatLng coordenadas;
    private EnderecoAdaper adapter;
    private String perdidoOuAvistadoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastroPetBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot() );

        setSupportActionBar(findViewById(R.id.toolbarCadastroPets));
        getSupportActionBar().setTitle( "Novo pet" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_black_24p);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            String nome = bundle.getString( "nome" );
            String perdidoOuAvistado = bundle.getString( "perdidoOuAvistado" );
            String especie = bundle.getString( "especie" );
            String raca = bundle.getString( "raca" );
            String idade = bundle.getString( "idade" );
            if (perdidoOuAvistado.equals("avistado")){
                binding.linearNome.setVisibility( View.GONE );
            }
            Double latitude = bundle.getDouble( "latitude" );
            Double longitude = bundle.getDouble( "longitude" );
            coordenadas = new LatLng(
                    latitude,
                    longitude

            );

            localizacaoPetMapa( coordenadas );

            binding.editTextNome.setText( nome );
            binding.editTextPerdidoOuAvistado.setText( perdidoOuAvistado );
            binding.editTextTipo.setText( especie );
            binding.editTextRaca.setText( raca );
            binding.editTextIdade.setText( idade );

        }


        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        binding.imageViewInserir.setOnClickListener( this );
        binding.imageViewInserir2.setOnClickListener( this );
        binding.imageViewInserir3.setOnClickListener( this );
        binding.imageViewLocalizacaoMapa.setOnClickListener( this );
        binding.imageViewLocalizacao.setOnClickListener( this );

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        binding.editTextTipo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheetEspecie();
            }
        });

        binding.editTextIdade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSheetIdade();
            }
        });

        binding.editTextPerdidoOuAvistado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetPerdidoOuAvistado();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch ( view.getId() ){
            case R.id.imageViewInserir :
                escolherImagem( 1 );
                break;
            case R.id.imageViewInserir2 :
                escolherImagem( 2 );
                break;
            case R.id.imageViewInserir3 :
                escolherImagem( 3 );
                break;

            case R.id.imageViewLocalizacaoMapa:
                Intent i = new Intent( CadastroPetActivity.this, SelecionarEnderecoPetActivity.class );
                i.putExtra( "nome", binding.editTextNome.getText().toString() );
                i.putExtra( "perdidoOuAvistado", binding.editTextPerdidoOuAvistado.getText().toString() );
                i.putExtra( "especie", binding.editTextTipo.getText().toString() );
                i.putExtra( "raca", binding.editTextRaca.getText().toString() );
                i.putExtra( "idade", binding.editTextIdade.getText().toString() );
                startActivity( i );
                break;

            case R.id.imageViewLocalizacao:
                showSheetEndereco();
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if( requestCode == 1 ){
                binding.imageViewInserir.setImageURI( imagemSelecionada );
            }else if( requestCode == 2 ){
                binding.imageViewInserir2.setImageURI( imagemSelecionada );
            }else if( requestCode == 3 ){
                binding.imageViewInserir3.setImageURI( imagemSelecionada );
            }

            listaFotosRecuperadas.add( caminhoImagem );

        }

    }

    private Pet configurarPet(){

        String nomePet = binding.editTextNome.getText().toString();



        String idadePet = binding.editTextIdade.getText().toString();
        String dataPet = binding.editTextDataDesaparecimento.getMasked().toString();
        String racaPet = binding.editTextRaca.getText().toString();
        String tipoPet = binding.editTextTipo.getText().toString();
        String descricaoPet = binding.editTextDescricao.getText().toString();
        String perdidoOuAvistado = binding.editTextPerdidoOuAvistado.getText().toString();


        Pet pet = new Pet();
        pet.setNome(nomePet);
        pet.setIdade(idadePet);
        pet.setDataPerdido(dataPet);
        pet.setRaca(racaPet);
        pet.setEndereco(endereco);
        pet.setDataCriacao( salvarData() );
        pet.setTipo( tipoPet );
        pet.setDescricao( descricaoPet );
        pet.setSituacao( "perdido" );
        pet.setPerdidoOuAvistado( perdidoOuAvistado );

        return pet;

    }


    public void validarDadosPet(View view) {

        pet = configurarPet();

        String ultimaP = endereco.getRua();

        if (!pet.getPerdidoOuAvistado().isEmpty()) {
            if ( pet.getPerdidoOuAvistado().equals("avistado") ){

                pet.setNome( "Pet Avistado" );

            }
            binding.imageCheckPerdidoOuAvistado.setImageResource(R.drawable.ic_check_verde);
        if (!pet.getNome().isEmpty()) {
            binding.imageCheckNome.setImageResource(R.drawable.ic_check_verde);
            if (!pet.getTipo().isEmpty()) {
                binding.imageCheckTipo.setImageResource(R.drawable.ic_check_verde);
                if (!pet.getIdade().isEmpty()) {
                    binding.imageCheckIdade.setImageResource(R.drawable.ic_check_verde);
                    if (!pet.getDataPerdido().isEmpty()) {
                        binding.imageCheckData.setImageResource(R.drawable.ic_check_verde);
                        if (!pet.getRaca().isEmpty()) {
                            binding.imageCheckRaca.setImageResource(R.drawable.ic_check_verde);
                            if (!ultimaP.isEmpty()) {
                                binding.imageCheckUltima.setImageResource(R.drawable.ic_check_verde);
                             if (!pet.getDescricao().isEmpty()) {
                                 binding.imageCheckDescricao.setImageResource(R.drawable.ic_check_verde);
                                 if ( listaFotosRecuperadas.size() > 0 ){
                                     binding.imageCheckImagem.setImageResource(R.drawable.ic_check_verde);

                                     //progressDialog.show();



                                     salvarPet();
                                         //finish();

                                 } else {Toast.makeText(CadastroPetActivity.this, "selecione ao menos uma imagem!", Toast.LENGTH_SHORT).show();
                                         binding.imageCheckImagem.setImageResource(R.drawable.ic_check_vermelho);}
                             } else {Toast.makeText(CadastroPetActivity.this, "Preencha a descrição!", Toast.LENGTH_SHORT).show();
                                 binding.imageCheckDescricao.setImageResource(R.drawable.ic_check_vermelho);}
                        } else {Toast.makeText(CadastroPetActivity.this, "Preencha a última localização!", Toast.LENGTH_SHORT).show();
                                binding.imageCheckUltima.setImageResource(R.drawable.ic_check_vermelho);}
                    } else { Toast.makeText(CadastroPetActivity.this, "Preencha a idade!", Toast.LENGTH_SHORT).show();
                            binding.imageCheckIdade.setImageResource(R.drawable.ic_check_vermelho);}
                    } else { Toast.makeText(CadastroPetActivity.this, "Preencha a data!", Toast.LENGTH_SHORT).show();
                        binding.imageCheckData.setImageResource(R.drawable.ic_check_vermelho);}
             } else { Toast.makeText(CadastroPetActivity.this, "Preencha a raça!", Toast.LENGTH_SHORT).show();
                    binding.imageCheckRaca.setImageResource(R.drawable.ic_check_vermelho);}
            } else { Toast.makeText(CadastroPetActivity.this, "Preencha o tipo!", Toast.LENGTH_SHORT).show();
                binding.imageCheckTipo.setImageResource(R.drawable.ic_check_vermelho);}
        } else { Toast.makeText(CadastroPetActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
            binding.imageCheckNome.setImageResource(R.drawable.ic_check_vermelho);}
        }else { Toast.makeText(CadastroPetActivity.this, "Preencha o campo perdido ou avistado!", Toast.LENGTH_SHORT).show();
                binding.imageCheckPerdidoOuAvistado.setImageResource(R.drawable.ic_check_vermelho);}
    }




    public String salvarData(){

        String data  = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

        return data;
    }


    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {

        //Criar nó no Storage
        StorageReference imagemPetPerdido = storage.child( "imagens" )
                .child( "pets" )
                .child( pet.getId() )
                .child( "imagem" + contador );

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemPetPerdido.putFile( Uri.parse( urlString ) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemPetPerdido.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri firebaseurl = task.getResult();
                        String urlConvertida = firebaseurl.toString();

                        listaUrlFotos.add( urlConvertida );

                        if ( totalFotos == listaUrlFotos.size() ){

                            pet.setFotos( listaUrlFotos );
                            pet.salvar(  );

                            //progressDialog.dismiss();
                            dialog.dismiss();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro( "Falha ao fazer upload" );
                Log.d( "INFO", "Falha ao fazer upload: " + e.getMessage() );
            }
        });
    }

    public void salvarPet(){
        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage( "Salvando pet" )
                .setCancelable( false )
                .build();


        dialog.show();


        /**
         * Salvarimagem no Storage
         */
        for ( int i=0; i < listaFotosRecuperadas.size(); i++ ){

            String urlImagem = listaFotosRecuperadas.get( i );
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage( urlImagem, tamanhoLista, i );
        }
    }

    private void exibirMensagemErro( String mensagem ){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    private void sheetPerdidoOuAvistado(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( this );
        bottomSheetDialog.setContentView( R.layout.bottom_sheet_perdido_ou_avistado );

        LinearLayout perdido = bottomSheetDialog.findViewById(R.id.linearPerdido);
        LinearLayout avistado = bottomSheetDialog.findViewById(R.id.linearAvistado);

        perdido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perdidoOuAvistadoString = "perdido";
                binding.editTextPerdidoOuAvistado.setText( "perdido" );
                binding.textViewUltimaLocalizacao.setText( "Última Localização" );
                binding.textViewDataDesaparecoimento.setText( "Data Desaparecimento" );
                binding.linearNome.setVisibility( View.VISIBLE );
                bottomSheetDialog.dismiss();
            }
        });

        avistado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perdidoOuAvistadoString = "avistado";
                binding.editTextPerdidoOuAvistado.setText( "avistado" );
                binding.textViewUltimaLocalizacao.setText( "Localização avistamento" );
                binding.textViewDataDesaparecoimento.setText( "Data Avistamento" );
                binding.linearNome.setVisibility( View.GONE );


                bottomSheetDialog.dismiss();
            }
        });



        bottomSheetDialog.show();

    }

    private void showSheetEspecie(){

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

    private void showSheetIdade(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( this );
        bottomSheetDialog.setContentView( R.layout.bottom_sheet_idade );

        LinearLayout novo = bottomSheetDialog.findViewById(R.id.linearNovo);
        LinearLayout normal = bottomSheetDialog.findViewById(R.id.linearNormal);
        LinearLayout senhor = bottomSheetDialog.findViewById(R.id.linearSenhor);

        novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextIdade.setText( "jovem" );
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

    private void showSheetEndereco(){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( this );
        bottomSheetDialog.setContentView( R.layout.bottom_sheet_enderecos );

        RecyclerView recyclerViewEnderecos = bottomSheetDialog.findViewById( R.id.recyclerEnderecos );
        List<Endereco> enderecos = new ArrayList<>();
        localizacaoPetPesquisa( enderecos );


        //configurar adapter
        adapter = new EnderecoAdaper( enderecos, getApplicationContext());

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewEnderecos.setLayoutManager(layoutManager);
        recyclerViewEnderecos.setHasFixedSize(true);
        recyclerViewEnderecos.setAdapter(adapter);

        //Configurar evento de clique no recyclerview
        recyclerViewEnderecos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewEnderecos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                endereco = enderecos.get( position );
                                Toast.makeText(CadastroPetActivity.this, endereco.getRua(), Toast.LENGTH_SHORT).show();
                                binding.editTextUltimaLocalizacao.setText( endereco.getRua() + ", " + endereco.getNumero()
                                        + " - " + endereco.getBairro() );
                                bottomSheetDialog.dismiss();
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

        bottomSheetDialog.show();
        if ( enderecos.size() < 1 ) {

            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Endereço não encontrado!!!", Toast.LENGTH_SHORT).show();
        }

    }

    public void localizacaoPetPesquisa(List<Endereco> listaEnderecos ){

        Geocoder geocoder = new Geocoder( getApplicationContext(), Locale.getDefault() );
        try {
            String stringEndereco = binding.editTextUltimaLocalizacao.getText().toString() ;
            List<Address> listaEndereco = geocoder.getFromLocationName( stringEndereco, 5 );
            for ( Address en : listaEndereco ){
                Endereco enderecoResultado = new Endereco();
                enderecoResultado.setLatitude( String.valueOf(en.getLatitude()) );
                enderecoResultado.setLongitude( String.valueOf(en.getLongitude()) );
                enderecoResultado.setCidade( en.getSubAdminArea() );
                enderecoResultado.setBairro( en.getSubLocality() );
                enderecoResultado.setRua( en.getThoroughfare() );
                enderecoResultado.setNumero( en.getFeatureName() );

                listaEnderecos.add( enderecoResultado );
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void localizacaoPetMapa(LatLng latLng ){

        Geocoder geocoder = new Geocoder( getApplicationContext(), Locale.getDefault() );
        try {
            List<Address> listaEndereco = geocoder.getFromLocation(  latLng.latitude, latLng.longitude, 1);
            Address enderecoResultado = listaEndereco.get(0);
            this.endereco.setRua( enderecoResultado.getThoroughfare() );
            this.endereco.setLatitude( String.valueOf(enderecoResultado.getLatitude()) );
            this.endereco.setLongitude( String.valueOf(enderecoResultado.getLongitude()) );
            this.endereco.setCep( enderecoResultado.getPostalCode() );
            this.endereco.setCidade( enderecoResultado.getSubAdminArea() );
            this.endereco.setBairro( enderecoResultado.getSubLocality() );
            this.endereco.setNumero( enderecoResultado.getFeatureName() );
            binding.editTextUltimaLocalizacao.setText( this.endereco.getRua() + ", " + this.endereco.getNumero()
            + " - " + this.endereco.getBairro() );

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}