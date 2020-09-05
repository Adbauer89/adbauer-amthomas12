package edu.bsu.cs222;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class WikipediaPageTest {

    @Test
    void testGetUsersSortedByRevisionsCount() {
        WikipediaParser parser = new WikipediaParser();
        InputStream inputSteam = getClass().getClassLoader().getResourceAsStream("zappa.json");
        WikipediaPage zappa = parser.parse(inputSteam);
        List<User> users = zappa.getUsersSortedByRevisions();
        String userWithMostRevisions = users.get(0).getName();
        String userWithSecondMostRevisions = users.get(1).getName();
        Assertions.assertEquals("86.13.118.247", userWithMostRevisions);
        Assertions.assertEquals("50.110.39.138", userWithSecondMostRevisions);
    }

    @Test
    void testUsersGetSortedByTimestamp() {
        WikipediaParser parser = new WikipediaParser();
        InputStream inputSteam = getClass().getClassLoader().getResourceAsStream("zappa.json");
        WikipediaPage zappa = parser.parse(inputSteam);
        List<User> givenUsersList = zappa.getUsersSortedByRevisions();
        List<String> expectedUsernamesList = new ArrayList<>(
                Arrays.asList(
                        "86.13.118.247",
                        "50.110.39.138",
                        "Anonymous from the 21st century",
                        "Dan56",
                        "InternetArchiveBot",
                        "DVdm",
                        "Autumn Hawk",
                        "2604:CA00:150:22A:62C4:274A:9A8B:CCFC",
                        "JJMC89 bot III",
                        "RHodnett",
                        "Mitch Ames",
                        "2600:100A:B023:B234:39A0:A503:DB97:BFB"
                )
        );
        for (User givenUser : givenUsersList){
            int index = givenUsersList.indexOf(givenUser);
            Assertions.assertEquals(expectedUsernamesList.get(index), givenUser.getName());
        }
    }

}
