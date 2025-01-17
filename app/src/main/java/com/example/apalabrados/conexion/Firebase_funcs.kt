package com.example.apalabrados.conexion


import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.apalabrados.model.Partida
import com.example.apalabrados.model.Pregunta
import com.example.apalabrados.mvvm.ViewModel
import com.example.apalabrados.navegacion.PantallasJugar
import com.example.apalabrados.session.Session
import com.example.apalabrados.ui.theme.AzulClarito
import com.example.apalabrados.ui.theme.AzulFondo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun aniadirPreguntaButton(ViewModel: ViewModel) {
    val context = LocalContext.current

    val campo by ViewModel.campo.observeAsState(initial = "")
    val respuesta by ViewModel.respuesta.observeAsState(initial = "")


    val pregunta by ViewModel.pregunta.observeAsState(initial = "")
    val respuesta1 by ViewModel.respuesta1.observeAsState(initial = "")
    val respuesta2 by ViewModel.respuesta2.observeAsState(initial = "")
    val respuesta3 by ViewModel.respuesta3.observeAsState(initial = "")

    Button(
        onClick = {
            if (campo.isEmpty()) {
                Toast
                    .makeText(context, "campo en blanco", Toast.LENGTH_LONG)
                    .show()
            } else if (pregunta.isEmpty()) {
                Toast
                    .makeText(context, "respuesta1 en blanco", Toast.LENGTH_LONG)
                    .show()
            } else if (respuesta1.isEmpty()) {
                Toast
                    .makeText(context, "respuesta1 en blanco", Toast.LENGTH_LONG)
                    .show()
            } else if (respuesta2.isEmpty()) {
                Toast
                    .makeText(context, "respuesta2 en blanco", Toast.LENGTH_LONG)
                    .show()
            } else if (respuesta3.isEmpty()) {
                Toast
                    .makeText(context, "respuesta3 en blanco", Toast.LENGTH_LONG)
                    .show()
            } else if (respuesta.isEmpty()) {
                Toast
                    .makeText(context, "Respuesta correcta en blanco", Toast.LENGTH_LONG)
                    .show()
            } else {
                var correcta = ""
                if (respuesta == "respuesta1") {
                    correcta = respuesta1
                } else if (respuesta == "respuesta2") {
                    correcta = respuesta2
                } else if (respuesta == "respuesta3") {
                    correcta = respuesta3
                }

                db
                    .collection(campo)
                    .document()
                    .set(
                        mapOf(
                            "pregunta" to pregunta,
                            "respuesta1" to respuesta1,
                            "respuesta2" to respuesta2,
                            "respuesta3" to respuesta3,
                            "correcta" to correcta
                        )
                    )
                    .addOnSuccessListener {
                        ViewModel.limpiarCampos()
                        Toast
                            .makeText(context, "Añadido correctamente", Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast
                            .makeText(context, "No se ha podido añadir", Toast.LENGTH_LONG)
                            .show()
                    }
            }
            println(ViewModel.respuesta.value)
            println(ViewModel.campo.value)
            println("alijdlisajdliajds")
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AzulClarito,
            contentColor = AzulFondo
        )
    ) {
        Text(
            text = "Añadir Pregunta",
            fontSize = 26.sp,

            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

//Con esta función se crea una partida
@Composable
fun aniadirPartida(jugador1: String, ViewModel: ViewModel, navController: NavController) {
    val context = LocalContext.current

    Button(
        border = BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
        onClick = {
            val partidaData = hashMapOf(
                "partida" to 1,
                "turno" to 1,
                "subturno" to 1,
                "logrosj1" to 0,
                "logrosj2" to 0,
                "j1" to jugador1,
                "j2" to "",
                "ganador" to "",
                "codigo" to ViewModel.codigoSala.value
            )

            GlobalScope.launch(Dispatchers.Main) {
                val partidaEncontrada =
                    ViewModel.codigoSala.value?.let { buscarPartidaPorCodigo(it) }
                if (partidaEncontrada == true) {
                    Toast
                        .makeText(
                            context,
                            "Codigo de sala ya existe, inserte otro, por favor",
                            Toast.LENGTH_LONG
                        )
                        .show()
                } else {
                    ViewModel.codigoSala.value?.let {
                        db
                            .collection("partida")
                            .document(it)
                            .set(partidaData)
                            .addOnSuccessListener {
                                ViewModel.limpiarCampos()
                                Toast
                                    .makeText(context, "Añadido correctamente", Toast.LENGTH_LONG)
                                    .show()
                                navController.navigate(route = PantallasJugar.SalaDeEspera.route + "/" + ViewModel.codigoSala.value)
                                ViewModel.limpiarCodigoSala()
                            }
                            .addOnFailureListener {
                                Toast
                                    .makeText(context, "No se ha podido añadir", Toast.LENGTH_LONG)
                                    .show()
                            }
                    }
                }
            }
        }) {
        Text(text = "Crear partida")
    }

}

//Se llama a esta función para buscar si está libre ese código
suspend fun buscarPartidaPorCodigo(codigoSala: String): Boolean {
    val coleccion = db.collection("partida")
    val consulta = coleccion.whereEqualTo("codigo", codigoSala)

    return try {
        val documentos = consulta.get().await()
        !documentos.isEmpty
    } catch (excepcion: Exception) {
        false
    }
}
/*
@Composable
fun buscarJugadorLibre(nombre: String) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val querySnapshot = db.collection("partida")
            .whereEqualTo("J2", "")
            .limit(1)
            .get()
            .await()
        if (!querySnapshot.isEmpty) {
            // Hay al menos un jugador2 libre
            val docSnapshot = querySnapshot.documents.firstOrNull()
            if (docSnapshot != null) {
                // Actualizamos el documento para añadir a Pedro como jugador 2
                val docId = docSnapshot.id
                db.collection("partida").document(docId)
                    .update("J2", nombre)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Unido a partida correctamente", Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "No se ha podido unir a la partida", Toast.LENGTH_LONG)
                            .show()
                    }
            }
        } else {
            // No hay ningún jugador2 libre
            Toast.makeText(context, "No hay partidas disponibles", Toast.LENGTH_LONG)
                .show()
        }
    }
}*/

suspend fun buscarPartidaPorCodigoUnirse(
    user: String,
    ViewModel: ViewModel,
    navController: NavController,
    codigoSala: String,
) {
    val coleccion = db.collection("partida")
    val query = coleccion.whereEqualTo("codigo", codigoSala)

    val userj2 = buscarJugadorPartida(codigoSala, "j2")

    if (userj2!!.isEmpty()) {

        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val partidaId = document.id
                val partida = document.toObject(Partida::class.java)
                partida.j2 = user
                coleccion.document(partidaId).set(partida)
                navController.navigate(route = PantallasJugar.SalaDeEspera.route + "/" + ViewModel.codigoSalaUnirse.value)
                ViewModel.limpiarCodigoSalaUnirse()
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error al obtener las partidas", exception)
        }
    }
}

fun buscarUsuarioReference(nick: String, onComplete: (Boolean) -> Unit) {
    val usersCollection = db.collection("usuarios")
    val query = usersCollection.whereEqualTo("usuario", nick)

    query.get().addOnSuccessListener { querySnapshot ->
        if (!querySnapshot.isEmpty) {
            onComplete(true) // El documento ya contiene el nick proporcionado
        } else {
            onComplete(false) // El documento no contiene el nick proporcionado
        }
    }.addOnFailureListener { exception ->
        onComplete(false) // Error al obtener el documento
    }
}

fun verSiExisteUsuario(usuario: String, contrasenia: String, callback: (Boolean) -> Unit) {
    // Consulta en la base de datos si existe un usuario con el nombre y la contraseña proporcionados
    db.collection("usuarios")
        .whereEqualTo("usuario", usuario)
        .whereEqualTo("password", contrasenia)
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                if (!result?.isEmpty!!) {
                    callback(true)
                } else {
                    // Si no existe un usuario con el nombre y la contraseña proporcionados, devuelve falso
                    callback(false)
                }
            } else {
                callback(false)
            }
        }
}

fun cogerEmailSessionUsuario(user: String, onComplete: (String?) -> Unit) {
    val coleccion = db.collection("usuarios")
    val consulta = coleccion.whereEqualTo("usuario", user)

    consulta.get().addOnSuccessListener { querySnapshot ->
        if (!querySnapshot.isEmpty) {
            val documento = querySnapshot.documents[0]
            val email = documento.getString("email")
            onComplete(email)
        } else {
            onComplete(null)
        }
    }.addOnFailureListener { exception ->
        println(exception)
        onComplete(null)
    }
}

suspend fun buscarJugadorPartida(codigoSala: String, posicion: String): String? {
    val querySnapshot = db.collection("partida")
        .whereEqualTo("codigo", codigoSala)
        .get()
        .await()

    if (querySnapshot.documents.isNotEmpty()) {
        val document = querySnapshot.documents[0]
        return document.getString(posicion).toString()
    }

    return null
}

suspend fun buscarTurnoOSubturno(codigoSala: String, turnoOsubturno: String): Int? {
    println("Entré función al buscarturno")

    val querySnapshot = db.collection("partida")
        .whereEqualTo("codigo", codigoSala)
        .get()
        .await()



    if (querySnapshot.documents.isNotEmpty()) {

        println("NSIFUNCIONA")

        val document = querySnapshot.documents[0]
        return document.getLong(turnoOsubturno)?.toInt()
    }

    return null
}

suspend fun consultarGanador(codigoSala: String, logrosJugador: String): Int? {
    val querySnapshot = db.collection("partida")
        .whereEqualTo("codigo", codigoSala)
        .get()
        .await()

    if (querySnapshot.documents.isNotEmpty()) {
        val document = querySnapshot.documents[0]
        return document.getLong(logrosJugador)?.toInt()
    }

    return null
}

fun buscarPartidasUsuario(sessionManager: Session): Task<MutableList<Partida>> {

    val j1Query = db.collection("partida").whereEqualTo("j1", sessionManager.getNick())
    val j2Query = db.collection("partida").whereEqualTo("j2", sessionManager.getNick())

    val partidas = mutableListOf<Partida>()

    val task = Tasks.whenAllSuccess<QuerySnapshot>(j1Query.get(), j2Query.get())
        .addOnSuccessListener { querySnapshots ->
            for (querySnapshot in querySnapshots) {
                for (document in querySnapshot) {
                    val c: Partida? = document.toObject(Partida::class.java)
                    if (c?.ganador == "") {
                        c?.let { partidas.add(it) }
                    }
                }
            }
        }.continueWith {
            partidas
        }

    return task
}

fun buscarPreguntas(tema: String): Task<MutableList<Pregunta>> {
    val preguntas = mutableListOf<Pregunta>()
    val query = db.collection(tema)

    val task = query.get().addOnSuccessListener { result ->
        for (document in result) {
            val pregunta: Pregunta? = document.toObject(Pregunta::class.java)
            pregunta?.let { preguntas.add(it) }
            println(pregunta?.respuesta2)
        }
    }.continueWith { task ->
        preguntas
    }

    return task
}

fun jugadorAcerto(codigoSala: String, jugador: String) {
    // Obtener la referencia al documento de la partida
    val partidaRef = db.collection("partida").document(codigoSala)

    // Actualizar el campo correspondiente
    partidaRef.get().addOnSuccessListener { documentSnapshot ->
        val logrosJ1 = documentSnapshot.getLong("logrosJ1") ?: 0
        val logrosJ2 = documentSnapshot.getLong("logrosJ2") ?: 0

        if (jugador == "j1") {
            println("Entré j1")
            partidaRef.update("logrosJ1", logrosJ1 + 1)
        } else if (jugador == "j2") {
            println("Entré j2")
            partidaRef.update("logrosJ2", logrosJ2 + 1)
        }
    }
}

fun jugadorFallo(codigoSala: String) {
    val partidaRef = db.collection("partida").document(codigoSala)

    // Actualizar el campo correspondiente
    partidaRef.get().addOnSuccessListener { documentSnapshot ->
        val subturno = documentSnapshot.getLong("subturno") ?: 0
        partidaRef.update("subturno", subturno + 1)

    }
}

fun aniadirGanador(codigoSala: String, nombre: String) {
    // Obtener la referencia al documento de la partida
    val partidaRef = db.collection("partida").document(codigoSala)

    // Actualizar el campo correspondiente
    partidaRef.get().addOnSuccessListener { documentSnapshot ->
        partidaRef.update("ganador", nombre)
    }
}