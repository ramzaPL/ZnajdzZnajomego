package com.example.znajdzznajomego;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends Activity implements LocationListener {

	private GoogleMap mapa;
	static final LatLng LUBLIN = null, dl = null, szer = null;
	private LatLng tuJestem;
	private CameraPosition pozycjaMapy;
	private CameraUpdate podazanie;
	private LocationManager locationManager;
	private Location location;
	private Criteria kryteria;
	String najlepszyDostawca;
	private Button przyciskListaKontaktow;
	private Intent iKontakty;

	Kontakt kontakty;

	private ProgressDialog pDialog;
	private static final String LOGIN_URL = "http://192.168.0.16:80/pozycja.php";
	private static final String POWODZENIE = "success";
	private static final String WIADOMOSC = "message";
	JSONParser jsonParser = new JSONParser();
	private wysylaniePolozenia polozenieDoBazy;
	String loginMap;

	String czasOstatniejAktywnosciString;
	long czasOstatniejAktywnosciLong;
	Date dataZBazy;
	Date teraz;
	long terazLong;
	static final long TRZYDZIESCI_MINUT = 1800000;

	private int potwierdzenie = 0;

	// metoda aktualizujaca polozenie
	public void odswiezLokalizacje() {
		najlepszyDostawca = locationManager.getBestProvider(kryteria, true);
		location = locationManager.getLastKnownLocation(najlepszyDostawca);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// obiekt GoogleMap
		mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mapa.getUiSettings().setZoomControlsEnabled(true);

		// LatLng LUBLIN = new LatLng (51.218109, 22.5637702);

		kryteria = new Criteria();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		odswiezLokalizacje();
		locationManager.requestLocationUpdates(najlepszyDostawca, 1000 * 5, 5, this);

		przyciskListaKontaktow = (Button) findViewById(R.id.buttonZnajomi);
		OnClickListener nasluchiwaczKontakty = new OnClickListener() {

			@Override
			public void onClick(View v) {
				iKontakty = new Intent(Map.this, Kontakt.class);
				startActivity(iKontakty);
			}
		};
		przyciskListaKontaktow.setOnClickListener(nasluchiwaczKontakty);
	}

	class wysylaniePolozenia extends AsyncTask<String, String, String> {

		boolean wpadka = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.this);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			loginMap = LogIn.wpiszLogin.getText().toString();

			String szerokoscBaza = Double.toString(location.getLatitude());
			String dlugoscBaza = Double.toString(location.getLongitude());

			int powodzenie;
			try {
				// Parametry
				List<NameValuePair> parametry = new ArrayList<NameValuePair>();
				// parametry.add(new BasicNameValuePair("email", email));
				parametry.add(new BasicNameValuePair("username", loginMap));
				parametry.add(new BasicNameValuePair("szerokosc", szerokoscBaza));
				parametry.add(new BasicNameValuePair("dlugosc", dlugoscBaza));

				// wysylanie danych uzytkownika
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						parametry);
				
				powodzenie = json.getInt(POWODZENIE);

				if (powodzenie == 1) {
					//i = 0;
					if (potwierdzenie == 0) {
						return json.getString(WIADOMOSC);
					}				
				} else {
					Log.d("Mapa - b³¹d ", json.getString(WIADOMOSC));
					return json.getString(WIADOMOSC);
				}
				potwierdzenie++;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(Map.this, file_url, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		odswiezLokalizacje();
		
		mapa.setMyLocationEnabled(true);

		// ustalenie polozenia
		tuJestem = new LatLng(location.getLatitude(), location.getLongitude());
		// najazd kamery na ustalone polozenie
		pozycjaMapy = new CameraPosition.Builder().target(tuJestem).zoom(20)
				.build();
		podazanie = CameraUpdateFactory.newCameraPosition(pozycjaMapy);
		mapa.animateCamera(podazanie);

		polozenieDoBazy = new wysylaniePolozenia();
		polozenieDoBazy.execute();

		SimpleDateFormat formatDaty = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.ENGLISH);

		for (int j = 0; j < Kontakt.getIloscPozycjiWBazie(); j++) {
			czasOstatniejAktywnosciString = Kontakt.czasLogTablica[j];
			try {
				dataZBazy = formatDaty.parse(czasOstatniejAktywnosciString);
				czasOstatniejAktywnosciLong = dataZBazy.getTime();
				terazLong = System.currentTimeMillis();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			// porownuje nazwy uzytkownikow aby przy odpowiendim zmienic ikone
			// nie zmienia przy aktualnie zalogowanym
			if ((terazLong - czasOstatniejAktywnosciLong) <= TRZYDZIESCI_MINUT) {
				if (Kontakt.uzytkownicyTablica[j].equals(loginMap)) {
				} else {
					String szerokoscUzytkonikaZBazy = Kontakt.szerokoscTablica[j];
					String dlugoscUzytkonikaZBazy = Kontakt.dlugoscTablica[j];
					LatLng pozycjaUzytkownikaZBazy = new LatLng(
							Double.parseDouble(szerokoscUzytkonikaZBazy),
							Double.parseDouble(dlugoscUzytkonikaZBazy));
					
					MarkerOptions znacznikiUzytkownikowZBazy = new MarkerOptions()
							.position(pozycjaUzytkownikaZBazy)
							.title(Kontakt.uzytkownicyTablica[j])
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
					
					Marker pozycjaUzytkownikow = mapa
							.addMarker(znacznikiUzytkownikowZBazy);
					
					pozycjaUzytkownikow.showInfoWindow();
				}
			}
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
}
