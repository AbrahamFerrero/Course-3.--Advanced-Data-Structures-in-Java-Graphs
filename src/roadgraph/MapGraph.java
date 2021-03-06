/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Collections;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and Abraham Ferrero
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph{
	// Maintain both nodes and edges as you will need to
	// be able to look up nodes by lat/lon or by roads
	// that contain those nodes.
	private HashMap<GeographicPoint,MapNode> pointNodeMap;
	private HashSet<MapEdge> edges;
	
	/** 
	 * Number of roadworks:
	 */
	public static final int ROADWORKS = 2;
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		pointNodeMap = new HashMap<GeographicPoint,MapNode>();
		edges = new HashSet<MapEdge>();
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		return pointNodeMap.values().size();
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		return pointNodeMap.keySet();
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		return edges.size();
	}

	/*TODO week6: Method that takes the main(longer) roads and changes the boolean Roadworks to true. 
	 * For example, SetWorks(2) sets works on the 2 longest roads of the map. If the roadworks inserted are
	 * bigger than the number of streets, it lets you know.
	 */
	public void setWorks(int numOfWorks) {
		int doubleDirection = numOfWorks*2;
		List<MapEdge> sortedList = new ArrayList<MapEdge>(edges);
		Collections.sort(sortedList);
		if(doubleDirection<= sortedList.size()){
			for(int i=0;i<doubleDirection; i++) {
				MapEdge edgeWithWorks = sortedList.get(i);
				edgeWithWorks.setRoadWorkstoTrue();
			}
		}
		else {
			System.out.println("Roadworks can't be bigger than the actual number of streets");
		}
	}
	
	//borrar
	public void printedges() {
		for (MapEdge g : edges) {
			System.out.println(g);
		}
	}
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		if (location == null) {
			return false;
		}
		MapNode n = pointNodeMap.get(location);
		if (n == null) {
			n = new MapNode(location);
			pointNodeMap.put(location, n);
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {

		MapNode n1 = pointNodeMap.get(from);
		MapNode n2 = pointNodeMap.get(to);

		// check nodes are valid
		if (n1 == null)
			throw new NullPointerException("addEdge: pt1:"+from+"is not in graph");
		if (n2 == null)
			throw new NullPointerException("addEdge: pt2:"+to+"is not in graph");

		MapEdge edge = new MapEdge(roadName, roadType, n1, n2, length);
		edges.add(edge);
		n1.addEdge(edge);
		
	}
		
	/** 
	 * Get a set of neighbor nodes from a mapNode
	 * @param node  The node to get the neighbors from
	 * @return A set containing the MapNode objects that are the neighbors 
	 * 	of node
	 */
	private Set<MapNode> getNeighbors(MapNode node) {
		return node.getNeighbors();
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, 
			 					     Consumer<GeographicPoint> nodeSearched)
	{
		/* Note that this method is a little long and we might think
		 * about refactoring it to break it into shorter methods as we 
		 * did in the Maze search code in week 2 */
		// Setup - check validity of inputs
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null) {
			System.err.println("Start node " + start + " does not exist");
			return null;
		}
		if (endNode == null) {
			System.err.println("End node " + goal + " does not exist");
			return null;
		}

		// setup to begin BFS
		HashMap<MapNode,MapNode> parentMap = new HashMap<MapNode,MapNode>();
		Queue<MapNode> toExplore = new LinkedList<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		toExplore.add(startNode);
		MapNode next = null;

		while (!toExplore.isEmpty()) {
			next = toExplore.remove();
			
			 // hook for visualization
			nodeSearched.accept(next.getLocation());
			
			if (next.equals(endNode)) break;
			Set<MapNode> neighbors = getNeighbors(next);
			for (MapNode neighbor : neighbors) {
				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					parentMap.put(neighbor, next);
					toExplore.add(neighbor);
				}
			}
		}
		if (!next.equals(endNode)) {
			System.out.println("No path found from " +start+ " to " + goal);
			return null;
		}
		// Reconstruct the parent path
		List<GeographicPoint> path =
				reconstructPath(parentMap, startNode, endNode);

		return path;
	
	}
	


	/** Reconstruct a path from start to goal using the parentMap
	 *
	 * @param parentMap the HashNode map of children and their parents
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from
	 *   start to goal (including both start and goal).
	 */
	private List<GeographicPoint>
	reconstructPath(HashMap<MapNode,MapNode> parentMap,
					MapNode start, MapNode goal)
	{
		LinkedList<GeographicPoint> path = new LinkedList<GeographicPoint>();
		MapNode current = goal;

		while (!current.equals(start)) {
			path.addFirst(current.getLocation());
			current = parentMap.get(current);
		}

		// add start
		path.addFirst(start.getLocation());
		return path;
	}


	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	
	//Helper method that sets distances from start to 0 for the start node and infinite for the rest
	private void setDistanceFromStart(GeographicPoint start,GeographicPoint goal) {
		for(MapNode m : pointNodeMap.values()) {
			GeographicPoint startloc = new GeographicPoint(start.getX(),start.getY());
			if(m.getLocation().equals(startloc)){
				m.setDistanceToZero();
			}
			else {
				m.setDistanceToInf();
			}
		}
	}
	
	public List<GeographicPoint> dijkstraWithWorks(GeographicPoint start, GeographicPoint goal, int numOfWorks) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
		try {
			Consumer<GeographicPoint> temp = (x) -> {
			};
			return dijkstraWithWorks(start, goal, temp, numOfWorks);
		} catch (NullPointerException e) {
			System.out.print("No path found due to roadworks");
			return null;
		}
	}
	
	public List<GeographicPoint> dijkstraWithWorks(GeographicPoint start, 
			  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched, int numOfWorks){
		// TODO: Implement this method in WEEK 6: We just include the method setWorks(numOfWorks), and change the 
		//getNeighbors() method for the getNeighborsWithoutWorks() instead. Easy changes for a brand new search
		int count = 0;
		HashMap<MapNode,MapNode> parentMap = new HashMap<MapNode,MapNode>();
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		GeographicPoint startloc = new GeographicPoint(start.getX(),start.getY());
		setWorks(numOfWorks);
		//Setting distances to 0 and infinite;
		setDistanceFromStart(start,goal);
		//Enqueue S,0 onto the PQ:
		toExplore.add(pointNodeMap.get(startloc));
		while(!toExplore.isEmpty()) {
			MapNode curr = toExplore.poll();
			count++;
			if(!visited.contains(curr)) {
				visited.add(curr);
				if(curr.getLocation().equals(goal)) {
					break; 
				}
				//For each of the currs neighbors, not visited:
				for(MapNode n : curr.getNeighborsWithoutWorks()) {
					if(!visited.contains(n)) {
						//gn is the total distance from start to n (the actual path)
						double gn = curr.getDisFromStart()+curr.getNighDistance(n);
						//If path through curr to n is shorter:
						if(gn<n.getDisFromStart()) {
							n.setDisFromStart(gn);
							parentMap.put(n, curr);
							toExplore.add(n);
						}
					}
				}
			}
		}
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		//We take advantage of our method reconstructPath created for BFS:
		List<GeographicPoint> path =
				reconstructPath(parentMap, startNode, endNode);
		System.out.println("count for dijkstra is:" + count);
		return path;
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearchWithWorks(GeographicPoint start, GeographicPoint goal, int numOfWorks) {
		// Dummy variable for calling the search algorithms with our week6
		// implementation
		try {
			Consumer<GeographicPoint> temp = (x) -> {
			};
			return aStarSearchWithWorks(start, goal, temp, numOfWorks);
		} catch (NullPointerException e) {
			System.out.print("No path found due to roadworks");
			return null;
		}

	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearchWithWorks(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched, int numOfWorks)
	{
		//TODO: week6: A* with roadworks on it. We just include the method setWorks(numOfWorks), and change the 
		//getNeighbors() method for the getNeighborsWithoutWorks() instead. Easy changes for a brand new search
		HashMap<MapNode,MapNode> parentMap = new HashMap<MapNode,MapNode>();
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		GeographicPoint startloc = new GeographicPoint(start.getX(),start.getY());
		setWorks(numOfWorks);
		int count = 0;
		//Setting distances to 0 and infinite;
		setDistanceFromStart(start,goal);
		//Enqueue S,0 onto the PQ:
		toExplore.add(pointNodeMap.get(startloc));
		while(!toExplore.isEmpty()) {
			MapNode curr = toExplore.poll();
			count++;
			if(!visited.contains(curr)) {
				visited.add(curr);
				if(curr.getLocation().equals(goal)) {
					break; 
				}
				//For each of the currs neighbors, not visited:
				MapNode nodeCloserToGoal = curr.getCloseNeighborsWithWorks(goal);
				if(!visited.contains(nodeCloserToGoal)) {
						//gn is the total distance from start to n (the actual path)
						double gn = curr.getDisFromStart()+curr.getNighDistance(nodeCloserToGoal);
						//If path through curr to n is shorter:
						if(gn<nodeCloserToGoal.getDisFromStart()) {
							nodeCloserToGoal.setDisFromStart(gn);
							parentMap.put(nodeCloserToGoal, curr);
							toExplore.add(nodeCloserToGoal);
						}
					}
				}
			}
			
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		//We take advantage of our method reconstructPath created for BFS:
		List<GeographicPoint> path =
				reconstructPath(parentMap, startNode, endNode);
		System.out.println("count for a*: " + count);
		return path;
	}

	//I INCLUDED THE CLASSIC WEEK3 DIJKSTRA AND A* METHODS TO AVOID ERRORS WITH GRADER CLASSES.
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {
		int count = 0;
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		GeographicPoint startloc = new GeographicPoint(start.getX(), start.getY());
		// Setting distances to 0 and infinite;
		setDistanceFromStart(start, goal);
		// Enqueue S,0 onto the PQ:
		toExplore.add(pointNodeMap.get(startloc));
		while (!toExplore.isEmpty()) {
			MapNode curr = toExplore.poll();
			count++;
			if (!visited.contains(curr)) {
				visited.add(curr);
				if (curr.getLocation().equals(goal)) {
					break;
				}
				// For each of the currs neighbors, not visited:
				for (MapNode n : curr.getNeighbors()) {
					if (!visited.contains(n)) {
						// gn is the total distance from start to n (the actual path)
						double gn = curr.getDisFromStart() + curr.getNighDistance(n);
						// If path through curr to n is shorter:
						if (gn < n.getDisFromStart()) {
							n.setDisFromStart(gn);
							parentMap.put(n, curr);
							toExplore.add(n);
						}
					}
				}
			}

		}
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		// We take advantage of our method reconstructPath created for BFS:
		List<GeographicPoint> path = reconstructPath(parentMap, startNode, endNode);
		System.out.println("count for dijkstra is:" + count);
		return path;
	}
	
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		GeographicPoint startloc = new GeographicPoint(start.getX(), start.getY());
		int count = 0;
		// Setting distances to 0 and infinite;
		setDistanceFromStart(start, goal);
		// Enqueue S,0 onto the PQ:
		toExplore.add(pointNodeMap.get(startloc));
		while (!toExplore.isEmpty()) {
			MapNode curr = toExplore.poll();
			count++;
			if (!visited.contains(curr)) {
				visited.add(curr);
				if (curr.getLocation().equals(goal)) {
					break;
				}
				// For each of the currs neighbors, not visited:
				MapNode nodeCloserToGoal = curr.getCloseNeighbors(goal);
				if (!visited.contains(nodeCloserToGoal)) {
					// gn is the total distance from start to n (the actual path)
					double gn = curr.getDisFromStart() + curr.getNighDistance(nodeCloserToGoal);
					// If path through curr to n is shorter:
					if (gn < nodeCloserToGoal.getDisFromStart()) {
						nodeCloserToGoal.setDisFromStart(gn);
						parentMap.put(nodeCloserToGoal, curr);
						toExplore.add(nodeCloserToGoal);
					}
				}
			}
		}

		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		// We take advantage of our method reconstructPath created for BFS:
		List<GeographicPoint> path = reconstructPath(parentMap, startNode, endNode);
		System.out.println("count for a*: " + count);
		return path;
	}
	
	
	public static void main(String[] args)
	{
		System.out.print("Making a new map...");
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		System.out.println("DONE.");
		
		/*Basically, you can see how in this piece of test, the route without roadworks
		 * for simpletest.map, the map given in the course would be simple:
		 * 4,1 to 8,1, but that road is the 2nd biggest one so it has roadworks
		 * in progress, we cannot access it, so the path taken instead is 4,-1 to 4,0 to
		 * 5,1 to 6.5,0, to 8.-1.
		 */
		GeographicPoint start = new GeographicPoint(4.0, -1.0);
		GeographicPoint end = new GeographicPoint(8.0, -1.0);
		List<GeographicPoint> route = theMap.dijkstraWithWorks(start,end,ROADWORKS);
		List<GeographicPoint> route2 = theMap.aStarSearchWithWorks(start,end,ROADWORKS);
		
		
		 //Use this code in Week 3 End of Week Quiz
		/*
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		
		List<GeographicPoint> route = theMap.dijkstraWithWorks(start,end,1);
		//List<GeographicPoint> route2 = theMap.aStarSearch(start,end);*/

		
	}
	
}
