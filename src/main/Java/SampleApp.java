import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		SampleApp sampleApp = new SampleApp();
		
		props = sampleApp.loadProperties(DEFAULT_PROPERTIES_FILE);
		
		// call the API authentication to get a valid token (this is not necessary for many API calls, including /search)
		String token = sampleApp.getToken();
		//HttpResponse searchResponse = sampleApp.http(URLDecoder.decode(props.getProperty("search"), "UTF-8"));
		//System.out.println(searchResponse.toString());
	}
	
	/**
	 * Gets an authenticated token to be passed for future API calls requiring an authenticated user
	 * Uses the Properties from environment.properties to obtain username, password, and API-key
	 * Parses json response and returns the value of the token from the json
	 * @return
	 * @throws URISyntaxException
	 * @throws PropertyException 
	 * @throws UnsupportedEncodingException 
	 */
	public String getToken() {
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try{
			if((props.getProperty("targetHost") != null && !props.getProperty("targetHost").isEmpty())
					&& (props.getProperty("authenticate") != null && !props.getProperty("authenticate").isEmpty())){
				HttpPost httpPost = new HttpPost(props.getProperty("targetHost") + props.getProperty("authenticate"));
				List<NameValuePair> nvps = new ArrayList <NameValuePair>();
				if((props.getProperty("username") != null && !props.getProperty("username").isEmpty())
						&& (props.getProperty("password") != null && !props.getProperty("password").isEmpty())){
					
					nvps.add(new BasicNameValuePair("username", props.getProperty("username")));
					nvps.add(new BasicNameValuePair("password", props.getProperty("password")));	
					
					GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(KeyValuePairSerializer.class, new KeyValuePairSerializer());
					Gson gson = gsonBuilder.create();
					logger.debug("json serialization result: " + gson.toJson(nvps, KeyValuePairSerializer.class));
					httpPost.setEntity(new StringEntity(gson.toJson(nvps, KeyValuePairSerializer.class), ContentType.APPLICATION_JSON));
					httpPost.setHeader("Content-Type", "application/json");
					if(props.getProperty("apiKey") != null && !props.getProperty("apiKey").isEmpty()){
						httpPost.setHeader("API-key", props.getProperty("apiKey"));
						response = client.execute(httpPost);
					}
					else{
						throw new PropertyException("apiKey property cannot be null or empty");
					}
				}
			}
			else{
				throw new PropertyException("targetHost property cannot be null or empty");
			}
		}
		catch(IOException | PropertyException ex){
			System.out.println(ex);
		}
		
		try{
			// get json entity, etc
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
//		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
//			URI authenticateUri = new URIBuilder()
//					.setScheme("http")
//					.setHost(props.getProperty("host"))
//					.setPath(props.getProperty("authenticate"))
//					.build();
//            HttpPost request = new HttpPost(authenticateUri);
//
//            request.addHeader("Content-Type", "application/json");
//            request.addHeader("API-key", props.getProperty("apiKey"));
//            
//            List<NameValuePair> credParams = new ArrayList<NameValuePair>();
//            credParams.add(new BasicNameValuePair("username", props.getProperty("username")));
//            credParams.add(new BasicNameValuePair("password", props.getProperty("password")));
//            request.setEntity(new UrlEncodedFormEntity(credParams));
//            
//            HttpResponse result = httpClient.execute(request);
//            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
//            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
//            return jsonObject.get("token").getAsString();
//        } catch (IOException ex) {
//        	System.out.println(ex);
//        }
//        return null;
	}
	
	public HttpResponse http(String path) throws URISyntaxException {
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			URI authenticateUri = new URIBuilder()
					.setScheme("http")
					.setHost(props.getProperty("host"))
					.setPort(Integer.parseInt(props.getProperty("port")))
					.setPath(path)
					.build();
            HttpPost request = new HttpPost(authenticateUri);
            //StringEntity params = new StringEntity(jsonParams);
            request.addHeader("content-type", "application/json");
            request.addHeader("API-key", props.getProperty("apiKey"));
            //request.setEntity(params);
            System.out.println("URL=" + authenticateUri.getHost() + URLDecoder.decode(props.getProperty("search") + "?searchTerm=", "UTF-8"));
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
