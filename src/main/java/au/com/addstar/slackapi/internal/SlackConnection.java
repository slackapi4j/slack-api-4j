package au.com.addstar.slackapi.internal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class SlackConnection
{
	private String token;
	public SlackConnection(String token)
	{
		this.token = token;
	}
	
	private String encodeRequest(Map<String, String> params)
	{
		try
		{
			StringBuilder data = new StringBuilder();
			data.append("token=");
			data.append(URLEncoder.encode(token, "UTF-8"));
			for (Entry<String, String> param : params.entrySet())
			{
				data.append('&');
				data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				data.append('=');
				data.append(URLEncoder.encode(param.getValue(), "UTF-8"));
			}
			
			return data.toString();
		}
		catch (UnsupportedEncodingException e)
		{
			// Should never happen
			throw new AssertionError();
		}
	}
	
	private HttpsURLConnection createConnection(String method, Map<String, String> params) throws IOException, MalformedURLException
	{
		try
		{
			URL queryUrl = new URL("https", SlackConstants.HOST, "/api/" + method);
			
			HttpsURLConnection connection = (HttpsURLConnection)queryUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			// Add request params
			String request = encodeRequest(params);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			writer.write(request);
			writer.close();
			
			return connection;
		}
		catch ( ProtocolException e )
		{
			// Should not happen
			throw new AssertionError();
		}
	}
	
	public JsonElement callMethod(String method, Map<String, String> params)
	{
		try
		{
			HttpsURLConnection connection = createConnection(method, params);
			connection.connect();
			
			JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			
			JsonParser parser = new JsonParser();
			JsonElement result = parser.parse(reader);
			
			reader.close();
			
			return result;
		}
		catch (IOException e)
		{
			// TODO: Handle this
			return null;
		}
	}
}
