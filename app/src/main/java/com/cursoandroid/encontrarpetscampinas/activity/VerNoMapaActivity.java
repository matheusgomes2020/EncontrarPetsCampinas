package com.cursoandroid.encontrarpetscampinas.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityVerNoMapaBinding;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VerNoMapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityVerNoMapaBinding binding;
    private Pet pet = new Pet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVerNoMapaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Bundle bundle = getIntent().getExtras();
        Pet pet = (Pet) bundle.getSerializable( "dadosPet" );
        Toast.makeText(getApplicationContext(), pet.getEndereco().getRua(), Toast.LENGTH_SHORT).show();

        setSupportActionBar(findViewById(com.cursoandroid.encontrarpetscampinas.R.id.toolbarCadastroPets));
        getSupportActionBar().setTitle( pet.getNome() );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_black_24p);

        this.pet = pet;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.setMapType( GoogleMap.MAP_TYPE_SATELLITE );

        adicionaMarcadorPet();
    }

    private void adicionaMarcadorPet(){

        mMap.clear();

        Double latitude = Double.parseDouble( pet.getEndereco().getLatitude() );
        Double longitude = Double.parseDouble( pet.getEndereco().getLongitude() );
        LatLng ll = new LatLng(
                latitude,
                longitude
        );

        String  perdidoOuAvistado = "perdido";
        String  nome = pet.getNome();
        if ( pet.getPerdidoOuAvistado().equals("avistado") ){

            perdidoOuAvistado = "";
            nome = "Pet Avistado";

        }

        switch ( pet.getTipo() ){

            case "cachorro" :
                mMap.addMarker(
                        new MarkerOptions()
                                .position(ll)
                                .title(nome)
                                .snippet( perdidoOuAvistado )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dogmap))
                );
                break;

            case "gato" :
                mMap.addMarker(
                        new MarkerOptions()
                                .position(ll)
                                .title(nome)
                                .snippet( perdidoOuAvistado )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.catmap))
                );
                break;

            case "coelho" :
                mMap.addMarker(
                        new MarkerOptions()
                                .position(ll)
                                .title(nome)
                                .snippet( perdidoOuAvistado )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.rabbitmap))
                );
                break;

            case "hamster" :
                mMap.addMarker(
                        new MarkerOptions()
                                .position(ll)
                                .title(nome)
                                .snippet( perdidoOuAvistado )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.hamstermap))
                );
                break;

            case "tartaruga" :
                mMap.addMarker(
                        new MarkerOptions()
                                .position(ll)
                                .title(nome)
                                .snippet( perdidoOuAvistado )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.turtlemap))
                );
                break;

            case "aves" :
                mMap.addMarker(
                        new MarkerOptions()
                                .position(ll)
                                .title(nome)
                                .snippet( perdidoOuAvistado )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.birdmap))
                );
                break;



        }



        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom( ll, 19 ) );

    }
}