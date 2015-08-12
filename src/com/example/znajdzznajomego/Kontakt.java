package com.example.znajdzznajomego;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class Kontakt extends Activity {

	private ProgressDialog pDialog;
	private static final String DANE_Z_BAZY_URL = "http://192.168.0.16:80/pobieranie_danych_z_bazy.php";

	private static final String UZYTKOWNIK = "id_uzytkownika";
	private static final String POZYCJE_UZYTKOWNIKOW = "position";
	private static final String SZEROKOSC = "szer_geograficzna";
	private static final String DLUGOSC = "dl_geograficzna";
	private static final String OSTATNIE_LOGOWANIE = "updated_at";

	IkonaKontaktu adapterIkon;

	ListView lista;
	static String czasLogowania;
	private String uzytkownik;
	String grupaKontaktow;
	static String[] uzytkownicyTablica;
	static String[] czasLogTablica;
	static int iloscPozycjiWBazie;
	static String[] szerokoscTablica;
	static String[] dlugoscTablica;
	private JSONArray pozycjeZBazy = null;
	private ArrayList<HashMap<String, String>> pozycjeZBazyLista;

	public static int getIloscPozycjiWBazie() {
		return iloscPozycjiWBazie;
	}

	public static void setIloscPozycjiWBazie(int iloscPozycjiWBazie) {
		Kontakt.iloscPozycjiWBazie = iloscPozycjiWBazie;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kontakty);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new wczytajPozycje().execute();
	}
	//pobieranie danych o kontaktach z bazy
	public void updateJSONdata() {
		pozycjeZBazyLista = new ArrayList<HashMap<String, String>>();

		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(DANE_Z_BAZY_URL);
		try {
			pozycjeZBazy = json.getJSONArray(POZYCJE_UZYTKOWNIKOW);
			setIloscPozycjiWBazie(pozycjeZBazy.length());

			uzytkownicyTablica = new String[getIloscPozycjiWBazie()];
			szerokoscTablica = new String[getIloscPozycjiWBazie()];
			dlugoscTablica = new String[getIloscPozycjiWBazie()];
			czasLogTablica = new String[getIloscPozycjiWBazie()];

			for (int i = 0; i < getIloscPozycjiWBazie(); i++) {
				JSONObject c = pozycjeZBazy.getJSONObject(i);

				uzytkownik = c.getString(UZYTKOWNIK);
				uzytkownicyTablica[i] = uzytkownik;
				String szerokosc = c.getString(SZEROKOSC);
				szerokoscTablica[i] = szerokosc;
				String dlugosc = c.getString(DLUGOSC);
				dlugoscTablica[i] = dlugosc;
				czasLogowania = c.getString(OSTATNIE_LOGOWANIE);
				czasLogTablica[i] = czasLogowania;

				HashMap<String, String> map = new HashMap<String, String>();

				map.put(UZYTKOWNIK, uzytkownik);
				map.put(SZEROKOSC, szerokosc);
				map.put(DLUGOSC, dlugosc);
				map.put(OSTATNIE_LOGOWANIE, czasLogowania);

				pozycjeZBazyLista.add(map);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("Kontkaty - blad", e.toString());
		}
	}
	//zape³nianie listy kontaktów
	private void updateList() {
		lista = (ListView) findViewById(R.id.listView1);
		adapterIkon = new IkonaKontaktu(this, uzytkownicyTablica,
				czasLogTablica);
		
		lista.setAdapter(adapterIkon);
	}

	public class wczytajPozycje extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Kontakt.this);
			pDialog.setMessage("Loading Comments...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			updateJSONdata();
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			updateList();
		}
	}
}
