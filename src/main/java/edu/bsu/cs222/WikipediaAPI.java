package edu.bsu.cs222;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;

class WikipediaAPI {

    private String encodedPage;
    private URL requestURL;
    private URLConnection connection;
    private InputStream responseJSON;

    WikipediaPage requestRevisionsForPage(String search) throws IOException {
        setEncodedPageString(search);
        setRequestURL();
        setupConnection();
        getResponseJSON();
        return getWikipediaPage();
    }

    private void setEncodedPageString(String search) {
        try {
            encodedPage = URLEncoder.encode(search, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            // We don't expect to reach this exception as UTF_8 is supported
            e.printStackTrace();
        }
    }

    private void setRequestURL() {
        try {
            requestURL = new URL("" +
                    "https://en.wikipedia.org/w/api.php?" +
                    "action=query" +
                    "&format=json" +
                    "&prop=redirects%7Crevisions" +
                    "&titles=" + encodedPage +
                    "&redirects=1" +
                    "&rvlimit=24"
            );
        } catch (MalformedURLException e) {
            // We don't expect this to happen as the URL is always formed the same way so we only print the
            // stack trace.
            e.printStackTrace();
        }
    }

    private void setupConnection() {
        try {
            connection = requestURL.openConnection();
        } catch (IOException e) {
            // We do not expect this exception to occur as the request URL is always formed the same way
            e.printStackTrace();
        }
        connection.setRequestProperty("User-Agent", "Revision Tracker/0.1(amthomas12@bsu.edu)");
    }

    private void getResponseJSON() throws IOException {
        responseJSON = connection.getInputStream();
    }

    private WikipediaPage getWikipediaPage() {
        return new WikipediaParser().parse(responseJSON);
    }
}
