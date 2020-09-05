package edu.bsu.cs222;

import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class UI extends Application implements EventHandler<ActionEvent> {

    private final String SORT_BY_REVISIONS = "Recent Revisions";
    private final String SORT_BY_USERS = "Top Editors";

    private VBox body = new VBox();
    private Button searchButton = new Button();
    private TextField searchField = new TextField();
    private ComboBox<String> searchDropdown = new ComboBox<>();
    private ScrollPane accordionContainer = new ScrollPane();
    private TextArea searchStatus = new TextArea();
    private Accordion revisionsAccordion = new Accordion();

    private WikipediaPage currentPage;
    private boolean connectionFailure = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        configureStage(primaryStage);
        configureSearchDropdown();
        configureSearchField();
        configureSearchStatus();
        configureSearchButton();
        configureScrollPane();
    }

    private void configureStage(Stage stage) {
        stage.setTitle("Orwellian News Service | WikiStats Tool");
        Scene scene = new Scene(createRoot(), 800, 800);
        scene.getStylesheets().add("uiStyles.css");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private Pane createRoot() {
        Pane searchTools = createSearchTools();
        VBox.setVgrow(accordionContainer, Priority.ALWAYS);
        body.getChildren().addAll(
                searchTools,
                searchStatus,
                accordionContainer
        );
        return body;
    }

    private Pane createSearchTools() {
        HBox searchTools = new HBox();
        configureSearchTools(searchTools);
        searchTools.getChildren().addAll(
                searchDropdown,
                searchField,
                searchButton
        );
        return searchTools;
    }

    private void configureSearchTools(HBox searchTools) {
        searchTools.setMinHeight(100);
        searchTools.setAlignment(Pos.CENTER);
    }

    private void configureScrollPane() {
        accordionContainer.setFitToWidth(true);
    }

    private void configureSearchField() {
        searchField.setPrefWidth(400);
        searchField.setPromptText("Find Wiki Page...");
    }

    private void configureSearchStatus() {
        searchStatus.setEditable(false);
        searchStatus.setPrefRowCount(2);
    }

    private void configureSearchButton() {
        searchButton.setText("Search");
        searchButton.disableProperty().bind(disableButton);
        searchButton.setOnAction(this);
    }

    private void configureSearchDropdown() {
        searchDropdown.getItems().addAll(this.SORT_BY_REVISIONS, this.SORT_BY_USERS);
        searchDropdown.getSelectionModel().selectFirst();
    }

    @Override
    public void handle(ActionEvent event) {
        revisionsAccordion.getPanes().removeAll(revisionsAccordion.getPanes());
        outputResultsToResultField();
    }

    private void outputResultsToResultField() {
        String search = searchField.getText();
        try {
            currentPage = new WikipediaAPI().requestRevisionsForPage(search);
            connectionFailure = false;
        } catch (IOException e) {
            connectionFailure = true;
        }
        getResultFieldText();
    }

    private void getResultFieldText() {
        if (connectionFailure) {
            setResultsHeader();
        } else if (searchDropdown.getValue().equals(this.SORT_BY_REVISIONS)) {
            setAccordionRecentRevisions();
        } else if (searchDropdown.getValue().equals(this.SORT_BY_USERS)) {
            setAccordionTopEditors();
        }
    }

    private void setAccordionRecentRevisions() {
        setResultsHeader();
        List<Revision> revisions = currentPage.getRevisions();
        for (Revision revision : revisions) {
            String revisionText = revision.getTimestamp() + "\n" + revision.getComment();
            TextArea resultTextArea = createResultContainer();
            resultTextArea.setText(revisionText);
            TitledPane titledPane = new TitledPane(revision.getUsername(), resultTextArea);
            revisionsAccordion.getPanes().add(titledPane);
        }
        accordionContainer.setContent(revisionsAccordion);
    }

    private TextArea createResultContainer(){
        TextArea resultContainer = new TextArea();
        resultContainer.setEditable(false);
        return resultContainer;
    }

    private void setAccordionTopEditors() {
        setResultsHeader();
        List<User> users = currentPage.getUsersSortedByRevisions();
        for (User user : users) {
            TextArea resultContainer = getRevisionsText(user);
            TitledPane titledPane = new TitledPane(user.getName(), resultContainer);
            revisionsAccordion.getPanes().add(titledPane);
        }
        accordionContainer.setContent(revisionsAccordion);
    }

    private TextArea getRevisionsText(User user){
        int revisions = user.getRevisions().size();
        Date revisionDate = user.getMostRecentRevisionDate();
        StringBuilder revisionCount = new StringBuilder("Revision Count: " + revisions
                + "\nMost Recent: " + revisionDate
                + "\nRecent Revisions:\n");
        for(Revision revision : user.getRevisions()){
            revisionCount.append("\n").append(revision.getComment());
        }
        TextArea resultTextArea = createResultContainer();
        resultTextArea.setText(revisionCount.toString());

        return resultTextArea;
    }

    private void setResultsHeader() {
        if (connectionFailure) {
            searchStatus.setText("Connection has failed. Please check your Wi-Fi and try again.");
        } else if (currentPage.didRedirect()) {
            searchStatus.setText(searchField.getText() + " was redirected to " + currentPage.getTitle());
        } else if (!currentPage.didFindPage()) {
            searchStatus.setText(searchField.getText() + " could not be found. Please check your spelling and try again.");
        } else {
            searchStatus.setText("Results for " + currentPage.getTitle());
        }
    }

    private BooleanBinding disableButton = new BooleanBinding() {
        {
            super.bind(searchField.textProperty());
        }

        @Override
        protected boolean computeValue() {
            return (searchField.getText().isEmpty());
        }
    };

}
