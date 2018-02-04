import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


class GraphCycleDetection<T> implements Iterable<T> {

	/* A map from nodes in the graph to sets of outgoing edges.  Each
     * set of edges is represented by a map from edges to doubles.
     */
	private final Map<T, Map<T, Double>> graph = new HashMap<T, Map<T, Double>>();

	/**
	 * 	Adds a new node to the graph. If the node already exists then its a
	 *  no-op.
	 * 
	 * @param node  Adds to a graph. If node is null then this is a no-op.
	 * @return		true if node is added, false otherwise.
	 */
	public boolean addNode(T node) {
		if (node == null) {
			throw new NullPointerException("The input node cannot be null.");
		}
		if (graph.containsKey(node)) return false;

		graph.put(node, new HashMap<T, Double>());
		return true;
	}

//	/**
//	 * This is not a good idea, since removing one node would
//	 * mean changing the edges of the dependent nodes.
//	 */
//	public void removeNode (T node) {
//		
//	}

	/**
	 * Given the source and destination node it would add an arc from source 
	 * to destination node. If an arc already exists then the value would be 
	 * updated the new value.
	 * 	
	 * @param source					the source node.
	 * @param destination				the destination node.
	 * @param length					if length if 
	 * @throws NullPointerException		if source or destination is null.
	 * @throws NoSuchElementException	if either source of destination does not exists. 
	 */
	public void addEdge (T source, T destination, double length) {
		if (source == null || destination == null) {
			throw new NullPointerException("Source and Destination, both should be non-null.");
		}
		if (!graph.containsKey(source) || !graph.containsKey(destination)) {
			throw new NoSuchElementException("Source and Destination, both should be part of graph");
		}
		/* A node would always be added so no point returning true or false */
		graph.get(source).put(destination, length);
	}

	/**
	 * Removes an edge from the graph.
	 * 
	 * @param source		If the source node.
	 * @param destination	If the destination node.
	 * @throws NullPointerException		if either source or destination specified is null
	 * @throws NoSuchElementException	if graph does not contain either source or destination
	 */
	public void removeEdge (T source, T destination) {
		if (source == null || destination == null) {
			throw new NullPointerException("Source and Destination, both should be non-null.");
		}
		if (!graph.containsKey(source) || !graph.containsKey(destination)) {
			throw new NoSuchElementException("Source and Destination, both should be part of graph");
		}
		graph.get(source).remove(destination);
	}

	/**
	 * Given a node, returns the edges going outward that node,
	 * as an immutable map.
	 * 
	 * @param node The node whose edges should be queried.
	 * @return An immutable view of the edges leaving that node.
	 * @throws NullPointerException   If input node is null.
	 * @throws NoSuchElementException If node is not in graph.
	 */
	public Map<T, Double> edgesFrom(T node) {
		if (node == null) {
			throw new NullPointerException("The node should not be null.");
		}
		Map<T, Double> edges = graph.get(node);
		if (edges == null) {
			throw new NoSuchElementException("Source node does not exist.");
		}
		return Collections.unmodifiableMap(edges);
	}

	/**
	 * Returns the iterator that travels the nodes of a graph.
	 * 
	 * @return an iterator that travels the nodes of a graph.
	 */
	@Override public Iterator<T> iterator() {
		return graph.keySet().iterator();
	}
}


/**
 * Qualified example:
 * https://bitbucket.org/ameyapatil/all-images/commits/3f19c1697d6486563d29bd1dabf2d802c375ec09
 */

/**
 * References:
 * http://codereview.stackexchange.com/questions/38063/check-if-directed-graph-contains-a-cycle
 * http://en.wikipedia.org/wiki/Topological_sorting
 * http://stackoverflow.com/questions/583876/how-do-i-check-if-a-directed-graph-is-acyclic
 * http://www.keithschwarz.com/interesting/code/topological-sort/TopologicalSort.java.html
 * http://stackoverflow.com/questions/6850357/explanation-of-runtimes-of-bfs-and-dfs
 * http://codereview.stackexchange.com/questions/38063/check-if-directed-graph-contains-a-cycle
 * 
 * DIAGRAM:
 * https://bitbucket.org/ameyapatil/all-images/commits/3f19c1697d6486563d29bd1dabf2d802c375ec09
 * 
 * 
 * We are using the same method, that is used to return topological sort.
 * Topological sort can be done using 2 approaches:
 * 1. Use dfs							 (which we will use)
 * 2. Chose dude with no incoming edges.
 * 
 * Complexity:
 * O (V + E)
 * http://stackoverflow.com/questions/6850357/explanation-of-runtimes-of-bfs-and-dfs
 * 
 * BB: 12
 * 
 * @author SERVICE-NOW\ameya.patil
 */
