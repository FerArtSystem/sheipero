package prof.ronny.contatos.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import prof.ronny.contatos.data.ApiClient
import prof.ronny.contatos.viewmodel.ContatosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContatoTela(
    vm: ContatosViewModel,
    id: String?,
    voltar: () -> Unit
) {
    var confirmar by remember { mutableStateOf(false) }

    val resolver = LocalContext.current.contentResolver
    val seletor = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> vm.selecionarFoto(uri) }

    LaunchedEffect(id) { vm.abrir(id) }
    LaunchedEffect(vm.concluido) {
        if (vm.concluido) {
            vm.consumirConclusao()
            vm.fecharEditor()
            voltar()
        }
    }
    val sair = {
        vm.fecharEditor()
        voltar()
    }
    BackHandler { if (!vm.ocupado) sair() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (id == null) "Novo Parceiro Sheipero" else "Editar Parceiro",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = sair, enabled = !vm.ocupado) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    val habilitado = vm.pronto && !vm.ocupado
                    IconButton(
                        onClick = { vm.salvar(resolver) },
                        enabled = habilitado
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Salvar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (vm.ocupado) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            vm.mensagem?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (!vm.pronto && !vm.ocupado) {
                Button(onClick = {
                    vm.fecharEditor()
                    vm.abrir(id)
                }) {
                    Text("Tentar novamente")
                }
            }

            // Logotipo ou Foto da Fachada
            val imagem = vm.fotoLocal
                ?: vm.fotoId?.let { "${ApiClient.BASE_URL}upload/$it" }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (imagem != null) {
                    AsyncImage(
                        model = imagem,
                        contentDescription = "Logo do parceiro",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.large)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        seletor.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = vm.pronto && !vm.ocupado
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Selecionar Logo / Foto")
                }
                if (imagem != null) {
                    TextButton(
                        onClick = { vm.removerFoto() },
                        enabled = vm.pronto && !vm.ocupado,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Remover")
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val habilitado = vm.pronto && !vm.ocupado
            Campo(
                rotulo = "Nome do Estabelecimento / Parceiro",
                valor = vm.nome,
                mudar = { vm.nome = it },
                habilitado = habilitado,
                senha = false,
                icone = Icons.Default.Store,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
            Campo(
                rotulo = "E-mail Comercial",
                valor = vm.email,
                mudar = { vm.email = it },
                habilitado = habilitado,
                senha = false,
                icone = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            Campo(
                rotulo = "Telefone / WhatsApp",
                valor = vm.telefone,
                mudar = { vm.telefone = it },
                habilitado = habilitado,
                senha = false,
                icone = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
            Campo(
                rotulo = "Endereço Completo (Rua, Bairro, Cidade)",
                valor = vm.endereco,
                mudar = { vm.endereco = it },
                habilitado = habilitado,
                senha = false,
                icone = Icons.Default.Place,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                onImeAction = { if (habilitado) vm.salvar(resolver) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { vm.salvar(resolver) },
                enabled = habilitado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "SALVAR PARCEIRO",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (id != null) {
                OutlinedButton(
                    onClick = { confirmar = true },
                    enabled = habilitado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Excluir Parceiro")
                }
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("Excluir parceiro") },
            text = { Text("Deseja realmente remover este parceiro da rede Sheipero? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmar = false
                        vm.excluir()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
