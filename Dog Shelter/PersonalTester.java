package assignment3;

import assignment3.Dog;
import assignment3.DogShelter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class PersonalTester {
	static Dog d = null;
	static DogShelter ds = new DogShelter(d);
	static Dog[] randomDogs;
	
	

	public static void main(String[] args){
		String testInput = "[A(2, 5), D(1, 2), B(4, 4), E(3, 1), C(5, 3)]";
		debugTest1(10);   //test shelter method iteration by iteration
		inputTestCases();   //give your own test cases as input
		inputTestCases(testInput);   //give your own test cases as input
		test1(10); //create random test cases
		testRemove();    //remove the first element in the array
		testFindOldest();
		testFindYoungest();
		testFindDogToAdopt(20, 25);
		testBudget(20);
		testVetSchedule();
		testIterator();
		debugTestIn(testInput);
	}
	// ==========================================TESTING ITERATOR====================================================================
	static void testIterator(){
		Arrays.sort(randomDogs);
		int i = 1;
		System.out.println("========================================");
		System.out.println("Testing for iterator\n");
		System.out.println("ACTUAL");
		for (Dog d: randomDogs) {
			System.out.println(i++ + " -> " + d);
		}
		System.out.println("\n\nYOURS");
		i=1;
		for(Dog d: ds){
			System.out.println(i++ + " -> " + d);
		}
	}
	// ==========================================TESTING VET SCHEDULE====================================================================
	static void testVetSchedule(){
		ArrayList<ArrayList<Dog>> al = new ArrayList<>();
		Arrays.sort(randomDogs);
		for(Dog d: randomDogs){
			int week = (d.getDaysToNextVetAppointment()/7);
			while (week>al.size()-1) al.add(new ArrayList<Dog>());
			al.get(week).add(d);
		}
		System.out.println("========================================");
		System.out.println("Testing for vet schedule\n");
		System.out.println("ACTUAL");
		int i = 0;
		for(ArrayList<Dog> a: al){
			System.out.println(i++ + "->" +a);
		}
		i=0;

		System.out.println("\n\nYOURS");
		al = ds.getVetSchedule();
		for(ArrayList<Dog> a: al){
			System.out.println(i++ + "->" +a);
		}
	}
	// ==========================================TESTING FINDING OLDEST====================================================================
	static void testBudget(int days){
		System.out.println("========================================");
		System.out.println("Within = "+days);
		int len= randomDogs.length, i=0;
		double[] cost = new double[len];
		double c=0.0;
		int[] app = new int[len];
		for(Dog d: randomDogs) {
			cost[i] = d.getExpectedVetCost();
			app[i++] = d.getDaysToNextVetAppointment();
		}
		System.out.println("costs = "+ Arrays.toString(cost));
		System.out.println("Days = "+ Arrays.toString(app));
		i=-1;
		while(++i<len){
			if(app[i]<=days) {
//				System.out.println(cost[i]);
				c += cost[i];
			}
		}
//		int co = (int) Math.round(c*100)/100;
		System.out.println("Actual cost = "+c);
		System.out.println("Cost from your function = "+ds.budgetVetExpenses(days));
	}
	// ==========================================TESTING FINDING OLDEST====================================================================
	static void testFindDogToAdopt(int minAge, int maxAge){
		System.out.println("========================================");
		System.out.println("Dog Found = "+ds.findDogToAdopt(minAge, maxAge)+"\n\n");
	}
	// ==========================================TESTING FINDING OLDEST====================================================================
	static void testFindOldest(){
		System.out.println("========================================");
		System.out.println("Oldest dog = "+ds.findOldest()+"\n\n");
	}

	// ==========================================TESTING FINDING OLDEST====================================================================
	static void testFindYoungest(){
		System.out.println("========================================");
		System.out.println("Youngest dog = "+ds.findYoungest()+"\n\n");
	}

	// ==========================================TESTING REMOVE(CHECK ON YOUR OWN)====================================================================
	static void testRemove(){
		System.out.println("============================");
		System.out.println("removing "+randomDogs[0]+"\n\n");
		ds.adopt(randomDogs[0]);
		ds.root.print2D();
	}
	// ==========================================RANDOM DOGS====================================================================
	static void test1(int howMany){
		randomDogs = generateRandomDogs(howMany);
		ds.root = null;
		System.out.println(Arrays.toString(randomDogs));
		for (Dog d: randomDogs) {
			ds.shelter(d);
		}
		System.out.println("==============initial print==============");
		ds.root.print2D(ds.root, 20);
	}

	// =============================================INPUT TEST DOGS=================================================================
	static void inputTestCases(){
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter the test cases: ");
		String testInput = sc.nextLine();
		ArrayList<Dog> testCases = new ArrayList<Dog>();
		int[] startIndex = {0};
		ds.root = null;
		while(getNextDog(testCases, startIndex, testInput));
		System.out.println(testCases);
		for (Dog d: testCases) {
			ds.shelter(d);
		}
		ds.root.print2D(ds.root, 20);
		randomDogs = (Dog[]) testCases.toArray();
	}

	static void inputTestCases(String testInput){
		ArrayList<Dog> testCases = new ArrayList<Dog>();
		int[] startIndex = {0};
		ds.root = null;
		while(getNextDog(testCases, startIndex, testInput));
		System.out.println(testCases);
		for (Dog d: testCases) {
			ds.shelter(d);
		}
		ds.root.print2D(ds.root, 20);
//		randomDogs = (Dog[]) testCases.toArray();
	}

	static boolean getNextDog(ArrayList<Dog> testCases, int[] startIndex, String testInput){
		if(testInput.charAt(startIndex[0]+1)==']') return false;

		int i = startIndex[0];
		String name = "";
		if(testInput.charAt(i+1)==',') i+=2;
		while(testInput.charAt(++i)!='(') name+=testInput.charAt(i);
		int age = Integer.parseInt(testInput.substring(++i, testInput.indexOf(' ',i)));
		i = testInput.indexOf(',',i)+2;
		int daysAtShelter = Integer.parseInt(testInput.substring(i, testInput.indexOf(')',i)));
		i=testInput.indexOf(')',i);
		startIndex[0]=i;
		Random r = new Random();
		testCases.add(new Dog(name, age, daysAtShelter, r.nextInt(), (((double) r.nextInt(100))/100)+r.nextInt()));
		return true;
	}
// ==================================================DEBUGGING ONE BY ONE============================================================
	static void debugTest1(int howMany){
		Scanner sc = new Scanner(System.in);
		randomDogs = generateRandomDogs(howMany);
		ds.root = null;
		System.out.println(Arrays.toString(randomDogs));
		for (Dog d: randomDogs) {
			System.out.println("\n\n\n\n");
			System.out.println(d);
			ds.shelter(d);
			sc.next();
			ds.root.print2D(ds.root, 20);
		}

	}
	
	static void debugTestIn(String sd){
		Scanner sc = new Scanner(System.in);
		ArrayList<Dog> testCases = new ArrayList<Dog>();
		int[] startIndex = {0};
		ds.root = null;
		while(getNextDog(testCases, startIndex, sd));
		System.out.println(testCases);
		for (Dog d: testCases) {
			System.out.println("\n\n\n\n");
			System.out.println("==========ADDING NEXT==============");
			System.out.println(d);
			System.out.println("===================================");
			ds.shelter(d);
			sc.nextLine();
			ds.root.print2D(ds.root, 20);
			System.out.println("==============ADDED================");
		}
	}
	
	
	// =======================================GENERATING RANDOM DOGS=======================================================================
	static Dog[] generateRandomDogs(int howMany){
		Random r = new Random();
		String[] dogNames = randomDogNameArray(howMany);
		int[] ages = getRandomAges(howMany);
		int[] daysAtShelter = getRandomDaysAtShelter(howMany);
		Dog[] dogs = new Dog[howMany];
		for (int i = 0; i < howMany; i++) {
			dogs[i] = new Dog(dogNames[i], ages != null ? ages[i] : r.nextInt(), daysAtShelter != null ? daysAtShelter[i] : r.nextInt(), r.nextInt(50), (((double) r.nextInt(100))/100)+r.nextInt(20));
		}
		return dogs;
	}

	public static String[] randomDogNameArray(int howMany){
		try {
			String key = "32899195bbb14cb7a4dc723a86c8cd8d";
			String url = "https://randommer.io/api/Name?nameType=firstname&quantity="+howMany;
			HttpRequest request =  HttpRequest.newBuilder().GET()
					.uri(URI.create(url))
					.header("X-Api-key", key)
					.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			JSONArray nameArray = new JSONArray(response.body());
			return JSONStringArrayToStringArray(nameArray);
		} catch (Exception e) {
			System.out.println("There was an error fetching data.");
			e.printStackTrace();
		}
		String[] defaultArr = new String[howMany];
		Arrays.fill(defaultArr, "noName");
		return defaultArr;
	}

	public static String[] JSONStringArrayToStringArray(JSONArray j) throws JSONException {
		String[] ret = new String[j.length()];
		for(int i=0; i<j.length(); i++) ret[i]=j.getString(i);
		return ret;
	}

	public static int[] getRandomAges(int howMany){
		try {
			String url = "http://www.randomnumberapi.com/api/v1.0/random?min=1&max="+(howMany*3+1)+"&count="+(howMany);
			HttpRequest request =  HttpRequest.newBuilder().GET()
					.uri(URI.create(url)).build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			String[] sIntArr = response.body().split(", ");
			sIntArr[0]=sIntArr[0].substring(1);
			sIntArr[sIntArr.length-1]=sIntArr[sIntArr.length-1].substring(0, sIntArr[sIntArr.length-1].length()-1);
			int[] iIntArr = new int[sIntArr.length];
			for (int i = 0; i < sIntArr.length; i++) iIntArr[i]=Integer.parseInt(sIntArr[i]);
			return iIntArr;
		} catch (Exception e) {
			System.out.println("There was an error fetching data.");
			e.printStackTrace();
		}
		return null;
	}

	public static int[] getRandomDaysAtShelter(int howMany){
		try {
			String url = "http://www.randomnumberapi.com/api/v1.0/random?min=1&max="+(30+(howMany*3))+"&count="+(howMany);
			HttpRequest request =  HttpRequest.newBuilder().GET()
					.uri(URI.create(url)).build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			String[] sIntArr = response.body().split(", ");
			sIntArr[0]=sIntArr[0].substring(1);
			sIntArr[sIntArr.length-1]=sIntArr[sIntArr.length-1].substring(0, sIntArr[sIntArr.length-1].length()-1);
			int[] iIntArr = new int[sIntArr.length];
			for (int i = 0; i < sIntArr.length; i++) iIntArr[i]=Integer.parseInt(sIntArr[i]);
			return iIntArr;
		} catch (Exception e) {
			System.out.println("There was an error fetching data.");
			e.printStackTrace();
		}
		return null;
	}
}