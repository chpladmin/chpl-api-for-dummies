import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SampleApp {
	private static final String DEFAULT_PROPERTIES_FILE = "environment.properties";
	private static final Logger logger = LogManager.getLogger(SampleApp.class);
	private static Properties props;
	public static final Integer ALL_EXAMPLES = 0;
	public static final Integer FIRST_EXAMPLE = 1;
	public static final Integer SECOND_EXAMPLE = 2;
	public static final Integer THIRD_EXAMPLE = 3;
	public static final Integer FOURTH_EXAMPLE = 4;
	public static final Integer EXIT_APP = 9;
	public static final Integer FIRST_ITERATION = 99;
	
	/**
	 * This application makes sample API calls to the CHPL API:
	 * - shows how to make API calls using org.apache.http
	 * - shows how to parse the JSON response using com.google.gson.jsonparser
	 */
	public SampleApp(){}
	
	public static void main(String[] args) throws IOException, URISyntaxException, JSONException {
		SampleApp sampleApp = new SampleApp();
		String token = null;
		Integer input = FIRST_ITERATION;
		Scanner scanner = new Scanner(System.in);
		
		while(input != EXIT_APP){
			props = sampleApp.loadProperties(DEFAULT_PROPERTIES_FILE);
			if(input == FIRST_ITERATION){
				sampleApp.displayInstructions();
			}
			else {
				System.out.println("\n-------------------------------------------------\n"
						+ "Please hit 'Enter' to redisplay instructions");
				scanner.nextLine();
				sampleApp.displayInstructions();
			}
			
			input = sampleApp.getValidUserInput(scanner);
			
			if(input == ALL_EXAMPLES || input == FIRST_EXAMPLE || input == FOURTH_EXAMPLE){
				// This token could be used in an API call that requires authentication. 
				token = sampleApp.getToken();
			}
			
			/**
			 * Example 2: 
			 * a. Call the API /search using HTTP GET to get all Certified Product Details
			 * b. Parse the JSON using org.json to get only information keyed on "product"
			 * c. Parse the "product" JSON to CSV format
			 * d. Write the csv data to a file
			 */
			if(input == ALL_EXAMPLES || input == SECOND_EXAMPLE){
				String searchResult = sampleApp.getSearchResult();
				if(searchResult != null){
					JSONObject search = new JSONObject(searchResult);
					JSONArray searchArr = search.getJSONArray("results");
					JSONObject products = new JSONObject();
					
					for (Integer i = 0; i < searchArr.length(); i++) {
					      if(searchArr.optJSONObject(i).has("product")){
					    	  products.put(searchArr.optJSONObject(i).get("id").toString(), searchArr.optJSONObject(i).get("product"));
					      }
					 }
					
					System.out.println("Parsed search JSONArray to retrieve products as follows: \n" + products.toString());
					JSONArray productNames = products.toJSONArray(products.names());
					System.out.println("Parsed certified products JSON to get products as follows: \n" + productNames.toString());
					String csv = CDL.toString(productNames);
					System.out.println("Parsed products JSON to CSV as follows: \n" + csv);
					File file = new File("products.csv");
					FileUtils.writeStringToFile(file, csv);
					System.out.println("Saved product information to products.csv");
				}
				else{
					System.out.println("searchResult is null. Skipping creation of CSV.");
				}
			}

			if(input == THIRD_EXAMPLE || input == ALL_EXAMPLES){
				sampleApp.getSearchResult_saveResultsInFile();
			}

			if(input == FOURTH_EXAMPLE || input == ALL_EXAMPLES){
				if(token != null){
					sampleApp.getSearchOptions_usesAuthentication(token);
				}
				else{
					System.out.println("\nFailed to run example 4 because the token is null. Please provide a valid token before running the fourth example.\n"
							+ "Please check the values in environment.properties");
				}
			}
		}
	}
	
	/**
	 * Displays instructions on how to use the CHPL Sample Application
	 */
	public void displayInstructions(){
		System.out.println("\nWelcome to the CHPL Sample Application! The purpose of this application is to provide the following examples: \n");
		
		System.out.println("Example 1: ");
		System.out.println("a. Call the API /auth/authentication using HTTP POST to get a valid token");
		System.out.println("(note: the authentication token is not necessary for many API calls, including /search)");
		System.out.println("b. Parse JSON response using com.google.gson");
		System.out.println("c. Return token as string\n");
		
		System.out.println("Example 2: ");
		System.out.println("a. Call the API /search using HTTP GET to get all Certified Product Details");
		System.out.println("b. Parse the JSON using org.json to get only information keyed on \"product\"");
		System.out.println("c. Parse the \"product\" JSON to CSV format");
		System.out.println("d. Write the csv data to a file\n");
		
		System.out.println("Example 3: ");
		System.out.println("a. call the API /search to get all Certified Product Details");
		System.out.println("b. Save results to a .txt file\n");
		
		System.out.println("Example 4: ");
		System.out.println("a. Call the API /data/search_options using token for authentication\n");
		
		System.out.println("NOTE: before running, please configure the environment.properties file\n");
		
		System.out.println("For example 1, please enter '1' into the console");
		System.out.println("For example 2, please enter '2' into the console");
		System.out.println("For example 3, please enter '3' into the console");
		System.out.println("For example 4, please enter '4' into the console");
		System.out.println("To perform all 4 examples, please enter '0' into the console");
		System.out.println("To exit the application, please enter '9' into the console\n");
	}
	
	/**
	 * Returns a valid integer from the console
	 * @return integer representing user input
	 * @throws IOException for incorrect user console input
	 * @param scanner for input
	 */
	public Integer getValidUserInput(Scanner scanner) throws IOException{
		Integer inputInt = FIRST_ITERATION;
		int counter = 0;
		while(inputInt != EXIT_APP && inputInt != ALL_EXAMPLES && inputInt != FIRST_EXAMPLE 
				&& inputInt != SECOND_EXAMPLE && inputInt != THIRD_EXAMPLE && inputInt != FOURTH_EXAMPLE){
			if(counter > 0){
				System.out.println("Invalid user input. Please enter an integer of " + ALL_EXAMPLES + ", " + 
						FIRST_EXAMPLE + ", " + SECOND_EXAMPLE + ", " + THIRD_EXAMPLE + ", " + FOURTH_EXAMPLE + " or " + EXIT_APP);
			}
			try{
				inputInt = scanner.nextInt();
				counter++;
			} catch(InputMismatchException e){
				throw new InputMismatchException("Invalid user input. Please enter an integer of " + ALL_EXAMPLES + ", " + 
						FIRST_EXAMPLE + ", " + SECOND_EXAMPLE + ", " + THIRD_EXAMPLE + ", " + FOURTH_EXAMPLE + " or " + EXIT_APP);
			}
		}
		scanner.nextLine(); //throw away the \n not consumed by nextInt()
		
		if(inputInt == EXIT_APP){
			System.out.println("Exiting application...");
			System.exit(0);
		}
		
		return inputInt;
	}
	
	/**
	 * Example 1:
	 * a. Call the API /auth/authentication using HTTP POST to get a valid token (note: the authentication token is not necessary for many API calls, including /search)
	 * b. Parse JSON response using com.google.gson
	 * c. Return token as string 
	 * 
	 * Uses HTTP POST to call /auth/authenticate and get an authenticated token to be passed for future API calls that require an authenticated user.
	 * Uses the Properties from environment.properties to obtain username, password, and API-key
	 * Parses json response and returns the value of the token from the json
	 * @return String with the token
	 */
	public String getToken() {
		System.out.println("\nRunning example 1 to get token for authentication:");
		System.out.println("Making REST HTTP POST call to " + props.getProperty("targetHost") + props.getProperty("authenticate") + 
				" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "...");
		String token = null;
		
		try{
			String tokenResponse = Request.Post(props.getProperty("targetHost") + props.getProperty("authenticate"))
					.bodyString("{ \"userName\": \"" + props.getProperty("username") + "\","
							+ " \"password\": \"" + props.getProperty("password") + "\" }", ContentType.APPLICATION_JSON)
					.version(HttpVersion.HTTP_1_1)
					.addHeader("Content-Type", "application/json")
					.addHeader("API-key", props.getProperty("apiKey"))
					.execute().returnContent().asString();
					JsonObject jobj = new Gson().fromJson(tokenResponse, JsonObject.class);
					System.out.println("Retrieved the following JSON from /auth/authenticate: \n" + jobj.toString());
					token = jobj.get("token").toString();
					System.out.println("Retrieved token " + token);
					return token;
		} catch (IOException e){
			System.out.println("Failed to make call to " + props.getProperty("targetHost") + props.getProperty("authenticate") + 
					" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "...");
			System.out.println("Please check that the targetHost and authenticate properties are configured correctly in environment.properties");
		}
		return token;
	}
	
	/**
	 * Example 2:
	 * 
	 * Uses HTTP GET to obtain the JSON response as a string from the /search?searchTerm= API call
	 * @return String with the searchResult
	 */
	public String getSearchResult() {
		String searchResult = null;
		System.out.println("\nRunning example 2 to search for certified products:");
		System.out.println("Making REST HTTP GET call to " + props.getProperty("targetHost") + props.getProperty("search") + 
				" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "...");
		try{
			searchResult = Request.Get(props.getProperty("targetHost") + props.getProperty("search"))
					.version(HttpVersion.HTTP_1_1)
					.addHeader("Content-Type", "application/json")
					.addHeader("API-key", props.getProperty("apiKey"))
					.execute().returnContent().asString();
		} catch(IOException e){
			System.out.println("Failed to make call to " + props.getProperty("targetHost") + props.getProperty("search") + 
					" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "...");
			System.out.println("Please check that the targetHost and search properties are configured correctly in environment.properties");
		}
		
		System.out.println("Retrieved " + props.getProperty("targetHost") + props.getProperty("search") + " JSON as follows: \n" + searchResult);
		return searchResult;
	}

	/**
	 * Example 3: 
	 * a. call the API /search to get all Certified Product Details
	 * b. Save results to a .txt file 
	 * 
	 * Uses HTTP GET to obtain the JSON response from the /search?searchTerm= API call and save as a csv
	 */
	public void getSearchResult_saveResultsInFile(){
		System.out.println("\nRunning example 3 to search for certified products and save JSON in a text file:");
		System.out.println("Making REST HTTP GET call to " + props.getProperty("targetHost") + props.getProperty("search") + 
				" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "..." + " and saving JSON to fullResults.txt");
		try{
			Request.Get(props.getProperty("targetHost") + props.getProperty("search"))
			.version(HttpVersion.HTTP_1_1)
			.addHeader("Content-Type", "application/json")
			.addHeader("API-key", props.getProperty("apiKey"))
			.execute().saveContent(new File("fullResults.txt"));
		} catch (IOException e){
			System.out.println("Failed to make call to " + props.getProperty("targetHost") + props.getProperty("search") + 
					" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "...");
			System.out.println("Please check that the targetHost and search properties are configured correctly in environment.properties");
		}
		
		
	}
	
	/**
	 * Example 4:
	 * a. Call the API /data/search_options using token for authentication. Note that authentication is not required for this API call.
	 * 
	 * Uses HTTP GET to obtain the JSON response from the /data/search_options API call. Uses token for authentication
	 * @param token for use in API authentication calls
	 */
	public void getSearchOptions_usesAuthentication(String token){
		System.out.println("\nRunning example 4 to call /data/search_options using a token for authentication: ");
		if(token == null){
			System.out.println("Token is null. Please provide a valid token for authentication.");
			System.exit(0);
		}
		System.out.println("Making REST HTTP GET call to " + props.getProperty("targetHost") + props.getProperty("searchOptions") + 
				" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "...");
		
		try{
			String searchResult = Request.Get(props.getProperty("targetHost") + props.getProperty("searchOptions"))
					.version(HttpVersion.HTTP_1_1)
					.addHeader("Content-Type", "application/json")
					.addHeader("API-key", props.getProperty("apiKey"))
					.addHeader("Authorization", "Bearer " + token)
					.execute().returnContent().asString();
			System.out.println("Retrieved result of " + props.getProperty("targetHost") + props.getProperty("searchOptions") + " as follows: \n" + searchResult);
		} catch (IOException e){
			System.out.println("Failed to make call to " + props.getProperty("targetHost") + props.getProperty("searchOptions") + 
					" using API-key=" + props.getProperty("apiKey").substring(0, 8) + "...");
			System.out.println("Please check that the targetHost and searchOptions properties are configured correctly in environment.properties");
		}
	}
	
	/**
	 * Utility method to load Properties from environment.properties file.
	 * @param filePathAndName location of environment.properties file
	 * @return Properties necessary for user credentials, API-key and API calls
	 */
	public Properties loadProperties(final String filePathAndName){
      final Properties properties = new Properties();
      try
      {
         final InputStream in = SampleApp.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE);
         properties.load(in);
         in.close();
         System.out.println("Loaded Properties from Environment.properties");
      }
      catch (FileNotFoundException fnfEx)
      {
         System.err.println("Could not read properties from file " + filePathAndName);
      }
      catch (IOException ioEx)
      {
         System.err.println(
            "IOException encountered while reading from " + filePathAndName);
      }
      return properties;
   }
}
