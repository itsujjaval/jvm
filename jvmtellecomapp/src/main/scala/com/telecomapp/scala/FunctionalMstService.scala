package com.telecomapp.scala

import com.telecomapp.domain.{Capital, Edge, Graph}

object FunctionalMstService {
  def calculateMst(graph: Graph): (List[Edge], Double) = {
    val edges = graph.getEdges().sortBy(_.distance)
    
    // We'll use a recursive function to build the MST
    def buildMst(remainingEdges: List[Edge], currentMst: List[Edge], connectedNodes: Set[Capital]): (List[Edge], Set[Capital]) = {
      if (remainingEdges.isEmpty || connectedNodes.size == graph.getCapitals().size) {
        (currentMst, connectedNodes)
      } else {
        // Find the next edge that connects a new node without forming a cycle
        val nextEdgeOption = remainingEdges.find { edge =>
          val connectsNewNode = !connectedNodes(edge.source) || !connectedNodes(edge.destination)
          val formsCycle = connectedNodes(edge.source) && connectedNodes(edge.destination)
          connectsNewNode && !formsCycle
        }
        
        nextEdgeOption match {
          case Some(edge) =>
            val newConnectedNodes = connectedNodes + edge.source + edge.destination
            val newMst = currentMst :+ edge
            val newRemaining = remainingEdges.filterNot(_ == edge)
            buildMst(newRemaining, newMst, newConnectedNodes)
          case None =>
            // No valid edge found, return current MST
            (currentMst, connectedNodes)
        }
      }
    }
    
    // Start with an empty MST and the first capital
    val startCapital = graph.getCapitals().headOption
    val initialConnected = startCapital.map(Set(_)).getOrElse(Set.empty)
    val (mstEdges, _) = buildMst(edges.toList, Nil, initialConnected)
    
    val totalDistance = mstEdges.map(_.distance).sum
    (mstEdges, totalDistance)
  }
}
