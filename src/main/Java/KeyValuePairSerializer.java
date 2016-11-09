import java.io.IOException;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class KeyValuePairSerializer extends TypeAdapter<List<BasicNameValuePair>> {
	@Override
	public void write(JsonWriter out, List<BasicNameValuePair> data) throws IOException {
	    out.beginObject();
	    for(int i=0; i<data.size();i++){
	        out.name(data.get(i).getName());
	        out.value(data.get(i).getValue());
	    }
	    out.endObject();
	}
	
	/*For serialization purposes only*/
	@Override
	public List<BasicNameValuePair> read(JsonReader in) throws IOException {
	    return null;
	}
}