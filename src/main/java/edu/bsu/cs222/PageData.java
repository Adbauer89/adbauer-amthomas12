package edu.bsu.cs222;

class PageData {
    private boolean didFindPage;
    private boolean didRedirect;
    private String title;

    PageData(boolean didFindPage, boolean didRedirect, String title) {
        this.didFindPage = didFindPage;
        this.didRedirect = didRedirect;
        this.title = title;
    }

    boolean didFindPage() {
        return didFindPage;
    }

    boolean didRedirect() {
        return didRedirect;
    }

    String getTitle() {
        return title;
    }
}
