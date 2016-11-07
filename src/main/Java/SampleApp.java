import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SampleApp {
	private static final String DEFAULT_PROPERTIES_FILE = "environment.properties";
	private static final Log logger = LogFactory.getLog(SampleApp.class);
	private static Properties props;
	
	/**
	 * This application makes sample API calls to the CHPL API:
	 * - shows how to make API calls using org.apache.http
	 * - shows how to parse the JSON response using com.google.gson.jsonparser
	 */
	public SampleApp(){}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		SampleApp sampleApp = new SampleApp();
		props = sampleApp.loadProperties(DEFAULT_PROPERTIES_FILE);
		
		// call the API authentication to get a valid token (this is not necessary for many API calls, including /search)
		//String token = sampleApp.getToken();
		HttpResponse searchResponse = sampleApp.http(URLDecoder.decode(props.getProperty("search") + "?searchTerm=", "UTF-8"));
		System.out.println(searchResponse.toString());
	}
	
	/**
	 * Gets an authenticated token to be passed for future API calls requiring an authenticated user
	 * Uses the Properties from environment.properties to obtain username, password, and API-key
	 * Parses json response and returns the value of the token from the json
	 * @return
	 * @throws URISyntaxException
	 */
	public String getToken() throws URISyntaxException{
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			URI authenticateUri = new URIBuilder()
					.setScheme("http")
					.setHost(props.getProperty("host"))
					.setPath(props.getProperty("authenticate"))
					.build();
            HttpPost request = new HttpPost(authenticateUri);
            StringEntity params = new StringEntity("{ \"userName\": \"" + props.getProperty("usernameLocal") + 
            		"\", \"password\": \"" + props.getProperty("passwordLocal") + "\" }");
            request.addHeader("content-type", "application/json");
            request.addHeader("API-key", props.getProperty("apiKey"));
            request.setEntity(params);
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            return jsonObject.get("token").getAsString();
        } catch (IOException ex) {
        	System.out.println(ex);
        }
        return null;
	}
	
	public HttpResponse http(String path) throws URISyntaxException {
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			URI authenticateUri = new URIBuilder()
					.setScheme("http")
					.setHost(props.getProperty("host"))
					.setPath(path)
					.build();
            HttpPost request = new HttpPost(authenticateUri);
            //StringEntity params = new StringEntity(jsonParams);
            request.addHeader("content-type", "application/json");
            request.addHeader("API-key", props.getProperty("apiKey"));
            //request.setEntity(params);
            HttpResponse result = httpClient.execute(request);
            return result;
        } catch (IOException ex) {
        	System.out.println(ex);
        }
        return null;
	}
	
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
