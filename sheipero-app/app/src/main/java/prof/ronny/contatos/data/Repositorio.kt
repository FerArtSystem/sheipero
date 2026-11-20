package prof.ronny.contatos.data

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.JsonParser
import retrofit2.Response

object Repositorio {
    private val api get() = ApiClient.api

    suspend fun registrar(dados: RegistroEnvio) =
        ler { api.registrar(dados) }

    private suspend fun <T> ler(
        chamada: suspend () -> Response<T>
    ): T {
        val autenticado = Sessao.token != null
        val resposta = chamada()
        if (!resposta.isSuccessful) {
            val texto = resposta.errorBody()?.string()
            val mensagem = runCatching {
                val json = JsonParser.parseString(texto).asJsonObject
                (json["mensagem"] ?: json["erro"])?.asString
            }.getOrNull() ?: "Erro HTTP ${resposta.code()}"
            if (autenticado && resposta.code() in listOf(401, 403)) {
                Sessao.token = null
                error("Sessão encerrada. Faça login novamente.")
            }
            error(mensagem)
        }
        return resposta.body() ?: error("Resposta vazia da API")
    }

    suspend fun login(dados: LoginEnvio) = ler { api.login(dados) }

    suspend fun listar(): List<Contato> = ler { api.listar() }
    suspend fun listarParceiros(): List<Parceiro> = listar()

    suspend fun criar(dados: ContatoEnvio): Contato {
        val resposta = ler { api.criar(dados) }
        return resposta.contato ?: error("A API não retornou o parceiro criado")
    }
    suspend fun criarParceiro(dados: ParceiroEnvio): Parceiro = criar(dados)

    suspend fun buscar(id: String): Contato = ler { api.buscar(id) }
    suspend fun buscarParceiro(id: String): Parceiro = buscar(id)

    suspend fun atualizar(id: String, dados: ContatoEnvio): Contato =
        ler { api.atualizar(id, dados) }
    suspend fun atualizarParceiro(id: String, dados: ParceiroEnvio): Parceiro =
        atualizar(id, dados)

    suspend fun excluir(id: String) = ler { api.excluir(id) }
    suspend fun excluirParceiro(id: String) = excluir(id)

    suspend fun enviarFoto(resolver: ContentResolver, uri: Uri): String {
        val parte = prepararFoto(resolver, uri)
        return ler { api.enviarFoto(parte) }.fileId
            ?.takeIf { it.isNotBlank() }
            ?: error("A API não retornou fileId")
    }
}
