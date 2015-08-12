package com.example.znajdzznajomego;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogIn extends Activity implements OnClickListener {

	private TextView ekran_rejestracji;
	private Button przyciskZaloguj;
	static EditText wpiszLogin;
	private EditText wpiszHaslo;
	private ProgressDialog pDialog;
	private Intent intencjaReg;
	private Intent intencjaMap;
	private ProbaLogowania logowanie;
	public String nickLogin;
	public String uzytkownik;

	JSONParser jsonParser = new JSONParser();
	private static final String LOGIN_URL = "http://192.168.0.16:80/login.php";
	private static final String POWODZENIE = "success";
	private static final String WIADOMOSC = "message";

	public static EditText getWpiszLogin() {
		return wpiszLogin;
	}

	public static void setWpiszLogin(EditText wpiszLogin) {
		LogIn.wpiszLogin = wpiszLogin;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);

		super.onCreate(savedInstanceState);

		// przejœcie pomiêdzy logowaniem a rejestracj¹
		ekran_rejestracji = (TextView) findViewById(R.id.textLink_register);
		ekran_rejestracji.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				intencjaReg = new Intent(getApplicationContext(), Reg.class);
				startActivity(intencjaReg);
			}
		});

		przyciskZaloguj = (Button) findViewById(R.id.buttonLog);
		setWpiszLogin((EditText) findViewById(R.id.editTextNick));
		wpiszHaslo = (EditText) findViewById(R.id.editTextPass);

		// nasluchiwacze
		przyciskZaloguj.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		logowanie = new ProbaLogowania();
		logowanie.execute();
	}

	class ProbaLogowania extends AsyncTask<String, String, String> {

		boolean wpadka = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LogIn.this);
			pDialog.setMessage("Logowanie...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			String login = getWpiszLogin().getText().toString();
			String haslo = wpiszHaslo.getText().toString();
			int powodzenie;

			try {
				// Parametry
				List<NameValuePair> parametry = new ArrayList<NameValuePair>();
				parametry.add(new BasicNameValuePair("username", login));
				parametry.add(new BasicNameValuePair("password", haslo));

				// wysylanie danych uzytkownika
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						parametry);

				powodzenie = json.getInt(POWODZENIE);

				if (powodzenie == 1) {
					intencjaMap = new Intent(LogIn.this, Map.class);
					finish();
					startActivity(intencjaMap);
					return json.getString(WIADOMOSC);
				} else {
					Log.d("Blad rejestracji", json.getString(WIADOMOSC));
					return json.getString(WIADOMOSC);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return login;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(LogIn.this, file_url, Toast.LENGTH_LONG).show();
			}
		}
	}
}
