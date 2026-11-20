package prof.ronny.contatos.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.*
import prof.ronny.contatos.data.*

class ContatosViewModel : OperacaoViewModel() {
    var contatos by mutableStateOf<List<Parceiro>>(emptyList())
        private set

    val parceiros: List<Parceiro> get() = contatos

    var nome by mutableStateOf("")
    var email by mutableStateOf("")
    var telefone by mutableStateOf("")
    var endereco by mutableStateOf("")
    var fotoId by mutableStateOf<String?>(null)
        private set
    var pronto by mutableStateOf(false)
        private set
    var concluido by mutableStateOf(false)
        private set
    private var inicializado = false
    private var idAtual: String? = null

    fun listar() = executar { contatos = Repositorio.listarParceiros() }

    fun abrir(id: String?) {
        if (inicializado && idAtual == id) return
        inicializado = true
        idAtual = id
        nome = ""; email = ""; telefone = ""; endereco = ""
        fotoId = null
        fotoLocal = null
        pronto = id == null
        concluido = false
        limparMensagem()
        if (id != null) executar {
            val parceiro = Repositorio.buscarParceiro(id)
            nome = parceiro.nome
            email = parceiro.email.orEmpty()
            telefone = parceiro.telefone.orEmpty()
            endereco = parceiro.endereco.orEmpty()
            fotoId = parceiro.fotoId
            pronto = true
        }
    }

    fun salvar(resolver: ContentResolver) {
        if (!pronto) return
        if (nome.isBlank()) {
            mensagem = "Informe o nome do parceiro ou estabelecimento"
            return
        }
        executar {
            val uri = fotoLocal
            if (uri != null) {
                fotoId = Repositorio.enviarFoto(resolver, uri)
                fotoLocal = null
            }
            val dados = ParceiroEnvio(
                nome.trim(), email.trim(), telefone.trim(), endereco.trim(), fotoId
            )
            val id = idAtual
            if (id == null) Repositorio.criarParceiro(dados)
            else Repositorio.atualizarParceiro(id, dados)
            concluido = true
        }
    }

    fun excluir() {
        val id = idAtual ?: return
        executar {
            Repositorio.excluirParceiro(id)
            concluido = true
        }
    }

    fun fecharEditor() {
        cancelar()
        inicializado = false
        pronto = false
        concluido = false
    }

    fun consumirConclusao() { concluido = false }

    fun limpar() {
        fecharEditor()
        contatos = emptyList()
        nome = ""; email = ""; telefone = ""; endereco = ""
        fotoId = null
        fotoLocal = null
    }

    var fotoLocal by mutableStateOf<Uri?>(null)
        private set

    fun selecionarFoto(uri: Uri?) {
        if (uri != null && !ocupado) fotoLocal = uri
    }

    fun removerFoto() {
        fotoLocal = null
        fotoId = null
    }
}
