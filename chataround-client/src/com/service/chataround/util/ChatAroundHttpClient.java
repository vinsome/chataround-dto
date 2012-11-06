package com.service.chataround.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.google.gson.Gson;

public class ChatAroundHttpClient {
	
	public static final String PARAMS_QUERY = "?q1={query}&p2={query}";

	public static <T> T postSpringData(String url, Class<T> responseType,
			Object message) {

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(new MediaType("application", "json"));
		
		Gson gson = new Gson();
		String gmessage = gson.toJson(message);
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(gmessage,
				requestHeaders);

		RestTemplate restTemplate = new RestTemplate();
		
		return restTemplate.postForObject(url, requestEntity, responseType);
	}
	
	

	public static String postData(String url,Map<String,String> values) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		String res = "";
		try {
			// Add your data
			if(!CollectionUtils.isEmpty(values)){
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(values.size());
				Iterator<String> it = values.keySet().iterator();
					while(it.hasNext()) {
						String str = it.next();
						nameValuePairs.add(new BasicNameValuePair(str,values.get(str)));
					}
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					res = inputStreamToString(response.getEntity().getContent())
							.toString();					
			}
		} catch (ClientProtocolException e) {
			Log.e("ChatAroundHttpClient", e.toString());
		} catch (IOException e) {
			Log.e("ChatAroundHttpClient", e.toString());
		}
		return res;
	}

	// see
	// http://androidsnippets.com/executing-a-http-post-request-with-httpclient
	// http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient

	private static StringBuilder inputStreamToString(InputStream is)
			throws IOException {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		while ((line = rd.readLine()) != null) {
			total.append(line);
		}

		// Return full string
		return total;
	}
}
