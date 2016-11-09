import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
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
	
	/**
	 * This application makes sample API calls to the CHPL API:
	 * - shows how to make API calls using org.apache.http
	 * - shows how to parse the JSON response using com.google.gson.jsonparser
	 */
	public SampleApp(){}
	
	public static void main(String[] args) throws IOException, URISyntaxException, JSONException {
		SampleApp sampleApp = new SampleApp();
		
		props = sampleApp.loadProperties(DEFAULT_PROPERTIES_FILE);
		
		/**
		 * Example 1:
		 * a. Call the API /auth/authentication using HTTP POST to get a valid token (note: the authentication token is not necessary for many API calls, including /search)
		 * b. Parse JSON response using com.google.gson
		 * c. Return token as string
		 */
		String token = sampleApp.getToken();
		
		/**
		 * Example 2: 
		 * a. Call the API /search using HTTP GET to get all Certified Product Details
		 * b. Parse the JSON using org.json to get only information keyed on "product".
		 * c. Parse the "product" JSON to CSV format
		 * d. Write the csv data to a file
		 */
		String searchResult = sampleApp.getSearchResult();
		JSONObject search = new JSONObject(searchResult);
		JSONArray searchArr = search.getJSONArray("results");
		JSONObject products = new JSONObject();
		// Get product JSONObject with the CP_id as "id", then the "product" names/values: "versionId", "name", "id", "version"
		for (Integer i = 0; i < searchArr.length(); i++) {
		      if(searchArr.optJSONObject(i).has("product")){
		    	  products.put(searchArr.optJSONObject(i).get("id").toString(), searchArr.optJSONObject(i).get("product"));
		      }
		 }

		String csv = CDL.toString(products.toJSONArray(products.names()));
		File file = new File("products.csv");
		FileUtils.writeStringToFile(file, csv);
		
		/**
		 * Example 3: 
		 * a. call the API /search to get all Certified Product Details
		 * b. Save results to a .txt file
		 */
		sampleApp.getSearchResult_saveResultsInFile();
	}
	
	/**
	 * Uses HTTP POST to get an authenticated token to be passed for future API calls that require an authenticated user.
	 * Uses the Properties from environment.properties to obtain username, password, and API-key
	 * Parses json response and returns the value of the token from the json
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws URISyntaxException
	 * @throws PropertyException 
	 * @throws UnsupportedEncodingException 
	 */
	public String getToken() throws ClientProtocolException, IOException {
		String tokenResponse = Request.Post(props.getProperty("targetHost") + props.getProperty("authenticate"))
		.bodyString("{ \"userName\": \"" + props.getProperty("username") + "\","
				+ " \"password\": \"" + props.getProperty("password") + "\" }", ContentType.APPLICATION_JSON)
		.version(HttpVersion.HTTP_1_1)
		.addHeader("Content-Type", "application/json")
		.addHeader("API-key", props.getProperty("apiKey"))
		.execute().returnContent().asString();
		JsonObject jobj = new Gson().fromJson(tokenResponse, JsonObject.class);
		String token = jobj.get("token").toString();
		return token;
	}
	
	/**
	 * Uses HTTP GET to obtain the JSON response as a string from the /search?searchTerm= API call
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String getSearchResult() throws ClientProtocolException, IOException{
		String searchResult = Request.Get(props.getProperty("targetHost") + props.getProperty("search"))
				.version(HttpVersion.HTTP_1_1)
				.addHeader("Content-Type", "application/json")
				.addHeader("API-key", props.getProperty("apiKey"))
				.execute().returnContent().asString();
		return searchResult;
	}

	/**
	 * Uses HTTP GET to obtain the JSON response from the /search?searchTerm= API call and save as a csv
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public void getSearchResult_saveResultsInFile() throws ClientProtocolException, IOException{
		Request.Get(props.getProperty("targetHost") + props.getProperty("search"))
				.version(HttpVersion.HTTP_1_1)
				.addHeader("Content-Type", "application/json")
				.addHeader("API-key", props.getProperty("apiKey"))
				.execute().saveContent(new File("fullResults.txt"));
	}
	
	/**
	 * Utility method to load Properties from environment.properties file.
	 * @param filePathAndName
	 * @return
	 */
	public Properties loadProperties(
		      final String filePathAndName)
		   {
		      final Properties properties = new Properties();
		      try
		      {
		         final InputStream in = SampleApp.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE);
		         properties.load(in);
		         in.close();
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
