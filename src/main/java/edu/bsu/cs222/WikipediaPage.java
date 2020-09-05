package edu.bsu.cs222;

import java.util.ArrayList;
import java.util.List;

class WikipediaPage {

    private boolean didRedirect;
    private boolean didFindPage;
    private List<Revision> revisions;
    private String title;

    WikipediaPage(PageData pageData, List<Revision> revisions) {
        this.didFindPage = pageData.didFindPage();
        this.didRedirect = pageData.didRedirect();
        this.revisions = revisions;
        this.title = pageData.getTitle();
    }

    boolean didRedirect() {
        return didRedirect;
    }

    List<Revision> getRevisions() {
        return revisions;
    }

    boolean didFindPage() {
        return didFindPage;
    }

    String getTitle(){
        return title;
    }

    List<User> getUsersSortedByRevisions() {
        List<String> usernames = this.getAllUniqueReviserUsernames();
        List<User> sortedUsers = new ArrayList<>();
        for (String username : usernames) {
            User newUser = this.createUser(username);
            this.sortUserIntoSortedArray(newUser, sortedUsers);
        }
        return sortedUsers;
    }

    private List<String> getAllUniqueReviserUsernames() {
        List<String> usernames = new ArrayList<>();
        for (Revision revision : revisions) {
            String username = revision.getUsername();
            if (usernames.indexOf(username) == -1) {
                usernames.add(username);
            }
        }
        return usernames;
    }

    private User createUser(String username) {
        User user = new User(username);
        for (Revision revision : revisions) {
            if(user.getName().equals(revision.getUsername())){
                user.addRevision(revision);
            }
        }
        return user;
    }

    private void sortUserIntoSortedArray(User user, List<User> sortedUsers) {
        int insertionIndex = this.getInsertionPointForNewUser(user, sortedUsers);
        sortedUsers.add(insertionIndex, user);
    }

    private int getInsertionPointForNewUser(User newUser, List<User> sortedUsers) {
        int insertionPoint = -1;
        for (User sortedUser : sortedUsers) {
            if (this.newUserBelongsBeforeSortedUser(newUser, sortedUser)) {
                insertionPoint = sortedUsers.indexOf(sortedUser);
                break;
            }
        }
        if (insertionPoint == -1) {
            insertionPoint = sortedUsers.size();
        }
        return insertionPoint;
    }

    private boolean newUserBelongsBeforeSortedUser(User newUser, User sortedUser) {
        if (this.newUserHasMoreRevisionsThanSortedUser(newUser, sortedUser)) {
            return true;
        } else {
            return (this.newUserEditedMoreRecentlyThanSortedUser(newUser, sortedUser)
                    && this.newUserHasSameRevisionsAsSortedUser(newUser, sortedUser));
        }
    }

    private boolean newUserHasMoreRevisionsThanSortedUser(User newUser, User sortedUser) {
        int newUserRevisionsCount = newUser.getRevisions().size();
        int sortedUserRevisionsCount = sortedUser.getRevisions().size();
        return newUserRevisionsCount > sortedUserRevisionsCount;
    }

    private boolean newUserEditedMoreRecentlyThanSortedUser(User newUser, User sortedUser) {
        return newUser.getMostRecentRevisionDate().after(sortedUser.getMostRecentRevisionDate());
    }

    private boolean newUserHasSameRevisionsAsSortedUser(User newUser, User sortedUser) {
        int newUserRevisionsCount = newUser.getRevisions().size();
        int sortedUserRevisionsCount = sortedUser.getRevisions().size();
        return newUserRevisionsCount == sortedUserRevisionsCount;
    }
}
