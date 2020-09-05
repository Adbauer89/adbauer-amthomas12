package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WikipediaParserTest {

    private WikipediaPage zappa;
    private WikipediaPage ballState;
    private WikipediaPage noPage;

    @BeforeAll
    void setup() {
        WikipediaParser parser = new WikipediaParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("zappa.json");
        zappa = parser.parse(inputStream);
        inputStream = getClass().getClassLoader().getResourceAsStream("ballStateUniversityWiki.json");
        ballState = parser.parse(inputStream);
        inputStream = getClass().getClassLoader().getResourceAsStream("noPage.json");
        noPage = parser.parse(inputStream);
    }

    @Test
    void testParsePageNotNull() {
        Assertions.assertNotNull(zappa);
        Assertions.assertNotNull(noPage);
    }

    @Test
    void testParsePageDidRedirect() {
        Assertions.assertTrue(zappa.didRedirect());
    }

    @Test
    void testParsePageNoRedirect() {
        Assertions.assertFalse(ballState.didRedirect());
    }

    @Test
    void testNoPageFound() {
        Assertions.assertTrue(ballState.didFindPage());
        Assertions.assertFalse(noPage.didFindPage());
    }

    @Test
    void testCountRevisions() {
        List<Revision> ballStateRevisions = ballState.getRevisions();
        Assertions.assertEquals(1, ballStateRevisions.size());
        List<Revision> zappaRevisions = zappa.getRevisions();
        Assertions.assertEquals(24, zappaRevisions.size());
        List<Revision> noPageRevisions = noPage.getRevisions();
        Assertions.assertEquals(0, noPageRevisions.size());
    }

    @Test
    void testGetRevisionUsers() {
        List<Revision> zappaRevisions = zappa.getRevisions();
        Assertions.assertEquals("DVdm", zappaRevisions.get(0).getUsername());
        Assertions.assertEquals("86.13.118.247", zappaRevisions.get(1).getUsername());
    }

    @Test
    void testGetRevisionTimestamp() throws ParseException {
        List<Revision> zappaRevisions = zappa.getRevisions();
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(timeZone);
        Assertions.assertEquals(dateFormat.parse("2019-09-14T08:14:03Z"), zappaRevisions.get(0).getTimestamp());
        Assertions.assertEquals(dateFormat.parse("2019-09-14T00:36:10Z"), zappaRevisions.get(1).getTimestamp());
    }

    // getRevisions() is called so the IllegalArgumentException is thrown. Results are not needed.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testGetRevisionTimestampThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            WikipediaParser parser = new WikipediaParser();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jsonWithEditedTime.json");
            WikipediaPage editedTime = parser.parse(inputStream);
            editedTime.getRevisions();
        });
    }

    @Test
    void testGetPageTitle() {
        Assertions.assertEquals("Frank Zappa", zappa.getTitle());
    }

    @Test
    void testGetFirstRevisionsComment() {
        List<Revision> revisions = zappa.getRevisions();
        Assertions.assertEquals(
                "Reverted to revision 914586087 by [[Special:Contributions/Autumn Hawk|Autumn Hawk]] " +
                        "([[User talk:Autumn Hawk|talk]]): Not in the cited sources ([[WP:TW|TW]])",
                revisions.get(0).getComment()
        );
    }

    @Test
    void testGetEmptyComment() {
        List<Revision> revisions = zappa.getRevisions();
        Assertions.assertEquals(
                "",
                revisions.get(1).getComment()
        );
    }
}
