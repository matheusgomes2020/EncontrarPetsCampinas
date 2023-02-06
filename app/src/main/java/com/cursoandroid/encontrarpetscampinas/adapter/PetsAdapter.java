package com.cursoandroid.encontrarpetscampinas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.helper.TimeAgo;
import com.cursoandroid.encontrarpetscampinas.model.Pet;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.MyViewHolder> {

    private List<Pet> pets;
    private Context context;

    public PetsAdapter(List<Pet> listapets, Context c) {
        this.pets = listapets;
        this.context = c;
    }

    public List<Pet> getPets(){
        return this.pets;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemlista = LayoutInflater.from( parent.getContext() ).inflate(R.layout.adapter_pets, parent, false );
        return new MyViewHolder( itemlista );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Pet pet = pets.get( position );
        holder.nome.setText( pet.getNome() );
        holder.raca.setText( pet.getRaca() );


        if ( pet.getEndereco().getBairro() !=null || pet.getEndereco().getBairro().isEmpty() ){
            holder.ultimaLocalizacao.setText( pet.getEndereco().getBairro() );
        } else {
            holder.ultimaLocalizacao.setText( "Campinas" );
        }

        holder.nomeTutor.setText( pet.getUsuario().getNome() );
        String tempoAtras = TimeAgo.timeAgoResultado( pet.getDataCriacao() );
        holder.tempoPostagem.setText( tempoAtras );

        //Pega a primeira imagem da lista
        List<String> urlFotos = pet.getFotos();
        String urlCapa = urlFotos.get( 0 );

        Glide.with( context ).load( urlCapa ).into( holder.foto );



        switch ( pet.getTipo() ) {

            case "cachorro" :
                holder.tipo.setImageResource( com.cursoandroid.encontrarpetscampinas.R.drawable.dog1 );
                break;
            case "gato" :
                holder.tipo.setImageResource(R.drawable.cat1);
                break;
            case "hamster" :
                holder.tipo.setImageResource(R.drawable.hamster1);
                break;
            case "coelho" :
                holder.tipo.setImageResource(R.drawable.rabbit1);
                break;
            case "tartaruga" :
                holder.tipo.setImageResource(R.drawable.turtle1);
                break;
            case "aves" :
                holder.tipo.setImageResource(R.drawable.bird1);
                break;

        }





        switch ( pet.getIdade() ){

            case "jovem" :
                holder.imageViewIdade.setImageResource(R.drawable.babyi);
                holder.idade.setText( "jovem" );
                break;

            case "normal" :
                holder.imageViewIdade.setImageResource(R.drawable.normali);
                holder.idade.setText( "normal" );
                break;

            case "idoso" :
                holder.imageViewIdade.setImageResource(R.drawable.seniori);
                holder.idade.setText( "idoso" );
                break;

        }


    }



    @Override
    public int getItemCount() {
        return pets.size();
    }


    public class  MyViewHolder extends RecyclerView.ViewHolder {

        ImageView foto, tipo, imageViewIdade;
        TextView nome, raca, idade, data,ultimaLocalizacao, nomeTutor, tempoPostagem;
        LinearLayout linearNome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textViewNomeA);
            raca = itemView.findViewById(R.id.textRacaA);
            idade = itemView.findViewById(R.id.textIdade);
            imageViewIdade = itemView.findViewById( R.id.imageViewIdadeEditar);
            ultimaLocalizacao = itemView.findViewById(R.id.textUltimaLocalizacaoA);
            foto = itemView.findViewById(R.id.imageFotoA);
            tipo = itemView.findViewById(R.id.imageViewTipo);
            nomeTutor = itemView.findViewById( R.id.textNomeTutorA );
            tempoPostagem = itemView.findViewById( R.id.textTempoPostagemA );
            linearNome = itemView.findViewById( R.id.linearNomeAdapter );
        }
    }


}