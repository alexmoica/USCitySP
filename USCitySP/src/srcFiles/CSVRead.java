package srcFiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVRead {	
	public static String[][] USCities = new String[32][3];
	public static String[][] Connections = new String[52][];
	public static String[][] MDRaw = new String[13912][4];
    public static String[][] MDList = new String[32][3];
    public static String[][] menuRaw = new String[40][3];
    public static int minMeal1 = 0;
    public static int minMeal2 = 0;

    public static void main(String[] args) {
    	int countCity = 0;
    	String line = "";
    	
    	String USCitiesCSV = "Datasets/USCities.csv"; //state code, zip code, state abbr, name, lat, long 
        try (BufferedReader br = new BufferedReader(new FileReader(USCitiesCSV))) {
        	line = br.readLine(); //skip first line of file
            while ((line = br.readLine()) != null) {
                String[] state = line.split(",");
                USCities[countCity][0] = state[2]+","+state[3]; //state abbr, name
                USCities[countCity][1] = state[4]; //lat
                USCities[countCity][2] = state[5]; //long
            	countCity++;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int countMarked = 0;
    	String ConnectionsCSV = "Datasets/connectedCities.txt"; //source, target
        try (BufferedReader br = new BufferedReader(new FileReader(ConnectionsCSV))) {
            while ((line = br.readLine()) != null) {
            	String[] state = line.split(", ");
            	Connections[countMarked] = state;
            	countMarked++;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int countMD = 0;
        //McDonalds was used since it has the two lowest priced items, if there is a McDonalds in each city, then the cheapest route would alternate between ordering these two items at each city on the route
        String MDCSV = "Datasets/mcdonalds.csv"; //long, lat, name, address
        try (BufferedReader br = new BufferedReader(new FileReader(MDCSV))) {
        	line = br.readLine(); //skip first line of file
            while ((line = br.readLine()) != null) {
            	String[] state = line.split(",");
            	MDRaw[countMD][0] = state[1]; //lat
            	MDRaw[countMD][1] = state[0]; //long
            	MDRaw[countMD][2] = state[6].substring(0, 2)+","+state[5].substring(1); //state abbr, name
            	MDRaw[countMD][3] = state[4].substring(1)+","+state[5]+", "+state[6].substring(0, 2); //city, state, address
            	countMD++;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //reduce the total list of McDonald's to a list of 52, one for each state that is used in the graph
        for(int i = 0;i<CSVRead.USCities.length;i++) {
        	//inconsistency in naming of New York/New York City throughout CSV files, this conditional accounts for it
        	if(CSVRead.USCities[i][0].equals("NY,NEW YORK CITY")) {
            	for(int j = 0;j<MDRaw.length;j++) {
            		if(("NY,NEW YORK").equals(MDRaw[j][2].toUpperCase())) {
            			//Add the first listed restaurant in each city as the restaurant to visit in that city
            			MDList[i][0] = MDRaw[j][0]; //lat
            			MDList[i][1] = MDRaw[j][1]; //long
            			MDList[i][2] = MDRaw[j][3]; //city, state abbr, address
            			break;
            		}
            	}
            //inconsistency in naming of ST LOUIS/ST. LOUIS throughout CSV files, this conditional accounts for it
        	} else if(CSVRead.USCities[i][0].equals("MO,ST. LOUIS")) {
        		for(int j = 0;j<MDRaw.length;j++) {
            		if(("MO,ST LOUIS").equals(MDRaw[j][2].toUpperCase())) {
            			//Add the first listed restaurant in each city as the restaurant to visit in that city
            			MDList[i][0] = MDRaw[j][0]; //lat
            			MDList[i][1] = MDRaw[j][1]; //long
            			MDList[i][2] = MDRaw[j][3]; //city, state abbr, address
            			break;
            		}
            	}
        	} else {
            	for(int j = 0;j<MDRaw.length;j++) {
            		if(CSVRead.USCities[i][0].equals(MDRaw[j][2].toUpperCase())) {
            			//Add the first listed restaurant in each city as the restaurant to visit in that city
            			MDList[i][0] = MDRaw[j][0]; //lat
            			MDList[i][1] = MDRaw[j][1]; //long
            			MDList[i][2] = MDRaw[j][3]; //city, state abbr, address
            			break;
            		}
            	}
        	}
        }
        //Restaurants are considered within city limits if their lat and long values are within 0.5 of those of the city
        for(int i = 0;i<USCities.length;i++) {
        	//check latitude
        	if(Math.abs(Double.parseDouble(USCities[i][1])-Double.parseDouble(MDList[i][0]))>0.5) {
        		System.out.println("Error: Restaurant at index: "+i+" is not within city distance!");
        		break;
        	}
        	//check longitude
        	if(Math.abs(Double.parseDouble(USCities[i][2])-Double.parseDouble(MDList[i][1]))>0.5) {
        		System.out.println("Error: Restaurant at index: "+i+" is not within city distance!");
        		break;
        	}
        }
        //if no error statements are returned, then there is at least one McDonalds restaurant in each city, meaning we can only look at McDonalds restaurants as per the requirements
        int countMenu = 0;
        String MenuCSV = "Datasets/menu.csv"; //restaurant, meal, price, comment
        try (BufferedReader br = new BufferedReader(new FileReader(MenuCSV))) {
        	line = br.readLine(); //skip first line of file
            while ((line = br.readLine()) != null) {
            	String[] state = line.split(",");
            	menuRaw[countMenu][0] = state[0]; //name
            	menuRaw[countMenu][1] = state[1]; //meal
            	menuRaw[countMenu][2] = state[2].substring(1); //price
            	countMenu++;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //find the two lowest cost items at McDonald's
        for(int i = 0;i<MenuCSV.length();i++) {
        	if(menuRaw[i][0].equals("McDonald’s")) {
        		if(Double.parseDouble(menuRaw[i][2])<Double.parseDouble(menuRaw[minMeal1][2])) {
        			minMeal1 = i;
        		}
        	}
        }
        for(int i = 0;i<MenuCSV.length();i++) {
        	if(menuRaw[i][0].equals("McDonald’s")) {
        		if(Double.parseDouble(menuRaw[i][2])<Double.parseDouble(menuRaw[minMeal2][2]) && i != minMeal1) {
        			minMeal2 = i;
        		}
        	}
        }
    }
}