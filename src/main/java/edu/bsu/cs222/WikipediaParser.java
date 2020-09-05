package edu.bsu.cs222;

import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class WikipediaParser {

    private JsonObject rootObject;

    WikipediaPage parse(InputStream input) {
        this.setRootObject(input);
        PageData pageData = this.getPageData();
        List<Revision> revisions = this.getRevisions();
        return new WikipediaPage(pageData, revisions);
    }

    private void setRootObject(InputStream input) {
        JsonParser parser = new JsonParser();
        Reader reader = new InputStreamReader(input);
        JsonElement rootElement = parser.parse(reader);
        rootObject = rootElement.getAsJsonObject();
    }

    private PageData getPageData() {
        boolean didFindPage = this.didFindPage();
        boolean didRedirect = this.didRedirect();
        return new PageData(didFindPage, didRedirect, getTitle());
    }

    private boolean didRedirect() {
        JsonArray redirects = rootObject.getAsJsonObject("query").getAsJsonArray("redirects");
        return redirects != null;
    }

    private boolean didFindPage() {
        JsonObject pageNotFound = rootObject.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject("-1");
        return pageNotFound == null;
    }

    private String getTitle(){
        String title = "";
        if (this.didFindPage()){
            JsonObject pages = rootObject.getAsJsonObject("query").getAsJsonObject("pages");
            for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
                JsonObject entryObject = entry.getValue().getAsJsonObject();
                title = entryObject.get("title").getAsString();
            }
        }
        return title;
    }

    private List<Revision> getRevisions() {
        List<Revision> revisions = new ArrayList<>();
        if (this.didFindPage()) {
            JsonArray revisionJsonArray = this.getRevisionsJson();
            revisions = this.parseRevisionsJson(revisionJsonArray);
        }
        return revisions;
    }

    private JsonArray getRevisionsJson() {
        JsonArray revisionsJson = new JsonArray();
        JsonObject pages = rootObject.getAsJsonObject("query").getAsJsonObject("pages");
        for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
            JsonObject entryObject = entry.getValue().getAsJsonObject();
            revisionsJson = entryObject.getAsJsonArray("revisions");
        }
        return revisionsJson;
    }

    private List<Revision> parseRevisionsJson(JsonArray revisionJsonArray) {
        List<Revision> revisions = new ArrayList<>();
        for (JsonElement revisionJson : revisionJsonArray) {
            Revision revision = this.createRevision(revisionJson);
            revisions.add(revision);
        }
        return revisions;
    }

    private Revision createRevision(JsonElement revisionJson) {
        JsonObject revision = revisionJson.getAsJsonObject();
        String user = revision.get("user").getAsString();
        Date timestamp = this.getTimestamp(revision);
        String comment = this.getComment(revision);
        return new Revision(user, timestamp, comment);
    }

    private Date getTimestamp(JsonObject revision) {
        String time = revision.get("timestamp").getAsString();
        String wikipediaTimezoneReturned = "UTC";
        TimeZone timeZone = TimeZone.getTimeZone(wikipediaTimezoneReturned);
        String wikipediaDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        DateFormat dateFormat = new SimpleDateFormat(wikipediaDateFormat);
        dateFormat.setTimeZone(timeZone);
        Date timestamp;
        try {
            timestamp = dateFormat.parse(time);
        } catch (ParseException e) {
            // We don't expect this to happen as this data is being received from wikipedia in the same format and
            // is not influenced by the user. We choose to throw an unchecked exception in this case.
            throw new IllegalArgumentException("Incorrect date format");
        }
        return timestamp;
    }

    private String getComment(JsonObject revision) {
        return revision.get("comment").getAsString();
    }

}
