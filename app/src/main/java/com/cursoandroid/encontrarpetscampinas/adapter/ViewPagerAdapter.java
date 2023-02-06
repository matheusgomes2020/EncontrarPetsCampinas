package com.cursoandroid.encontrarpetscampinas.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cursoandroid.encontrarpetscampinas.fragment.PetsAvistadosFragment;
import com.cursoandroid.encontrarpetscampinas.fragment.PetsPerdidosFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position==0) {
            return new PetsPerdidosFragment();
        }
        else {
            return new PetsAvistadosFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }}