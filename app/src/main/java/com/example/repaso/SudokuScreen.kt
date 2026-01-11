package com.example.repaso
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Data class simple
data class Casilla(
    val valor: Int,
    val esFija: Boolean // True si venía con el puzzle, False si la pone el usuario
)

// Sudoku básico
val tableroInicial = listOf(
    5, 3, 0, 0, 7, 0, 0, 0, 0,
    6, 0, 0, 1, 9, 5, 0, 0, 0,
    0, 9, 8, 0, 0, 0, 0, 6, 0,
    8, 0, 0, 0, 6, 0, 0, 0, 3,
    4, 0, 0, 8, 0, 3, 0, 0, 1,
    7, 0, 0, 0, 2, 0, 0, 0, 6,
    0, 6, 0, 0, 0, 0, 2, 8, 0,
    0, 0, 0, 4, 1, 9, 0, 0, 5,
    0, 0, 0, 0, 8, 0, 0, 7, 9
).map { Casilla(it, it != 0) } // Convertimos los ints a objetos Casilla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuApp() {
    // Lista completa de casillas. Usamos toMutableStateList para que la UI se actualice al cambiar datos.
    val tablero = remember { tableroInicial.toMutableStateList() }

    // Variable que indica la casilla seleccionada / -1 es ninguna
    var indiceSeleccionado by remember { mutableStateOf(-1) }

    Scaffold(
        // Barra superior
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sudoku Repaso") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- TABLERO ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(9), // 9 Columnas obligatorias
                modifier = Modifier.padding(8.dp).weight(1f) // Ocupa el espacio disponible
            ) {
                // items funciona igual que un forEach
                items(tablero.size) { indice ->
                    val casilla = tablero[indice]

                    // Dibujamos cada celda
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f) // Hace que sea cuadrada
                            .border(1.dp, Color.Black)
                            .background(
                                // Cambia color si está seleccionada
                                if (indice == indiceSeleccionado) Color.Cyan.copy(alpha = 0.3f)
                                else Color.White
                            )
                            .clickable {
                                // Al tocar, guardamos qué índice hemos tocado
                                indiceSeleccionado = indice
                            }
                    ) {
                        // Solo mostramos el número si no es 0
                        if (casilla.valor != 0) {
                            Text(
                                text = casilla.valor.toString(),
                                fontSize = 18.sp,
                                fontWeight = if (casilla.esFija) FontWeight.Bold else FontWeight.Normal,
                                color = if (casilla.esFija) Color.Black else Color.Blue
                            )
                        }
                    }
                }
            }

            // --- BOTONES ---
            // Solo mostramos los botones si hay una casilla seleccionada que NO sea fija
            if (indiceSeleccionado != -1 && !tablero[indiceSeleccionado].esFija) {
                Text("Selecciona un número:", modifier = Modifier.padding(8.dp))

                // Una fila de botones del 1 al 9
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..9).forEach { numero ->
                        Button(
                            onClick = {
                                // LÓGICA DE ACTUALIZACIÓN
                                // Copiamos la casilla antigua pero con el nuevo valor
                                val nuevaCasilla = tablero[indiceSeleccionado].copy(valor = numero)
                                // Actualizamos la lista (esto dispara la recomposición y actualiza la pantalla)
                                tablero[indiceSeleccionado] = nuevaCasilla
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(35.dp) // Botones pequeños
                        ) {
                            Text(text = numero.toString())
                        }
                    }
                }

                // Botón para borrar
                Button(onClick = {
                    tablero[indiceSeleccionado] = tablero[indiceSeleccionado].copy(valor = 0)
                }) {
                    Text("Borrar")
                }
            } else if (indiceSeleccionado != -1) {
                Text("Esta casilla no se puede cambiar", color = Color.Red, modifier = Modifier.padding(16.dp))
            }
        }
    }
}