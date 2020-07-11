package srcFiles;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

class Vertex implements Comparable<Vertex> {
    public final int val;
    public ArrayList<Edge> adjacencies;
    public double minDist = Double.POSITIVE_INFINITY;
    public Vertex prev;
    
    public Vertex(int v) {
    	val = v;
    }
    public int compareTo(Vertex that)
    {
        return Double.compare(minDist, that.minDist);
    }
}

class Edge {
    public final Vertex tar;
    public final double weight;
    public Edge(Vertex t, double w) {
    	tar = t; weight = w;
    }
}

//Dijkstra's algorithm
public class GraphSP {
	public static boolean flag = true;
    public static void computePaths(Vertex source) {
        source.minDist = 0.0;
        PriorityQueue<Vertex> vQ = new PriorityQueue<Vertex>();
        vQ.add(source);

        while (!vQ.isEmpty()) {
            Vertex u = vQ.poll();

            // Visit each edge leading out of vertex u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.tar;
                double weight = e.weight;
                double distU = u.minDist + weight;
                if (distU < v.minDist) {
                    vQ.remove(v);

                    v.minDist = distU ;
                    v.prev = u;
                    vQ.add(v);
                }
            }
        }
    }

    public static List<Vertex> shortestPathTo(Vertex tar) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex v = tar; v != null; v = v.prev)
            path.add(v);
        
        Collections.reverse(path);
        return path;
    }
    
    public static void DFS(int from, int to, boolean marked[], ArrayList<Vertex> vertList, PrintWriter writer) {
    	if(flag) {
    		//mark current node and print it
        	marked[from] = true;
        	if(vertList.get(from).val == vertList.get(to).val) {
    	        flag = false;
            	//System.out.print(CSVRead.USCities[vertList.get(from).val][0].substring(3));
                writer.print(CSVRead.USCities[vertList.get(from).val][0].substring(3));
        	} else {
            	//System.out.print(CSVRead.USCities[vertList.get(from).val][0].substring(3) + ", ");
            	writer.print(CSVRead.USCities[vertList.get(from).val][0].substring(3) + ", ");
        	}
        	
        	//recur DFS for all vertices adjacent to vertex
        	Iterator<Vertex> i = vertList.listIterator();
            while(i.hasNext()) {
            	Vertex e = i.next();
            	if(!marked[e.val]) {
            		DFS(e.val, to, marked, vertList, writer);
            	}
            }
    	}
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
    	
    	PrintWriter writer = new PrintWriter("out.txt", "UTF-8"); //output file
    	
    	CSVRead.main(args); //run CSVRead.java main method
    	GlobalDictionary CityDict = new GlobalDictionary(); //global dictionary to map city names to integers used in graphs
    	ArrayList<Vertex> vertList = new ArrayList<Vertex>();
    	ArrayList<Integer> markedList = new ArrayList<Integer>(); //array of CSV row inputs already turned into vertices
    	
    	//map cities to integers
       	for(int i = 0;i<CSVRead.USCities.length;i++) {
    		CityDict.add(CSVRead.USCities[i][0].substring(3), i);
    	}
    	
    	for(int n = 0;n<CSVRead.Connections.length;n++) {
    		Integer src = CityDict.get(CSVRead.Connections[n][0].toUpperCase());
    		Integer tar = CityDict.get(CSVRead.Connections[n][1].toUpperCase());
    		Double srcLat = Double.parseDouble(CSVRead.USCities[src][1]);
    		Double srcLong = Double.parseDouble(CSVRead.USCities[src][2]);
    		Double tarLat = Double.parseDouble(CSVRead.USCities[tar][1]);
    		Double tarLong = Double.parseDouble(CSVRead.USCities[tar][2]);

    		//greater circle distance formula for points on the Earth's surface described by latitude and longitude
    		Double distance = Math.acos(Math.sin(Math.PI*srcLat/180.0)*Math.sin(Math.PI*tarLat/180.0)+Math.cos(Math.PI*srcLat/180.0)*Math.cos(Math.PI*tarLat/180.0)*Math.cos(Math.PI*srcLong/180.0-Math.PI*tarLong/180.0))*6378;    		
    		
    		//source city does not have an existing vertex
    		if(!markedList.contains(src)) {
    			//create a source vertex and initialize it
    			Vertex newSrc = new Vertex(src);
    			newSrc.adjacencies = new ArrayList<Edge>();
    			vertList.add(newSrc);
    			markedList.add(src);
    		}

    		//source and destination cities already have existing vertices
    		if(markedList.contains(tar)) {
    			for(int i = 0;i<vertList.size();i++) {
    				if(vertList.get(i).val == src) {
    					for(int j = 0;j<vertList.size();j++) {
    						if(vertList.get(j).val == tar) {
    							Edge newEdg = new Edge(vertList.get(j),distance);
    							vertList.get(i).adjacencies.add(newEdg); //simply add the existing target city to the adjacency list of the source city
    							break;
    						}
    					}
    					break;
    				}
    			}
    		}
    		
    		//only source city has an existing vertex
    		if(!markedList.contains(tar)) {
    			//create a target vertex and initialize it
    			Vertex newTar = new Vertex(tar);
    			newTar.adjacencies = new ArrayList<Edge>();
    			vertList.add(newTar);
    			markedList.add(tar);
    			
    			for(int i = 0;i<vertList.size();i++) {
        			if(vertList.get(i).val == src) {
        				Edge newEdg = new Edge(newTar,distance);
        				vertList.get(i).adjacencies.add(newEdg); //add the newly created target city to the adjacency list of the source city
        				break;
        			}
        		}
    		}    		
    	}
   
    	int from = 0;
    	int to = 21;
        computePaths(vertList.get(from)); // run Dijkstra's algorithm
        List<Vertex> path = shortestPathTo(vertList.get(to+1));
        
        //System.out.print("BFS trace: ");
    	writer.print("BFS: ");
        //BFS
        int skipFirst = 0;
        boolean markedBFS[] = new boolean[vertList.size()]; //mark all vertices not visited
        LinkedList<Vertex> queueBFS = new LinkedList<Vertex>();
        //start BFS from 'from' vertex 
        markedBFS[from] = true;
        queueBFS.add(vertList.get(from));
        while(queueBFS.size() != 0) {
        	Vertex next = queueBFS.poll(); //dequeue vertex
        	
        	//stop once the final destination has been found
        	if(next.val == vertList.get(to).val) {
            	//System.out.print(CSVRead.USCities[to][0].substring(3));
            	writer.print(CSVRead.USCities[to][0].substring(3));
        		break;
        	}
        	//only display the starting city once
        	if(skipFirst == 0) {
            	//System.out.print(CSVRead.USCities[f][0].substring(3)+", ");
            	writer.print(CSVRead.USCities[from][0].substring(3)+", ");
            	skipFirst++;
        	} else {
            	//System.out.print(CSVRead.USCities[next.val][0].substring(3));
            	writer.print(CSVRead.USCities[next.val][0].substring(3)+", ");
        	}

        	//get adjacent vertices, if a vertex is not marked, mark and enqueue it
        	Iterator<Vertex> i = vertList.listIterator();
        	while(i.hasNext()) {        		
        		Vertex e = i.next();
        		if(!markedBFS[e.val]) {
        			markedBFS[e.val] = true;
        			queueBFS.add(e);
        		}
        	}
        }
        //System.out.println();
    	writer.println();
    	writer.println();
    	
        //System.out.print("DFS trace: ");
    	writer.print("DFS: ");
        //DFS
        boolean markedDFS[] = new boolean[vertList.size()]; //mark all vertices not visited
        DFS(from, to+1, markedDFS, vertList, writer);
    	//System.out.println();
    	writer.println();
    	//System.out.println();
    	writer.println();
        
    	to++;
    	
        //System.out.println("Most Cost Efficient Path From " + CSVRead.USCities[vertList.get(from).val][0].substring(3) + " To " + CSVRead.USCities[vertList.get(to).val][0].substring(3) +":");
    	writer.println("Most Cost Efficient Path From " + CSVRead.USCities[vertList.get(from).val][0].substring(3) + " To " + CSVRead.USCities[vertList.get(to).val][0].substring(3) +":");
        //System.out.println();
    	writer.println();
        //System.out.print(String.format("%30s %40s %20s %60s \r\n", "City", "Meal Choice", "Cost of Meal", "Restaurant Address"));
    	writer.print(String.format("%30s %40s %20s %60s \r\n", "City", "Meal Choice", "Cost of Meal", "Restaurant Address"));

        Boolean toggle = true; //alternates between which meal to get
        for(int i = 0;i<path.size();i++) {
    		String city = CSVRead.USCities[path.get(i).val][0].substring(3);
    		String address = CSVRead.MDList[path.get(i).val][2].split(",")[0];
        	if(i == 0) {
        		//System.out.print(String.format("%30s %40s %20s %60s \r\n", city, "N/A", "N/A", "N/A"));
        		writer.print(String.format("%30s %40s %20s %60s \r\n", city, "N/A", "N/A", "N/A"));
        	} else {
        		if(toggle) {
            		String meal = CSVRead.menuRaw[CSVRead.minMeal1][1];
            		String price = "$"+CSVRead.menuRaw[CSVRead.minMeal1][2];
            		//System.out.print(String.format("%30s %40s %20s %60s \r\n", city, meal, price, address));
            		writer.print(String.format("%30s %40s %20s %60s \r\n", city, meal, price, address));
        		} else {
            		String meal = CSVRead.menuRaw[CSVRead.minMeal2][1];
            		String price = "$"+CSVRead.menuRaw[CSVRead.minMeal2][2];
            		//System.out.print(String.format("%30s %40s %20s %60s \r\n", city, meal, price, address));
            		writer.print(String.format("%30s %40s %20s %60s \r\n", city, meal, price, address));
        		}
        		toggle =! toggle;
        	}
        }
        writer.close();
    }
}