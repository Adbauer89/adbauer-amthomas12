package edu.bsu.cs222;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class User {
    private String name;
    private List<Revision> revisions;

    User(String name) {
        this.name = name;
        revisions = new ArrayList<>();
    }

    String getName() {
        return name;
    }

    void addRevision(Revision revision) {
        this.revisions.add(revision);
    }

    Date getMostRecentRevisionDate() {
        Date mostRecentRevision = new Date(0);
        for (Revision revision : this.revisions) {
            if (revision.getTimestamp().after(mostRecentRevision)) {
                mostRecentRevision = revision.getTimestamp();
            }
        }
        return mostRecentRevision;
    }

    List<Revision> getRevisions() {
        return revisions;
    }
}
