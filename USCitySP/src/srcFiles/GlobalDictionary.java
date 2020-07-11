package srcFiles;

import java.util.HashMap;
import java.util.Map;

//Assign string names to integer values used in creating the graph
public class GlobalDictionary {
	private Map<String, Integer> map = new HashMap<String, Integer>();
	
	public void add(String key, int val) {
		map.put(key,  val);
	}
	
	public Integer get(String key) {
		return map.get(key);
	}
}
