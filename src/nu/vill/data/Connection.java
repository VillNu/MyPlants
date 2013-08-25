/*
 * Connection.java
 * 
 * Copyright (C) 2013 Magnus Duberg 
 *
 * Licensed under the Want Now License, Version 0.q (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://vill.nu/licenses/WANT-NOW-LICENSE-0.q
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package nu.vill.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * This class provides an object which query() method accept a http url
 * and will return a string with the response
 * @author Magnus
 */
public class Connection {

	private static final String TAG = "Connection";

	/**
	 * The class doesn't hold any variables since they're anyway recreated every time
	 * the query method is called.
	 */
	public Connection(){
	}
	
	// TODO Implement guidelines of http://source.android.com/source/code-style.html#javatests-style-rules
	// TODO Implement RESTful, and make use of different HTTP status codes for different CRUD statements 
	/**
	 * Creates a connection to a web site and collects the content
	 * @param accepts a well formed url
	 * @return returns a string with the result of the url
	 */
	public String query(String url){

		String response = "";
		
		try { // using http GET retrieve the data for the specific url
			HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) { // OK
				HttpEntity entity = httpResponse.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content),1024); // can buffer 1024 characters
				String line;
				StringBuilder stringBuilder = new StringBuilder(); 
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				response = stringBuilder.toString();
				Log.v(TAG, "Url: "+url+" returned "+response.length()+" characters.");
			} else {
				Log.w(TAG, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

}
