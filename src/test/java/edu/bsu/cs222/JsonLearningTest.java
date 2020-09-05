package edu.bsu.cs222;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonLearningTest {

    private JsonObject pages;

    @BeforeAll
    void setup() {
        JsonParser parser = new JsonParser();
        InputStream inputSteam = getClass().getClassLoader().getResourceAsStream("zappa.json");
        assert inputSteam != null;
        Reader reader = new InputStreamReader(inputSteam);
        JsonElement rootElement = parser.parse(reader);
        JsonObject rootObject = rootElement.getAsJsonObject();
        pages = rootObject.getAsJsonObject("query").getAsJsonObject("pages");
    }


    @Test
    void testCountRevisions() {
        Assertions.assertNotNull(pages);
        JsonArray array = null;
        for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
            JsonObject entryObject = entry.getValue().getAsJsonObject();
            array = entryObject.getAsJsonArray("revisions");
        }
        assert array != null;
        Assertions.assertEquals(24, array.size());
    }

    @Test
    void testGetFirstAuthor() {
        JsonArray array = null;
        for (Map.Entry<String, JsonElement> entry : pages.entrySet()) {
            JsonObject entryObject = entry.getValue().getAsJsonObject();
            array = entryObject.getAsJsonArray("revisions");
        }
        assert array != null;
        JsonObject firstEntry = (JsonObject) array.get(0);
        String firstAuthor = firstEntry.get("user").getAsString();
        Assertions.assertEquals("DVdm", firstAuthor);
    }
}
