package com.cursoandroid.encontrarpetscampinas.ui.pets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.adapter.ViewPagerAdapter;
import com.cursoandroid.encontrarpetscampinas.databinding.FragmentPetsBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PetsFragment extends Fragment {

    private FragmentPetsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentPetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Toolbar toolbar = root.findViewById( R.id.toolbarPrincipal );
        ((AppCompatActivity)getActivity()).setSupportActionBar( toolbar );

        binding.viewPagerPrincipal.setAdapter( new ViewPagerAdapter( this ));


        new TabLayoutMediator(binding.tabLayout, binding.viewPagerPrincipal, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                if (position==0){
                    tab.setText( "Perdidos" );
                }else {
                    tab.setText( "Avistados" );
                }
            }
        }).attach();

        return root;
    }



    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}