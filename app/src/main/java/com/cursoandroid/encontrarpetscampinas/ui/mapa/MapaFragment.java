package com.cursoandroid.encontrarpetscampinas.ui.mapa;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.FragmentMapaBinding;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localUsuario;
    private DatabaseReference petsRef;
    private Marker marcadorUsuario;
    private ArrayList<Pet> listaPets = new ArrayList<>();
    ValueEventListener valueEventListenerPets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize view
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        petsRef = ConfiguracaoFirebase.getFirebaseDatabase().child("petspublicos");
        recuperarPets();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        adicionaMarcadorPets();
        recuperarLocalizacaoUsuario();


    }

    private void centralizarMarcador( LatLng local ){
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom( local, 17 ) );
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

    private void adicionaMarcadorPets(){
        mMap.clear();

        for ( Pet pet : listaPets){

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

        }
    }

    private void recuperarLocalizacaoUsuario() {


        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                localUsuario = new LatLng(
                        latitude,
                        longitude
                );

                adicionaMarcadorUsuario( localUsuario, "ddd" );
                centralizarMarcador( localUsuario );
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    00,
                    00,
                    locationListener
            );
    }



    public void recuperarPets() {
        listaPets.clear();

        valueEventListenerPets = petsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dados : snapshot.getChildren()) {

                    Pet pet = dados.getValue(Pet.class);
                    pet.setId(dados.getKey());

                    listaPets.add(pet);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}