package finalproject;

import java.util.HashMap;
import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, LinkedList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;

	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does a graph traversal of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 * 
	 * 	This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {
		// TODO : Add code here
		if (internet.getVisited(url)) return;
		
		internet.addVertex(url);
		internet.setVisited(url, true);
		
		for (String link: parser.getLinks(url)) {
			internet.addVertex(link);
			internet.addEdge(url, link);
//			ArrayList<String> neighbors = internet.getNeighbors(link);
//			if (! neighbors.contains(url)) { //prevent loop
				crawlAndIndex(link); //crawl only
//			}
		}
		
		//update the word index
		ArrayList<String> words = parser.getContent(url);
		for (String word : words) {
			if (wordIndex.containsKey(word.toLowerCase())) {
				// if the word is already in the hashmap, then update the link
				if (!wordIndex.get(word.toLowerCase()).contains(url)) {
					// only add the link if it's not already in the arraylist
					wordIndex.get(word.toLowerCase()).add(url);
				}
			} else {
				// if not, then add the new key
				ArrayList<String> addList = new ArrayList<>();
				addList.add(url);
				wordIndex.put(word.toLowerCase(), addList);
			}
		}
	}
	
	
	/* 
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex(). 
	 * To implement this method, refer to the algorithm described in the 
	 * assignment pdf. 
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		// TODO : Add code here
		boolean passed = false;
		ArrayList<String> vertices = new ArrayList<>();
		vertices.addAll(internet.getVertices());
		ArrayList<Double> prevCalc = new ArrayList<>();
		ArrayList<Double> curCalc = new ArrayList<>();
		
		// initialize the page ranks to 1.0
		for (int i = 0; i < vertices.size(); i++) {
			internet.setPageRank(vertices.get(i), 1.0);
			prevCalc.add(1.0);
		}
		
		while (!passed) {
			passed = true;
			curCalc = computeRanks(vertices);
			for (int j = 0; j < vertices.size(); j++) {
				Double prev = prevCalc.get(j);
				Double cur = curCalc.get(j);
				internet.setPageRank(vertices.get(j), cur);
				if (Math.abs(prev - cur) >= epsilon) {
					passed = false;
				}
			}
			prevCalc = curCalc;
		}
	}
	
	
	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph 
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls. 
	 * Note that the double in the output list is matched to the url in the input list using 
	 * their position in the list.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		// TODO : Add code here
		ArrayList<Double> result = new ArrayList<>();
		
		for (int j = 0; j < vertices.size(); j++) {
			double inEdgeSum = 0.0;
			ArrayList<String> inEdges = new ArrayList<>();
			inEdges.addAll(internet.getEdgesInto(vertices.get(j)));
			for (String inEdge : inEdges) {
				inEdgeSum += internet.getPageRank(inEdge)/ internet.getOutDegree(inEdge);
			}
			
			double answer = 0.5 + 0.5*inEdgeSum;
			result.add(answer);
		}
		
		return result;
	}

	
	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 * 
	 * This method should take about 25 lines of code.
	 */
	public ArrayList<String> getResults(String query) {
		// TODO: Add code here
		ArrayList<String> result = new ArrayList<>();
		
		//if no website contains the query. returns an empty list
		if (! wordIndex.containsKey(query)) return result;
		
		// a list of the webPages containing the query
		ArrayList<String> webPages =  wordIndex.get(query);
		
		// need a hashmap of <String url, double pageRank>
		HashMap<String, Double> pageRanks = new HashMap<>();
		for (String page: webPages) {
			pageRanks.put(page, internet.getPageRank(page));
		}
		
		result = Sorting.fastSort(pageRanks);
		
		return result;
	}
}
