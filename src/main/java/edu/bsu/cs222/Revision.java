package edu.bsu.cs222;

import java.util.Date;

class Revision {

    private String user;
    private Date timestamp;
    private String comment;

    Revision(String user, Date timestamp, String comment) {
        this.user = user;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    String getUsername() {
        return user;
    }

    Date getTimestamp() {
        return timestamp;
    }

    String getComment() {
        return comment;
    }
}
