package com.example.znajdzznajomego;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Reg extends Activity implements OnClickListener {

	private EditText wpiszEmail;
	private EditText wpiszLogin;
	private EditText wpiszHaslo;
	private Button zarejestruj;
	private Intent intencjaLogowania;
	private stworzUzytkownika nowyUzytkownik;
	private TextView ekran_logowania;
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	// URL bazy danych
	private static final String LOGIN_URL = "http://192.168.0.16:80/register.php";
	private static final String POWODZENIE = "success";
	private static final String WIADOMOSC = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);

		ekran_logowania = (TextView) findViewById(R.id.textLink_login);

		ekran_logowania.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// zamkniecie ekranu rejestracji
				finish();
			}
		});

		wpiszEmail = (EditText) findViewById(R.id.editEmail);
		wpiszLogin = (EditText) findViewById(R.id.editTextNick);
		wpiszHaslo = (EditText) findViewById(R.id.editTextPass);
		zarejestruj = (Button) findViewById(R.id.buttonReg);
		zarejestruj.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		nowyUzytkownik = new stworzUzytkownika();
		nowyUzytkownik.execute();
	}

	class stworzUzytkownika extends AsyncTask<String, String, String> {
		boolean wpadka = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Reg.this);
			pDialog.setMessage("Tworzenie u¿ytkownika...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			String email = wpiszEmail.getText().toString();
			String login = wpiszLogin.getText().toString();
			String haslo = wpiszHaslo.getText().toString();
			int powodzenie;
			try {
				// Parametry
				List<NameValuePair> parametry = new ArrayList<NameValuePair>();
				parametry.add(new BasicNameValuePair("email", email));
				parametry.add(new BasicNameValuePair("username", login));
				parametry.add(new BasicNameValuePair("password", haslo));
				
				// wysylanie danych uzytkownika
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						parametry);

				powodzenie = json.getInt(POWODZENIE);

				if (powodzenie == 1) {
					finish();
					return json.getString(WIADOMOSC);
				} else {
					Log.d("Blad rejestracji", json.getString(WIADOMOSC));
					return json.getString(WIADOMOSC);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(Reg.this, file_url, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(Reg.this, "U¿ytkownik zarejestrowany",
						Toast.LENGTH_LONG).show();
				
				intencjaLogowania = new Intent(getApplicationContext(),
						LogIn.class);
				
				startActivity(intencjaLogowania);
				Toast.makeText(Reg.this, "Zaloguj siê", Toast.LENGTH_LONG)
						.show();
			}
		}
	}
}
