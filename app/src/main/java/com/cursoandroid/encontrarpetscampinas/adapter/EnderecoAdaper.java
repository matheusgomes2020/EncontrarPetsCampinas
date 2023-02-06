package com.cursoandroid.encontrarpetscampinas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.model.Endereco;

import java.util.List;

public class EnderecoAdaper extends RecyclerView.Adapter<EnderecoAdaper.MyViewHolder>{

    private List<Endereco> endereco;
    private Context context;

    public EnderecoAdaper(List<Endereco> listaenderecos, Context c) {
        this.endereco = listaenderecos;
        this.context = c;
    }

    public List<Endereco> getEndereco(){
        return this.endereco;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemlista = LayoutInflater.from( parent.getContext() ).inflate(com.cursoandroid.encontrarpetscampinas.R.layout.adapter_endereco, parent, false );
        return new MyViewHolder( itemlista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Endereco ender = endereco.get( position );
        holder.endereco.setText( ender.getRua() + ", " + ender.getNumero() + " - " + ender.getBairro() + " - " + ender.getCidade());


    }



    @Override
    public int getItemCount() {
        return endereco.size();
    }


    public class  MyViewHolder extends RecyclerView.ViewHolder {


        TextView endereco;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            endereco = itemView.findViewById(R.id.textViewEnderecoA);
        }
    }

}
