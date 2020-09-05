package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class WikipediaAPITest {

    @Test
    void testRequestRevisionsForPageReturnsWikipediaPage() throws IOException {
        WikipediaAPI api = new WikipediaAPI();
        WikipediaPage page = api.requestRevisionsForPage("Ball State");
        Assertions.assertNotNull(page);
    }
}
