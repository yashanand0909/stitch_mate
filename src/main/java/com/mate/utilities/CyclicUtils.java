package com.mate.utilities;

import com.mate.repositories.projections.EdgeList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/** Created by Alokh on 02/08/2022 */
@Slf4j
public class CyclicUtils {
  public static boolean containsCycle(List<EdgeList> edgeList, Long toNode, Long fromNode) {
    log.info("running cyclic validation on nodes");
    // form adjacency list from provided edges
    Map<Long, ArrayList<Long>> adjacencyList = getAdjacencyList(edgeList);
    // form list of all the nodes present in the edges
    List<Long> nodes =
        Stream.concat(
                edgeList.stream().map(EdgeList::getFromNode),
                edgeList.stream().map(EdgeList::getToNode))
            .distinct()
            .collect(Collectors.toList());
    if (!nodes.contains(toNode)) nodes.add(toNode);
    if (!nodes.contains(fromNode)) nodes.add(fromNode);

    // Mark all the vertices as not visited and
    // not part of recursion stack
    Map<Long, Boolean> visited = nodes.stream().collect(Collectors.toMap(r -> r, r -> false));

    return isCyclicUtil(toNode, visited, fromNode, adjacencyList);
  }

  private static boolean isCyclicUtil(
      Long currentNode,
      Map<Long, Boolean> visited,
      Long fromNode,
      Map<Long, ArrayList<Long>> adjacencyList) {

    // if we find the fromNode there is a cycle
    if (Objects.equals(currentNode, fromNode)) return true;
    // if this node was already covered as part of another node's traversal, there are no cycles
    if (Boolean.TRUE.equals(visited.get(currentNode))) return false;

    // Mark the current node as visited
    visited.put(currentNode, true);
    // check if the node is a terminal node by checking entry in adjacency list
    if (adjacencyList.containsKey(currentNode)) {
      List<Long> children = adjacencyList.get(currentNode);
      // call the recursive function for all the adjacent nodes
      for (Long c : children) {
        if (isCyclicUtil(c, visited, fromNode, adjacencyList)) {
          log.info("job does contains cyclic relations");
          return true;
        }
      }
    }
    log.info("job does not contain cyclic relations");
    return false;
  }

  private static Map<Long, ArrayList<Long>> getAdjacencyList(List<EdgeList> edgeList) {
    log.info("getting adjacency list of nodes and edges relations");
    Map<Long, ArrayList<Long>> result = new HashMap<>();
    for (EdgeList edge : edgeList) {
      if (result.containsKey(edge.getFromNode())) {
        result.get(edge.getFromNode()).add(edge.getToNode());
      } else {
        ArrayList<Long> integerArrayList = new ArrayList<>();
        integerArrayList.add(edge.getToNode());
        result.put(edge.getFromNode(), integerArrayList);
      }
    }
    return result;
  }
}
