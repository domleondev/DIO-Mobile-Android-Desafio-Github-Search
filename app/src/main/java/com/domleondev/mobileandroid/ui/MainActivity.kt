package com.domleondev.mobileandroid.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.domleondev.mobileandroid.R
import com.domleondev.mobileandroid.data.GitHubService
import com.domleondev.mobileandroid.domain.Repository
import com.domleondev.mobileandroid.ui.adapter.RepositoryAdapter
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val SHARED_PREF_KEY = "github_username"
class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()
        //getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        // 1 - Recuperar os Id's da tela para a Activity com o findViewById
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        //2 - colocar a acao de click do botao confirmar
        btnConfirmar.setOnClickListener {
            saveUserLocal() // Salva o nome do EditText
            getAllReposByUserName() // Busca os repositórios
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        //3 - Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar
        val nome = nomeUsuario.text.toString()
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString(SHARED_PREF_KEY, nome)
            apply() // Aplica as mudanças de forma assíncrona
        }
    }

    private fun showUserName() {
        // 4- depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor, exibir no proprio editText o valor salvo
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val savedName = sharedPref.getString(SHARED_PREF_KEY, "")

        if (!savedName.isNullOrEmpty()) {
            nomeUsuario.setText(savedName)
        }
    }

    private val GITHUB_URL = "https://api.github.com/"

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        /*
           5 -  realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */
        val retrofit = Retrofit.Builder()
            .baseUrl(GITHUB_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        // 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso
        val user = nomeUsuario.text.toString()

        // Mostra um Toast se o nome de usuário estiver vazio
        if (user.isEmpty()) {
            Toast.makeText(this, "Por favor, digite o nome do usuário.", Toast.LENGTH_SHORT).show()
            return
        }

        githubApi.getAllRepositoriesByUser(user).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { listRepos ->
                        setupAdapter(listRepos) // Chama o Adapter com os dados
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Usuário não encontrado ou erro na API.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        /*
             7 - Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */

        val adapter = RepositoryAdapter(list)

        // Configuração dos Listeners do Adapter ( 11 e 12)
        adapter.carItemLister = { repository ->
            openBrowser(repository.htmlUrl) //  12 - Abrir o browser no click do item
        }
        adapter.btnShareLister = { repository ->
            shareRepositoryLink(repository.htmlUrl) //  11 - Compartilhar link no click do share
        }

        listaRepositories.adapter = adapter
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @Todo 11 - Colocar esse metodo no click do share item do adapter
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    // @Todo 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}