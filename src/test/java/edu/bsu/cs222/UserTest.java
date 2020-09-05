package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    private WikipediaPage zappa;

    @BeforeAll
    void setup() {
        WikipediaParser parser = new WikipediaParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("zappa.json");
        zappa = parser.parse(inputStream);
    }

    @Test
    void testGetMostRecentRevisionDate() throws ParseException {
        User user = zappa.getUsersSortedByRevisions().get(0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Assertions.assertEquals(dateFormat.parse("2019-09-13T20:36:10Z"), user.getMostRecentRevisionDate());
    }
}
