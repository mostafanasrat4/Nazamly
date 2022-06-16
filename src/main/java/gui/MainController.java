package gui;

import classes.*;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.itextpdf.text.Image;

public class MainController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private GridPane grid;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginLabel1;
    @FXML
    private Label loginLabel2;
    @FXML
    private Label loginLabel3;
    @FXML
    private Label loginLabel4;
    @FXML
    private Label loginLabel5;
    @FXML
    private GridPane mainGrid;
    @FXML
    private Button createTournamentButton;
    @FXML
    private Button manageTournamentButton;
    @FXML
    private Label adminManageLabel;
    @FXML
    private TableView<Team> standingsTable;
    @FXML
    private TableColumn<Team, Integer> posColumn;
    @FXML
    private TableColumn<Team, String> nameColumn;
    @FXML
    private TableColumn<Team, Integer> playedColumn;
    @FXML
    private TableColumn<Team, Integer> gfColumn;
    @FXML
    private TableColumn<Team, Integer> gaColumn;
    @FXML
    private TableColumn<Team, Integer> gdColumn;
    @FXML
    private TableColumn<Team, Integer> pointsColumn;
    @FXML
    private ComboBox<String> tournamentFilterStandings;
    @FXML
    private ComboBox<String> buyTournamentCombo;
    @FXML
    private ComboBox<String> buyMatchComboBox;
    @FXML
    private Label ticketPriceLabel;
    @FXML
    private ComboBox<String> noTicketsCombo;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private ComboBox<String> payMethodCombo;
    @FXML
    private TextField cardNameField;
    @FXML
    private TextField cardNoField;
    @FXML
    private TextField ccvField;
    @FXML
    private ComboBox<String> monthExpCombo;
    @FXML
    private ComboBox<String> yearExpCombo;
    @FXML
    private Button payButton;
    @FXML
    private Label paySuccessLabel;
    @FXML
    private Tab buyTab;
    @FXML
    private DatePicker datePickerAllMatches;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileEmailLabel;
    @FXML
    private Label profileIsAdminLabel;
    @FXML
    private ComboBox<String> profileFavTeamsCombo;
    @FXML
    private ComboBox<String> profileAddFavCombo;
    @FXML
    private ComboBox<String> profileTournamentsCombo;
    @FXML
    private Tab profileTab;
    @FXML
    private ComboBox<String> tournamentFilter;
    @FXML
    private ComboBox<String> teamFilter;
    @FXML
    private ComboBox<String> filterPlayersCombobox;
    @FXML
    private Button resetPlayersButton;
    @FXML
    private GridPane statGrid;
    @FXML
    private Label tableNameLabel;

    private List<QueryDocumentSnapshot> tournaments = new ArrayList<>();
    private List<QueryDocumentSnapshot> teams = new ArrayList<>();
    private List<QueryDocumentSnapshot> matches = new ArrayList<>();
    private final List<Team> favTeams = new ArrayList<>();
    private final List<Team> notFavTeams = new ArrayList<>();
    private Tournament tournament;
    private Match boughtMatch;
