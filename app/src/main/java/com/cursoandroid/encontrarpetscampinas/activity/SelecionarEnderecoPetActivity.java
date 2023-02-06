package com.cursoandroid.encontrarpetscampinas.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivitySelecionarEnderecoPetBinding;
import com.cursoandroid.encontrarpetscampinas.model.Endereco;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelecionarEnderecoPetActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localUsuario;
    private LatLng localMarcador;
    private Marker marcadorUsuario;
    private Endereco enderecoEscolido;
    private String nomee;
    private String perdidoOuAvistado;
    private String especie;
    private String raca;
    private String idade;
    private String tipoActivity;
    private Pet pp = new Pet();


    private AppBarConfiguration appBarConfiguration;
    private ActivitySelecionarEnderecoPetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelecionarEnderecoPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(com.cursoandroid.encontrarpetscampinas.R.id.toolbarCadastroPets));
        getSupportActionBar().setTitle( "Localização pet" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_black_24p);

        Bundle bundle = getIntent().getExtras();
        String nome = bundle.getString( "nome" );
        String perdidoOuAvistado = bundle.getString( "perdidoOuAvistado" );
        String especie = bundle.getString( "especie" );
        String raca = bundle.getString( "raca" );
        String idade = bundle.getString( "idade" );
        //String tipoA = bundle.getString( "tipoActivity" );
        // pp = bundle.getParcelable( "petObjeto" );
        // tipoActivity = tipoA;
        nomee = nome;

        enderecoEscolido = new Endereco();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.cursoandroid.encontrarpetscampinas.R.id.map);
        mapFragment.getMapAsync(this);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Double l = localMarcador.latitude;
                String ll = l.toString();

                Double lo = localMarcador.longitude;
                String llo = lo.toString();


                Intent i = new Intent( SelecionarEnderecoPetActivity.this, CadastroPetActivity.class);
                i.putExtra( "endereco", enderecoEscolido );
                i.putExtra( "nome", nomee );
                i.putExtra( "perdidoOuAvistado", perdidoOuAvistado );
                i.putExtra( "especie", especie );
                i.putExtra( "raca", raca );
                i.putExtra( "idade", idade );
                i.putExtra( "latitude", l );
                i.putExtra( "longitude", lo );
                startActivity( i );
                finish();




            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;



        recuperarLocalizacaoUsuario();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mMap.clear();
                Double latitude = latLng.latitude;
                Double longitude = latLng.longitude;

                localMarcador = new LatLng(
                        latitude,
                        longitude
                );


                mMap.addMarker(
                        new MarkerOptions()
                                .position( latLng )
                                .title("Local")
                                .snippet("Descricao")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.paw))
                );

                adicionaMarcadorUsuario( localUsuario, "Local" );

            }


        });

    }

    private void inicio(){
        adicionaMarcadorUsuario( localUsuario, "usuario.getNome()" );
        centralizarMarcador( localUsuario );
    }

    private void centralizarMarcador( LatLng local ){
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom( local, 20 ) );
    }

    private void adicionaMarcadorUsuario(LatLng localizacao, String titulo){
        if( marcadorUsuario != null )
            marcadorUsuario.remove();

        marcadorUsuario = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocationmap))
        );

    }


    private void recuperarLocalizacaoUsuario() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();




                localUsuario = new LatLng(latitude, longitude);

                inicio();


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        //Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    00,
                    00,
                    locationListener
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
        Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
    }

}