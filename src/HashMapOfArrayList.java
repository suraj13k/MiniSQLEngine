import java.util.*;

public class HashMapOfArrayList {
	 HashMap<String, ArrayList<Integer>> my_graph;
	 
	public HashMapOfArrayList(HashMap<String, ArrayList<Integer>> graph) {
	    //HashMap<String, ArrayList<Integer>> graph = source.getGraph(); // get the source graph

	    my_graph = new HashMap<String, ArrayList<Integer>>(); //define our graph 
	    for(Map.Entry<String, ArrayList<Integer>> entry : graph.entrySet()) {
	        //iterate through the graph
	        ArrayList<Integer> sourceList = entry.getValue();
	        ArrayList<Integer> clonedList = new ArrayList<Integer>();
	        clonedList.addAll(sourceList);
	        //put value into new graph
	        my_graph.put(entry.getKey(), clonedList);
	    }
	}

	HashMap<String, ArrayList<Integer>> getGraph() {
		// TODO Auto-generated method stub
		return my_graph;
	}
}
