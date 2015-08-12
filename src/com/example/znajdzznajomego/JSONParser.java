package com.example.znajdzznajomego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream wejscie = null;
	static JSONObject obiektJSON = null;
	static String json = "";

	// konstruktor
	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(final String url) {

		try {
			DefaultHttpClient klientHttp = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse odpowiedzHttp = klientHttp.execute(httpPost);
			HttpEntity httpEntity = odpowiedzHttp.getEntity();

			wejscie = httpEntity.getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader bufor = new BufferedReader(new InputStreamReader(
					wejscie, "iso-8859-1"), 8);
			
			StringBuilder sb = new StringBuilder();
			String linia = null;

			while ((linia = bufor.readLine()) != null) {
				sb.append(linia + "\n");
			}

			wejscie.close();
			json = sb.toString();

		} catch (Exception e) {
			Log.e("B³¹d bufora", "B³¹d konwersji " + e.toString());
		}

		try {
			obiektJSON = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("Parsowanie JSON", "B³ad parsowania - 1 " + e.toString());
		}
		return obiektJSON;
	}

	// pobiera JSON z adesu URL i tworzy zapytanie metoda POST albo GET
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {
		// proboje stworzyc zapytanie HTTP
		try {
			// sprawdza metode zapytania
			if (method == "POST") {
				// zapytanie motoda POST
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				wejscie = httpEntity.getContent();
			} else if (method == "GET") {
				// zapytanie motoda GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				wejscie = httpEntity.getContent();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					wejscie, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String linia = null;

			while ((linia = reader.readLine()) != null) {
				sb.append(linia + "\n");
			}
			wejscie.close();

			json = sb.toString();
		} catch (Exception e) {
			Log.e("B³¹d bufora", "B³¹d konwersji " + e.toString());
		}

		// Proba parsowania String(json) do obiektu typu JSON
		try {
			obiektJSON = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("Parsowanie JSON", "B³ad parsowania - 2 " + e.toString());
		}
		return obiektJSON;
	}
}