// -------------------------------------------General----------------------------------------------//

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tournaments = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).get().get().getDocuments();
            teams = FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).get().get().getDocuments();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //-----------------------------Home Page-----------------------------------//
        if (LoginController.user != null) {
            try {
                loggedIn();
                List<QueryDocumentSnapshot> snapshotList = FirestoreClient.getFirestore().collection(Constants.USERS_COLLECTION)
                        .document(LoginController.user.getId()).collection(Constants.TEAMS_COLLECTION).get().get().getDocuments();

                for (QueryDocumentSnapshot q : snapshotList) {
                    favTeams.add(q.toObject(Team.class));
                    profileFavTeamsCombo.getItems().add(q.toObject(Team.class).getName());
                }

                for (QueryDocumentSnapshot t : teams) {
                    if (!favTeams.contains(t.toObject(Team.class))) {
                        notFavTeams.add(t.toObject(Team.class));
                        profileAddFavCombo.getItems().add(t.toObject(Team.class).getName());
                    }
                }

                gridMatches(getMatchDataTeams(favTeams), mainGrid);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (LoginController.user.isAdmin()) {
                createTournamentButton.setDisable(false);
                manageTournamentButton.setDisable(false);
                adminManageLabel.setText("You have admin privileges. Create / manage your tournaments!");
            } else {
                createTournamentButton.setDisable(true);
                manageTournamentButton.setDisable(true);
                adminManageLabel.setText("Sorry, you must be an admin account to create / manage tournaments");
            }
        } else {
            buyTab.setDisable(true);
            try {
                gridMatches(getMatchDataDate(Date.from(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(),
                        LocalDate.now().getDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        , mainGrid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //------------------------All matches Page--------------------------//
        try {
            gridMatches(getMatchDataDate(Date.from(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(),
                    LocalDate.now().getDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    , grid);
            for (QueryDocumentSnapshot queryDocumentSnapshot : tournaments)
                tournamentFilter.getItems().add(queryDocumentSnapshot.toObject(Tournament.class).getName());
            for (QueryDocumentSnapshot queryDocumentSnapshot : teams)
                teamFilter.getItems().add(queryDocumentSnapshot.toObject(Team.class).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //-------------------Standings Page----------------------------//
        displayAllTournaments(tournamentFilterStandings);


        //----------------------Stats Page--------------------------------//

        try {
            displayAllTournaments(filterPlayersCombobox);
            resetPlayersButton.setDisable(true);
            defaultStatGrid();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //----------------------Buy Tickets Page---------------------------//
        try {
            displayAllTournaments(buyTournamentCombo);
            for (int i = 1; i < 7; i++) noTicketsCombo.getItems().add("" + i);
            payMethodCombo.getItems().addAll("Cash", "Online Card Payment");
            monthExpCombo.getItems().addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
            yearExpCombo.getItems().addAll("21", "22", "23", "24", "25", "26");
        } catch (Exception e) {
            e.printStackTrace();
        }

        payButton.setText("Confirm order and pay");
    }

    public void loggedIn() throws ExecutionException, InterruptedException {
        buyTab.setDisable(false);
        profileTab.setDisable(false);
        profileNameLabel.setText(LoginController.user.getName());
        profileEmailLabel.setText(LoginController.user.getEmail());
        if (LoginController.user.isAdmin()) {
            profileIsAdminLabel.setText("YES");
            List<QueryDocumentSnapshot> queryDocumentSnapshots = FirestoreClient.getFirestore().
                    collection(Constants.TOURNAMENTS_COLLECTION).whereEqualTo(Constants.ADMIN_ID, LoginController.user.getId())
                    .get().get().getDocuments();
            for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                profileTournamentsCombo.getItems().add(s.toObject(Tournament.class).getName());
            }
        }

        loginButton.setText("LogOut");
        loginLabel1.setText("Hello, " + LoginController.user.getName());
        loginLabel2.setText("Your favourite teams' ");
        loginLabel3.setText("matches are displayed");
        loginLabel4.setText("You can add them in");
        loginLabel5.setText("\"My Profile\" tab");
    }

    private List<Match> getMatchDataDate(java.util.Date date) throws ExecutionException, InterruptedException {
        List<Match> allMatches = new ArrayList<>();

        for (QueryDocumentSnapshot tournament : tournaments) {
            List<QueryDocumentSnapshot> snapshotList = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                    .collection(Constants.MATCH_COLLECTION).whereEqualTo("date", date).get().get().getDocuments();
            for (QueryDocumentSnapshot snapshot : snapshotList) {
                allMatches.add(snapshot.toObject(Match.class));
            }
        }
        return allMatches;
    }

    private List<Match> getMatchDataTeams(List<Team> teams) throws ExecutionException, InterruptedException {
        List<Match> allMatches = new ArrayList<>();
        for (Team t : teams) {
            List<QueryDocumentSnapshot> matches = FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION)
                    .document(t.getId()).collection(Constants.MATCH_COLLECTION).orderBy("date").get().get().getDocuments();
            for (QueryDocumentSnapshot match : matches) {
                if (!allMatches.contains(match.toObject(Match.class)))
                    allMatches.add(match.toObject(Match.class));
            }
        }
        return allMatches;
    }

    private void gridMatches(List<Match> matches, GridPane grid) throws ExecutionException, InterruptedException {
        try {
            int row = 0;
            grid.getChildren().clear();
            for (Match match : matches) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getClassLoader().getResource("matchCard.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                MatchController matchController = fxmlLoader.getController();
                matchController.setMatchData(match);
                grid.add(anchorPane, 0, row++);
                grid.setMinWidth(585.0);
                grid.setPrefWidth(585.0);
                grid.setMaxWidth(Region.USE_PREF_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gridPlayers(List<Player> players, List<Tournament> tournaments, GridPane grid) throws ExecutionException, InterruptedException {
        try {
            int row = 0;
            for (int i = 0; i < players.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getClassLoader().getResource("playerCard.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                PlayerController playerController = fxmlLoader.getController();
                Team team = FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(players.get(i).getTeamId()).get().get().toObject(Team.class);
                assert team != null;
                playerController.setPlayerData(players.get(i), tournaments.get(i).getName(), team.getName());
                grid.add(anchorPane, 0, row++);
                grid.setMinWidth(565.0);
                grid.setPrefWidth(565.0);
                grid.setMaxWidth(Region.USE_PREF_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayAllTournaments(ComboBox<String> combo) {
        combo.getItems().clear();
        for (QueryDocumentSnapshot queryDocumentSnapshot : tournaments) {
            combo.getItems().add(queryDocumentSnapshot.toObject(Tournament.class).getName());
        }
    }

// -------------------------------------------Home tab----------------------------------------------//

    public void switchToLoginPage(javafx.event.ActionEvent e) {
        try {
            if (LoginController.user == null) {

                root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("loginPage.fxml")));

            } else {
                LoginController.user = null;
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("mainpage.fxml")));
            }
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

// -------------------------------------------All matches tab----------------------------------------------//


    public void dateFilterAllMatches() throws ExecutionException, InterruptedException {
        Date d = Date.valueOf(datePickerAllMatches.getValue());
//        Service<Void> dateF;
//        dateF = new Service<Void>() {
//            @Override
//            protected Task<Void> createTask() {
//                return new Task<Void>() {
//                    @Override
//                    protected Void call() throws Exception {
        gridMatches(getMatchDataDate(d), grid);
//                        return null;
//                    }
//                };
//            }
//        };
//        dateF.restart();
        tournamentFilter.setDisable(true);
        teamFilter.setDisable(true);
    }

    public void tournamentFilterAllMatches() throws ExecutionException, InterruptedException {
        int index = tournamentFilter.getItems().indexOf(tournamentFilter.getValue());
        tournament = tournaments.get(index).toObject(Tournament.class);
        List<Match> allMatches = new ArrayList<>();
        List<QueryDocumentSnapshot> matches = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION)
                .document(tournament.getId()).collection(Constants.MATCH_COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot match : matches) {
            allMatches.add(match.toObject(Match.class));
        }
        Collections.sort(allMatches);
        gridMatches(allMatches, grid);
        teamFilter.setDisable(true);
        datePickerAllMatches.setDisable(true);
    }

    public void teamFilterAllMatches() throws ExecutionException, InterruptedException {
        int index = teamFilter.getItems().indexOf(teamFilter.getValue());
        Team team = teams.get(index).toObject(Team.class);
        List<Match> allMatches = new ArrayList<>();
        List<QueryDocumentSnapshot> matches = FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION)
                .document(team.getId()).collection(Constants.MATCH_COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot match : matches) {
            allMatches.add(match.toObject(Match.class));
        }
        Collections.sort(allMatches);
        gridMatches(allMatches, grid);
        tournamentFilter.setDisable(true);
        datePickerAllMatches.setDisable(true);

    }

    public void clearFilters() {
        tournamentFilter.setDisable(false);
        //    tournamentFilter.getSelectionModel().clearSelection();
        teamFilter.setDisable(false);
        //   teamFilter.getSelectionModel().clearSelection();
        datePickerAllMatches.setDisable(false);
    }
// -------------------------------------------League Standings tab----------------------------------------------//

    public void tournamentFilterStandingAction() throws ExecutionException, InterruptedException {
        int index = tournamentFilterStandings.getItems().indexOf(tournamentFilterStandings.getValue());
        tournament = tournaments.get(index).toObject(Tournament.class);
        tableNameLabel.setText(tournament.getName() + " League Standings");
        List<QueryDocumentSnapshot> tour = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION)
                .document(tournament.getId()).collection(Constants.TEAMS_COLLECTION)
                .orderBy(Constants.POINTS, Query.Direction.DESCENDING).get().get().getDocuments();
        ObservableList<Team> Teams = FXCollections.observableArrayList();
        int cnt = 1;
        for (QueryDocumentSnapshot queryDocumentSnapshot : tour) {
            Team team = queryDocumentSnapshot.toObject(Team.class);
            team.setPosition(cnt);
            Teams.add(team);
            cnt++;
        }
        leagueTableView(Teams);
    }

    private void leagueTableView(ObservableList<Team> tournament) {
        standingsTable.getItems().clear();
        posColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        playedColumn.setCellValueFactory(new PropertyValueFactory<>("matchesPlayed"));
        gfColumn.setCellValueFactory(new PropertyValueFactory<>("gf"));
        gaColumn.setCellValueFactory(new PropertyValueFactory<>("ga"));
        gdColumn.setCellValueFactory(new PropertyValueFactory<>("gd"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        standingsTable.setItems(tournament);
    }
       // -------------------------------------------Statistics tab----------------------------------------------//
    public void defaultStatGrid() throws ExecutionException, InterruptedException {
        statGrid.getChildren().clear();
        List<Player> players = new ArrayList<>();
        List<Tournament> tours = new ArrayList<>();
        for (QueryDocumentSnapshot tournament : tournaments) {
            List<QueryDocumentSnapshot> snapshots = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION)
                    .document(tournament.toObject(Tournament.class).getId()).collection(Constants.PLAYERS_COLLECTION)
                    .orderBy(Constants.GOALS, Query.Direction.DESCENDING).get().get().getDocuments();
            int goals = -1;
            for (QueryDocumentSnapshot snapshot : snapshots) {
                if (goals == -1)
                    goals = snapshot.toObject(Player.class).getGoals();
                else if (goals > snapshot.toObject(Player.class).getGoals())
                    break;
                players.add(snapshot.toObject(Player.class));
                tours.add(tournament.toObject(Tournament.class));
            }
        }
        gridPlayers(players, tours, statGrid);

    }

    public void filterPlayers() throws ExecutionException, InterruptedException {
        resetPlayersButton.setDisable(false);
        Tournament t = tournaments.get(filterPlayersCombobox.getItems()
                .indexOf(filterPlayersCombobox.getValue())).toObject(Tournament.class);
        List<QueryDocumentSnapshot> snapshotList = FirestoreClient.getFirestore()
                .collection(Constants.TOURNAMENTS_COLLECTION).document(t.getId()).collection(Constants.PLAYERS_COLLECTION)
                .orderBy(Constants.GOALS, Query.Direction.DESCENDING).get().get().getDocuments();
        List<Player> players = new ArrayList<>();
        List<Tournament> tournaments = new ArrayList<>();
        for (QueryDocumentSnapshot p : snapshotList) {
            players.add(p.toObject(Player.class));
            tournaments.add(t);
        }
        statGrid.getChildren().clear();
        gridPlayers(players, tournaments, statGrid);
    }

    // -------------------------------------------Manage Tournament tab----------------------------------------------//
    public void switchToManagePage(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageTournament.fxml"));
            root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void switchToCreatePage(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("createTournament.fxml"));
            root = loader.load();

            CreateTournamentController createTournamentControllerO = loader.getController();
            createTournamentControllerO.reset();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // -------------------------------------------Buy Tickets tab----------------------------------------------//
    public void selectBuyTournamentCombo() throws ExecutionException, InterruptedException {
        buyMatchComboBox.setDisable(false);
        int index = buyTournamentCombo.getItems().indexOf(buyTournamentCombo.getValue());
        tournament = tournaments.get(index).toObject(Tournament.class);
        matches = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.MATCH_COLLECTION).get().get().getDocuments();
        buyMatchComboBox.getItems().clear();
        for (QueryDocumentSnapshot snapshot : matches) {
            buyMatchComboBox.getItems().add(snapshot.toObject(Match.class).getDeclare());
        }
    }

    public void selectBuyMatchCombo() {
        boughtMatch = matches.get(buyMatchComboBox.getSelectionModel().getSelectedIndex()).toObject(Match.class);
        monthExpCombo.setDisable(false);
        yearExpCombo.setDisable(false);
        payMethodCombo.setDisable(false);
        payButton.setDisable(false);
        ticketPriceLabel.setText(boughtMatch.getTicketPrice() + ".00 LE");
    }

    public void selectNoOfTickets() {
        int index = (noTicketsCombo.getValue() != null) ? noTicketsCombo.getItems().indexOf(noTicketsCombo.getValue()) : -1;
        totalPriceLabel.setText((index + 1) * boughtMatch.getTicketPrice() + ".00 LE");
    }

    public void pay() throws IOException {
        int index = buyTournamentCombo.getItems().indexOf(buyTournamentCombo.getValue());
        tournament = tournaments.get(index).toObject(Tournament.class);
        if (payButton.getText().equals("Confirm order and pay")) {
            boughtMatch = matches.get(buyMatchComboBox.getSelectionModel().getSelectedIndex()).toObject(Match.class);
            if ((cardNameField.getText().trim().isEmpty()
                    || cardNoField.getText().length() != 16
                    || monthExpCombo.getValue() == null
                    || yearExpCombo.getValue() == null
                    || ccvField.getText().length() != 3)
                    && !payMethodCombo.getValue().equals("Cash")) {
                paySuccessLabel.setOpacity(1);
                paySuccessLabel.setTextFill(Color.RED);
                paySuccessLabel.setText("All fields are required. Review your input and try again");
            } else {
                if (boughtMatch.getNoOfAvailableTickets() >= noTicketsCombo.getItems().indexOf(noTicketsCombo.getValue())) {
                    paySuccessLabel.setOpacity(1);
                    paySuccessLabel.setTextFill(Color.GREEN);
                    paySuccessLabel.setText("Payment success! Check your email.");
                    boughtMatch.setNoOfSoldTickets(boughtMatch.getNoOfSoldTickets() + noTicketsCombo.getItems().indexOf(noTicketsCombo.getValue()) + 1);
                    boughtMatch.setRevenue(boughtMatch.getRevenue() + boughtMatch.getTicketPrice() * (noTicketsCombo.getItems().indexOf(noTicketsCombo.getValue()) + 1));
                    boughtMatch.setNoOfAvailableTickets(boughtMatch.getNoOfAvailableTickets() - noTicketsCombo.getItems().indexOf(noTicketsCombo.getValue()) - 1);
                    FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId()).collection(Constants.MATCH_COLLECTION).document(boughtMatch.getId()).set(boughtMatch);
                } else {
                    paySuccessLabel.setOpacity(1);
                    paySuccessLabel.setTextFill(Color.RED);
                    paySuccessLabel.setText("Number of desired tickets more than available tickets for this match.");
                }
                payButton.setText("Print Ticket");
            }
        } else {
            Document document = new Document();
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(boughtMatch.getDeclare() + "Tickets.pdf"));
                document.open();
                Image image = Image.getInstance(System.getProperty("user.dir") + "/src/main/resources/logo2.png");
                image.scaleToFit(200, 200);
                image.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
                document.add(image);
                PdfPTable pdfPTable = new PdfPTable(3);
                pdfPTable.setWidthPercentage(100);
                pdfPTable.setSpacingBefore(10);
                pdfPTable.setSpacingAfter(10);
                document.add(new Paragraph(tournament.getName() + ": " + boughtMatch.getDeclare() + " Tickets"));
                document.add(new Paragraph("Payment Method: " + payMethodCombo.getValue()));
                pdfPTable.addCell("#");
                pdfPTable.addCell("Ticket ID");
                pdfPTable.addCell("Ticket Price");
                int index1 = noTicketsCombo.getItems().indexOf(noTicketsCombo.getValue()) + 1;
                for (int i = 0; i < index1; i++) {
                    pdfPTable.addCell(String.valueOf(i + 1));
                    pdfPTable.addCell(UUID.randomUUID().toString());
                    pdfPTable.addCell(String.valueOf(boughtMatch.getTicketPrice()));
                }
                pdfPTable.addCell("Total");
                pdfPTable.addCell(String.valueOf(index1));
                pdfPTable.addCell(String.valueOf(index1 * boughtMatch.getTicketPrice()));
                document.add(pdfPTable);
                document.close();
                writer.close();
                Desktop.getDesktop().open(
                        new File(System.getProperty("user.dir") + "/" + boughtMatch.getDeclare() + "Tickets.pdf"));
            } catch (DocumentException | FileNotFoundException ex) {
                ex.printStackTrace();
            }
            payButton.setDisable(true);
        }
    }

    public void choosePaymentMethod() {
        if (payMethodCombo.getValue().equals("Cash")) {
            payButton.setText("Confirm order");
        }
        if (payMethodCombo.getValue().equals("Cash")) {
            payButton.setText("Confirm order and pay");
        }
    }

// -------------------------------------------My profile tab----------------------------------------------//

    public void addFav() throws ExecutionException, InterruptedException {
        int index = profileAddFavCombo.getItems().indexOf(profileAddFavCombo.getValue());
        Team team = notFavTeams.get(index);
        FirestoreClient.getFirestore().collection(Constants.USERS_COLLECTION).document(LoginController.user.getId())
                .collection(Constants.TEAMS_COLLECTION).document(team.getId()).set(team);
        favTeams.add(team);
        profileFavTeamsCombo.getItems().add(team.getName());
        gridMatches(getMatchDataTeams(favTeams), mainGrid);
    }
}

