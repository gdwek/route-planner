import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class main {
	public static int numPlaces;
	public static ArrayList <String> places;
	public static ArrayList <String> placesSpaced;
	public static boolean roundTrip;
	public static void main(String[] args) throws Exception {
			places = new ArrayList<String>();
			placesSpaced = new ArrayList<String>();
			ArrayList <Integer> values = new ArrayList<Integer>();
			Scanner scan  = new Scanner (System.in);
			System.out.println("How many places would you like to go to?");
			numPlaces  = Integer.parseInt(scan.nextLine());
			System.out.println("Enter your starting point below.");
			String start = scan.nextLine();
			places.add(start.replaceAll("\\s", ""));
			placesSpaced.add(start);
			System.out.println("Do you have a specific endpoint? Enter Y/N");
			String yesOrNo = scan.nextLine();
			if(yesOrNo.equalsIgnoreCase("Y")) {
				System.out.println("Enter your endpoint below.");
				String end = scan.nextLine();
				places.add(end.replaceAll("\\s", ""));
				placesSpaced.add(end);
			}
			else if (yesOrNo.equalsIgnoreCase("N")) {
				System.out.println("Is this a round trip? Enter Y/N");
				String answer = scan.nextLine();
				if(answer.equalsIgnoreCase("Y"))
					roundTrip = true;
				else if(answer.equalsIgnoreCase("N"))
					roundTrip = false;
			}
			//else error handle
			
			int [][] matrix = new int [numPlaces+1][numPlaces+1];
			String place;
			if(yesOrNo.equalsIgnoreCase("Y")) {
				for(int i  = 2; i<=numPlaces; i++) {
					System.out.println("Enter stop " + (i-1)+ " below.");
					place = scan.nextLine();
					places.add(place.replaceAll("\\s", ""));
					placesSpaced.add(place);
				}
			}
			else {
				for(int i  = 1; i<=numPlaces; i++) {
					System.out.println("Enter stop " + i + " below.");
					place = scan.nextLine();
					places.add(place.replaceAll("\\s", ""));
					placesSpaced.add(place);
				}
			}
			for(int i  = 0; i<=numPlaces; i++) {
				for(int j = 0; j<=numPlaces; j++) {
					URL myURL = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + places.get(i) + "&destinations=" + places.get(j) + "&key=AIzaSyClZxuvOXNWqOjy5RPLpKKXvtdBOnELhWA");
					URLConnection myURLConnection = myURL.openConnection();
					myURLConnection.connect();
					
					BufferedReader in = new BufferedReader(new InputStreamReader(
					myURLConnection.getInputStream()));
					String inputLine;
					int line = 0;
					while ((inputLine = in.readLine()) != null) {
						line++;
						if(line == 14) {
							matrix[i][j]= Integer.parseInt(inputLine.split(" : ")[1]);
							break;
						}
					}
					in.close();
				}	
			}	
			
			System.out.println();
			ArrayList <Pair<Integer, Integer>> best;
			if(yesOrNo.equalsIgnoreCase("Y"))
				best = fastestRoute(matrix, 0, 1);
			else
				best = fastestRoute(matrix, 0);

			System.out.print("The best path is:");
			for(int i = 0; i<best.size(); i++) {
				System.out.print(" " + placesSpaced.get(best.get(i).getL())+ "----->");
			}
			System.out.print(" " + placesSpaced.get(best.get(best.size()-1).getR())+ " ");
	}
			
	public static ArrayList<Pair<Integer,Integer>> fastestRoute(int [][] matrix, int start) {
		int vertex [] = new int [numPlaces];
		ArrayList <Pair<Integer, Integer>> tempBest = new ArrayList<Pair<Integer, Integer>> ();
		ArrayList <Pair<Integer, Integer>> best = new ArrayList<Pair<Integer, Integer>> ();
		ArrayList <String> bestPath = new ArrayList <String>();
		for(int i = 0; i<numPlaces; i++) {
			vertex[i] = i+1;
		}
		int minPath = Integer.MAX_VALUE;
		int currentPathWeight;
		int k;
		while(true) {
			tempBest.clear();
			currentPathWeight = 0;
			k = start;
			for(int i = 0; i<vertex.length; i++) {
				currentPathWeight+= matrix[k][vertex[i]];
				tempBest.add(new Pair <Integer, Integer> (k, vertex[i]));
				k= vertex[i];
			}
			if(roundTrip) {
				currentPathWeight+= matrix[k][start];
				tempBest.add(new Pair <Integer, Integer> (k,start));
			}
			if(minPath>currentPathWeight) {
				best.clear();
				for(int i=0; i<tempBest.size(); i++) {
					best.add(new Pair<Integer, Integer> (tempBest.get(i).getL(), tempBest.get(i).getR()));
				}
				minPath = currentPathWeight;
			}
			if(!nextPermutation(vertex)) {
				break;
			}
		}
		System.out.println("The trip would take " + minPath/60/60 + " hours and " + ((TimeUnit.SECONDS.toMinutes(minPath)) - (TimeUnit.SECONDS.toHours(minPath))*60) + " minutes.");
		return best;
	}
	public static ArrayList<Pair<Integer,Integer>> fastestRoute(int [][] matrix, int start, int end) {
		int vertex [] = new int [numPlaces-1];
		ArrayList <Pair<Integer, Integer>> tempBest = new ArrayList<Pair<Integer, Integer>> ();
		ArrayList <Pair<Integer, Integer>> best = new ArrayList<Pair<Integer, Integer>> ();
		ArrayList <String> bestPath = new ArrayList <String>();  
		
		for(int i = 0; i<numPlaces-1; i++) {
			vertex[i] =i+2;
		}
		
		int minPath = Integer.MAX_VALUE;
		int currentPathWeight;
		int k;
		while(true) {
			tempBest.clear();
			currentPathWeight = 0;
			k = start;
			for(int i = 0; i<vertex.length; i++) {
				currentPathWeight+= matrix[k][vertex[i]];
				tempBest.add(new Pair <Integer, Integer> (k, vertex[i]));
				k= vertex[i];
			}
			currentPathWeight+= matrix[k][end];
			tempBest.add(new Pair <Integer, Integer> (k,end));
			if(minPath>currentPathWeight) {
				best.clear();
				for(int i=0; i<tempBest.size(); i++) {
					best.add(new Pair<Integer, Integer> (tempBest.get(i).getL(), tempBest.get(i).getR()));
				}
				minPath = currentPathWeight;
			}
			if(!nextPermutation(vertex)) {
				break;
			}
		}
		System.out.println("The trip would take " + minPath/60/60 + " hours and " + ((TimeUnit.SECONDS.toMinutes(minPath)) - (TimeUnit.SECONDS.toHours(minPath))*60) + " minutes.");
		return best;
	}
	public static boolean nextPermutation(int[]array) {
		int n= array.length;
		int i=n-2;
		while(i>=0 && array[i]>=array[i+1]) {
			i-=1;
		}
		if(i==-1) 
			return false;
		int j=i+1;
		while(j<n && array[j]>array[i]) {
			j+=1;
		}
		j-=1;
		int temp=array[i];
		array[i]=array[j];
		array[j]=temp;
		int left=i+1;
		int right=n-1;
		while(left<right) {
			temp=array[left];
			array[left]=array[right];
			array[right]=temp;
			left+=1;
			right-=1;
		}
		return true;
	}
}