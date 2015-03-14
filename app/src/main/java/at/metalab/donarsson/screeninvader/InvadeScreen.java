/*
 * ScreenInvader Android App
 * Copyright (C) 2014  Benjamin Schwarz <pwnicorndev@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.metalab.donarsson.screeninvader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InvadeScreen extends Activity {

	String invader = "10.20.30.40"; //TODO: Don't hard-code ip, automatically select based on connected WiFi

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ConnectivityManager connectivityManager = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo.isConnected()) {
			//TODO: Check if we know a ScreenInvader on this network
			Intent intent = getIntent();
			String type = intent.getType();
			if (type.startsWith("text/")) {
				String text = intent.getStringExtra(Intent.EXTRA_TEXT);
				Pattern pattern = Patterns.WEB_URL;
				Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					String url = matcher.group();
					new PostUrlTask().execute(url);
				}
			} //TODO: Add support for other types (file upload)
		} else {
			//TODO: Display a prompt to connect to a WiFi
			Toast.makeText(getApplicationContext(), getString(R.string.no_wifi_toast), Toast.LENGTH_LONG).show();
		}
		finish();
	}


	private class PostUrlTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {
			try {
				URL requestUrl = new URL("http://" + invader + "/cgi-bin/show?" + url[0]);
				HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();
				urlConnection.getInputStream(); // We don't need the response
				urlConnection.disconnect();
				return url[0]+getString(R.string.posturl_success);
			} catch (IOException e) {
				//TODO: Prompt user to report bug on GitHub
				e.printStackTrace();
				return getString(R.string.io_exception);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
		}
	}
}
