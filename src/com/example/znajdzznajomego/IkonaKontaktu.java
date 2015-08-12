package com.example.znajdzznajomego;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IkonaKontaktu extends ArrayAdapter<String> {

	private final Context context;
	private final String[] wartosci;
	String[] wartosci2;
	String czasOstatniejAktywnosciString;
	long czasOstatniejAktywnosciLong;
	Date dataZBazy;
	Date teraz;
	long terazLong;
	static final long TRZYDZIESCI_MINUT = 1800000;

	public IkonaKontaktu(Context context, String[] wartosci, String[] wartosci2) {
		super(context, R.layout.activity_pojedynczy_kontakt, wartosci);
		this.context = context;
		this.wartosci = wartosci;
		this.wartosci2 = wartosci2;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("Ikona - ikona View", "Start");
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_pojedynczy_kontakt,
				parent, false);

		TextView textView = (TextView) rowView.findViewById(R.id.Row);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		textView.setText(wartosci[position]);

		SimpleDateFormat formatDaty = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.ENGLISH);

		// liczy róznicê czasu oraz dobieroa odpowiednia ikone, Zielona
		// zalogowany do 30 minut temu, czerwona logowany dawniej niz 30 min
		for (int i = 0; i < Kontakt.iloscPozycjiWBazie; i++) {
			String s = wartosci[position];
			czasOstatniejAktywnosciString = Kontakt.czasLogTablica[i];
			try {
				dataZBazy = formatDaty.parse(czasOstatniejAktywnosciString);
				czasOstatniejAktywnosciLong = dataZBazy.getTime();
				terazLong = System.currentTimeMillis();
			} catch (ParseException e) {
				e.printStackTrace();
				Log.d("Ikona - wyjatek", e.toString());
			}

			// porownuje nazwy uzttkownikow aby przy odpowiendim zmienic ikone
			if ((terazLong - czasOstatniejAktywnosciLong) >= TRZYDZIESCI_MINUT) {
				if (s.startsWith(Kontakt.uzytkownicyTablica[i])) {
					imageView.setImageResource(R.drawable.red_dot);
				}
			}
			else if (s.startsWith(Kontakt.uzytkownicyTablica[i])) {
				imageView.setImageResource(R.drawable.green_dot);
			}
		}
		return rowView;
	}
}
