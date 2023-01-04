package com.tekmonk.time.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLReader {

	private StringBuilder source = new StringBuilder();
	URLConnection urlConnection = null;
	BufferedReader bufferedReader = null;
	URL url;

	public URLReader(URL url) throws Exception {
		this.url = url;
		urlConnection = url.openConnection();

	}

	public void read() throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	}

	public String readNextLine() throws Exception {
		if (bufferedReader == null) {
			throw new Exception("Buffered Reader is not initilized");
		}
		String line = bufferedReader.readLine();
		return line != null ? line : null;
	}

	public String readAll() throws Exception {
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			source.append(line + "\n");
		}
		bufferedReader.close();

		return source.toString();
	}

	public String getSource() {
		return source.toString();
	}

}
