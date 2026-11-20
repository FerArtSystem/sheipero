package prof.ronny.contatos.viewmodel

import prof.ronny.contatos.data.*
import androidx.compose.runtime.*

class AuthViewModel : OperacaoViewModel() {

    var entrou by mutableStateOf(false)
        private set

    fun registrar(nome: String, email: String, senha: String) {
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            mensagem = "Preencha nome, e-mail comercial e senha"
            return
        }
        executar {
            val resposta = Repositorio.registrar(
                RegistroEnvio(nome.trim(), email.trim(), senha)
            )
            mensagem = resposta.mensagem ?: "Conta de parceiro criada com sucesso!"
        }
    }

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            mensagem = "Preencha e-mail comercial e senha"
            return
        }
        executar {
            val resposta = Repositorio.login(LoginEnvio(email.trim(), senha))
            val token = resposta.token?.takeIf { it.isNotBlank() }
                ?: error("A API não retornou um token válido")
            Sessao.token = token
            entrou = true
        }
    }

    fun consumirEntrada() { entrou = false }

    fun sair() {
        cancelar()
        entrou = false
        Sessao.token = null
    }

}
