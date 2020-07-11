/**
 * 
 */
package srcFiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author alex
 *
 */
public class JUnitTest {
	public BufferedReader reader;
	public String[] args;
    public Boolean flag;
		
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		reader = new BufferedReader(new FileReader("a2_out.txt"));
		args = null;
		GraphSP.main(args);
		flag = false;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {				
	      List<String> content = new ArrayList<String>();
	      String line = reader.readLine();
	      while (line != null) {
	          content.add(line);
	          line = reader.readLine();
	      }
	      String[] contentBFS = content.get(0).split(", ");
	      contentBFS[0] = contentBFS[0].substring(5); //remove 'BFS: ' from first city string
	      for(int i = 1;i<contentBFS.length;i++) { //for each city in BFS trace
    		  flag = false;
	    	  for(int j = 0;j<CSVRead.Connections.length;j++) { //for each known city connection
	    		  if(CSVRead.Connections[j][1].toUpperCase().equals(contentBFS[i])) { //if the connection target is the city from the BFS trace
	    			  for(int k = 0;k<i;k++) { //for each index up to the current city index from the BFS trace
		    			  if(CSVRead.Connections[j][0].toUpperCase().equals(contentBFS[k])) { //if there exists a city in the BFS trace previous to the current city that is the src of the connection with the current city from the BFS trace
			    			  flag = true;
			    			  assert(true);
		    			  }
		    		  }
	    		  }
	    	  }
	    	  if(!flag) {
    			  assert(false);
    		  }
	      }
	      
	      String[] contentDFS = content.get(1).split(", ");
	      contentDFS[0] = contentDFS[0].substring(5); //remove 'DFS: ' from first city string
	      for(int i = 1;i<contentDFS.length;i++) { //for each city in DFS trace
    		  flag = false;
	    	  for(int j = 0;j<CSVRead.Connections.length;j++) { //for each known city connection
	    		  if(CSVRead.Connections[j][1].toUpperCase().equals(contentDFS[i])) { //if the connection target is the city from the DFS trace
	    			  for(int k = 0;k<i;k++) { //for each index up to the current city index from the DFS trace
		    			  if(CSVRead.Connections[j][0].toUpperCase().equals(contentDFS[k])) { //if there exists a city in the DFS trace previous to the current city that is the src of the connection with the current city from the DFS trace
			    			  flag = true;
			    			  assert(true);
		    			  }
		    		  }
	    		  }
	    	  }
	    	  if(!flag) {
    			  assert(false);
    		  }
	      }
	}
}