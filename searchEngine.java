import java.util.*;
import java.io.*;

public class searchEngine{
	
	/* 	NAME - ADITYA SAHA
		ID - 260453165
	*/

	/* this will contain a set of pairs (String, LinkedList of Strings)	 */
	public HashMap<String, LinkedList<String> > wordIndex;                  
	
	/* this is our internet graph */
	public directedGraph internet;             								

	
	LinkedList<String> content = new LinkedList<String>();   				
	LinkedList<String> urlsOut = new LinkedList<String>();					
	LinkedList<String> allVertices = new LinkedList<String>();				

	// Constructor initializes everything to empty data structures
	// It also sets the location of the internet files
	searchEngine() {
		// Below is the directory that contains all the internet files
		htmlParsing.internetFilesLocation = "internetFiles";
		wordIndex = new HashMap<String, LinkedList<String> > ();		
		internet = new directedGraph();				
	} // end of constructor2015


	// Returns a String description of a searchEngine
	public String toString () {
		return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
	}

	void traverseInternet(String url) throws Exception {
		/* NOTE: Refer to the linked lists defined outside the scope of this method */
		
		/* sets visited URLs */
		internet.setVisited(url, true);						
		
		/* extracts links from the URLs and places in the linked list */
		urlsOut = htmlParsing.getLinks(url);				
		
		/* extracts words from the URLs and places in the linked list */
		content = htmlParsing.getContent(url);				

		/* make current website the vertex of the graph */
		internet.addVertex(url);							

		/* iterator to loop through the linked list */
		Iterator<String> itr1 = urlsOut.iterator(); 		
		
		/* iterator to loop through all the words in the url */
		Iterator<String> itr2 = content.iterator();			

		/* updates wordIndex */
		while(itr2.hasNext()){	
			String word = itr2.next();									/* goes through all words in each webpage */
			if (wordIndex.containsKey(word)){ 							/* the word is already in the hashmap */
				if(!(wordIndex.get(word)).contains(url)){				/* if wordIndex does NOT contain present url for the word */
					wordIndex.get(word).addLast(url);					/* adds only the url, since the word is already present */
				}
			}
			else{														/* the word is new */
				LinkedList<String> newWordUrls = new LinkedList<>(); 	/* creates a new linked list for the new word */  
				newWordUrls.addLast(url);								/* adds the current url to the newly created linked list */
				wordIndex.put(word, newWordUrls);						/* adds word + linkedlist to the hashmap */ 
			}
		}	

		/* updates the internet graph */
		while(itr1.hasNext()){								/* while there are adjacent vertices starting from first element in the linked list */
			String vertex = itr1.next();					/* vertex is the next url in the linked list */
			internet.addEdge(url, vertex);					/* add a edge between the two websites */
			if(internet.getVisited(vertex)==false){			/* if the adjacent vertex has not been visited */
				internet.setVisited(vertex, true);			/* set the new vertex as visisted */
				traverseInternet(vertex);					/* recursively call traverseInternet with the new vertex */
			}
		}

	} 

	void computePageRanks(){

		/* The linked lists "allVertices" have been defined above outside the function */
		allVertices = internet.getVertices();        		/* gets all the vertices of the map */
		Iterator<String> itr1 = allVertices.iterator(); 	/* creating an iterator to loop through "allVertices" linked list */
		String vertex;		

		while(itr1.hasNext()){								/* while not end of linked list */
			vertex = itr1.next();							/* vertex is the next element in the list (starting with the head) */
			internet.setPageRank(vertex, 1.00);				/* sets rank of all vertex = 1 initially */
		}

		for(int i=0;i<100;i++){								/* for 100 iterations */
			itr1 = allVertices.iterator();					/* resetting itr1 with head of linked list containing all vertex */
			while(itr1.hasNext()){							/* looping through all vertices */
				vertex = itr1.next();													/* vertex = current vertex */
				LinkedList<String> edgesIntoList = internet.getEdgesInto(vertex);		/* linked list of all the edges into the current vertex */
				Iterator<String> itr2 = edgesIntoList.iterator();						/* iterator to go through all edges into list */
				double pageRank = 0.5;
				while(itr2.hasNext()){
					String edgeInto = itr2.next();														/* edgeInto = each edge into vertex	 */
					pageRank += (0.5*(internet.getPageRank(edgeInto)/internet.getOutDegree(edgeInto))); /* adding the page rank contribution of each new edge */
				}				
				internet.setPageRank(vertex, pageRank);		/* setting the page rank of each vertex */
			}
		}

	} 
	
	String getBestURL(String query){

		String url, bestUrl = "", word = query.toLowerCase(); 
		double pageRank, bestPageRank = -1;
		LinkedList<String> allUrls = new LinkedList<>();

		if(wordIndex.containsKey(word)){					/* if the word index contains the query word */
			allUrls = wordIndex.get(word);					/* allUrls gets the linked list of webpages containing the word */
			Iterator<String> itr1 = allUrls.iterator();		

			while(itr1.hasNext()){							/* iterates through all webpages */
				url = itr1.next();							/* gets url */
				pageRank = internet.getPageRank(url);		/* gets page rank of url */
				if(pageRank > bestPageRank){				/* if current page Rank > best Page Rank */
					bestUrl = url;							/* bestUrl = current url */
					bestPageRank = pageRank;				/* best Page Rank = current Page Rank */
				}				
			}			
			return bestUrl;									/* returns the bestUrl */
		}
		
		else{												/* if the word index doesn't contain the query word		 */
			return bestUrl;									/* returns "<...>" */
		}

	} 

	public static void main(String args[]) throws Exception{		
		searchEngine mySearchEngine = new searchEngine();
		// to debug your program, start with.
		//	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");

		// When your program is working on the small example, move on to
		/* mySearchEngine.traverseInternet("http://www.cs.mcgill.ca"); */
		mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");

		// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
		System.out.println(mySearchEngine);

		mySearchEngine.computePageRanks();

		BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
		String query;
		do{
			System.out.print("Enter query (or hit ENTER to exit): ");
			query = stndin.readLine();
			if ( query != null && query.length() > 0 ){
				System.out.println("Best site = " + mySearchEngine.getBestURL(query));
			}
		} while (query!=null && query.length()>0);				
	} // end of main
}