public final class DAGCycleDetection {
	
	private DAGCycleDetection() { }
	
	/** 
	 * Returns true if graph contains a cycle else returns false
	 * 
	 * @param graph  the graph to check for cycle
	 * @return true if cycle exists, else false.
	 */
	public static <T> boolean cycle(GraphCycleDetection<T> graph) {
		final Set<T> visitedNodes = new HashSet<T>();
		final Set<T> completedNodes = new HashSet<T>();
			
		for (T node : graph) {
			if (dfs(graph, node, visitedNodes, completedNodes)) return true;
		}
		return false;
	}
	
	/**	
	 * Returns true if graph contains a cycle else returns false
	 * 
	 * 
	 * @param graph	 		     the graph, which should be checked for cycles
	 * @param node			     the current node whose edges should be traversed.
	 * @param visitedNodes		 the nodes visited so far.	
	 * @return					 true if graph contains a cycle else return false;
	 */
	private static <T> boolean dfs (GraphCycleDetection<T> graph, 
									T node, 
									Set<T> visitedNodes,
									Set<T> completedNodes
									) {
		
		if (visitedNodes.contains(node)) {
			if (completedNodes.contains(node)) return false;
			return true;
		}
		
		visitedNodes.add(node); // constitues O(1) for each vertex

		for (Entry<T, Double> entry : graph.edgesFrom(node).entrySet()) {
			if (dfs(graph, entry.getKey(), visitedNodes, completedNodes)) return true;
		}

		completedNodes.add(node);

		return false;
	}

	public static void main(String[] args) {
		GraphCycleDetection<Integer> gcd1 = new GraphCycleDetection<Integer>();
		gcd1.addNode(1); gcd1.addNode(2); gcd1.addNode(3);
		gcd1.addEdge(1, 2, 10); gcd1.addEdge(2, 3, 10); gcd1.addEdge(1, 3, 10);
		assertFalse(cycle(gcd1));
		
		GraphCycleDetection<Integer> gcd2 = new GraphCycleDetection<Integer>();
		gcd2.addNode(1); gcd2.addNode(2); gcd2.addNode(3);
		gcd2.addEdge(1, 2, 10); gcd2.addEdge(2, 3, 10); gcd2.addEdge(3, 1, 10);
		assertTrue(cycle(gcd2));
		
		GraphCycleDetection<Integer> gcd3 = new GraphCycleDetection<Integer>();
		gcd3.addNode(1); gcd3.addNode(2); gcd3.addNode(3); gcd3.addNode(4); gcd3.addNode(5); 
		gcd3.addEdge(1, 2, 10); gcd3.addEdge(2, 3, 10); gcd3.addEdge(2, 4, 10); gcd3.addEdge(3, 4, 10); gcd3.addEdge(4, 5, 10);
		assertFalse(cycle(gcd3));
		
		GraphCycleDetection<Integer> gcd4 = new GraphCycleDetection<Integer>();
		gcd4.addNode(1); gcd4.addNode(2); gcd4.addNode(3); gcd4.addNode(4); gcd4.addNode(5); 
		gcd4.addEdge(1, 2, 10); gcd4.addEdge(2, 3, 10); gcd4.addEdge(2, 4, 10); gcd4.addEdge(3, 4, 10); gcd4.addEdge(4, 5, 10);  gcd4.addEdge(5, 2, 10);
		assertTrue(cycle(gcd4));
		
		// disconnected graph.
		GraphCycleDetection<Integer> gcd5 = new GraphCycleDetection<Integer>();
		gcd5.addNode(1); gcd5.addNode(2); gcd5.addNode(3); gcd5.addNode(10); gcd5.addNode(11);
		gcd5.addEdge(1, 2, 10); gcd5.addEdge(2, 3, 10); gcd5.addEdge(3, 1, 10); gcd5.addEdge(10, 11, 10); 
		assertTrue(cycle(gcd5));
	}
}
