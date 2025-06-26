package com.telecomapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.telecomapp.domain.Graph
import com.telecomapp.service.DistanceFileLoader
import com.telecomapp.service.MstService
import com.telecomapp.scala.MstService as ScalaMstService
import com.telecomapp.scala.FunctionalMstService

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Telecoms Application") {
        MaterialTheme {
            TelecomsApp()
        }
    }
}

@Composable
fun TelecomsApp() {
    val graph = remember { mutableStateOf(Graph()) }
    val totalDistance = remember { mutableStateOf(0.0) }
    val mstEdges = remember { mutableStateOf(listOf<com.telecomapp.domain.Edge>()) }
    val algorithm = remember { mutableStateOf("Kotlin") }
    val expanded = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("European Capitals Network Planner", style = MaterialTheme.typography.h5)
        
        // Algorithm selection
        Button(onClick = { expanded.value = true }) {
            Text("Algorithm: ${algorithm.value}")
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            DropdownMenuItem(onClick = {
                algorithm.value = "Kotlin"
                expanded.value = false
            }) {
                Text("Kotlin")
            }
            DropdownMenuItem(onClick = {
                algorithm.value = "Scala"
                expanded.value = false
            }) {
                Text("Scala")
            }
            DropdownMenuItem(onClick = {
                algorithm.value = "Functional"
                expanded.value = false
            }) {
                Text("Functional")
            }
        }
        
        Button(
            onClick = {
                val filePath = "capitals_distances.csv"
                val loader = DistanceFileLoader()
                loader.loadDistances(filePath, graph.value)
                
                val (edges, distance) = when (algorithm.value) {
                    "Kotlin" -> {
                        val mstService = MstService()
                        mstService.calculateMst(graph.value)
                    }
                    "Scala" -> {
                        ScalaMstService.calculateMst(graph.value)
                    }
                    "Functional" -> {
                        FunctionalMstService.calculateMst(graph.value)
                    }
                    else -> throw IllegalArgumentException("Unknown algorithm")
                }
                
                mstEdges.value = edges
                totalDistance.value = distance
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Load Distances and Calculate MST")
        }
        
        Text("Total cable length: ${totalDistance.value} km", style = MaterialTheme.typography.h6)
        
        // Display MST edges
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text("Minimum Spanning Tree Connections:", style = MaterialTheme.typography.subtitle1)
            mstEdges.value.forEach { edge ->
                Text("${edge.source.name} to ${edge.destination.name}: ${edge.distance} km")
            }
        }
    }
}
