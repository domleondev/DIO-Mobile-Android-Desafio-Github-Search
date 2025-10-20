package com.domleondev.mobileandroid.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.mobileandroid.R
import com.domleondev.mobileandroid.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var carItemLister: (Repository) -> Unit = {}
    var btnShareLister: (Repository) -> Unit = {}

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 8 -  Realizar o bind do viewHolder
        val repository = repositories[position]

        // Bind do nome do repositório
        holder.nomeRepositorio.text = repository.name

        //  12 - Colocar esse metodo no click item do adapter (Clique em qualquer lugar do item)
        holder.itemView.setOnClickListener {
            carItemLister(repository)
        }

        //  11 - Colocar esse metodo no click do share item do adapter (Clique no ícone de share)
        holder.btnShare.setOnClickListener {
            btnShareLister(repository)
        }
    }

    // Pega a quantidade de repositorios da lista
    // 9 - realizar a contagem da lista
    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 10 - Implementar o ViewHolder para os repositorios
        // Adicionar os Ids das views do repository_item.xml
        val nomeRepositorio: TextView
        val btnShare: ImageView // Assumindo que iv_favorite é o botão de share

        init {
            nomeRepositorio = view.findViewById(R.id.tv_preco)
            btnShare = view.findViewById(R.id.iv_favorite)
        }
    }
    }