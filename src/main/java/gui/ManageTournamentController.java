package gui;

import classes.*;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class ManageTournamentController implements Initializable {

    @FXML
    private ComboBox<String> selectTournamentCombobox;
    @FXML
    private ComboBox<String> selectMatchCombobox;
    @FXML
    private Label team1Label;
    @FXML
    private Label team2Label;
    @FXML
    private TextField team1ScoreField;
    @FXML
    private TextField team2ScoreField;
    @FXML
    private Button updateMatchButton;
    @FXML
    private ComboBox<String> scorerTeam1Combo;

    @FXML
    private TextField scorer1Field;

    @FXML
    private ComboBox<String> scorerTeam2Combo;

    @FXML
    private TextField scorer2Field;

    @FXML
    private Button generateButton;
    @FXML
    private ComboBox<String> salesTourComboBox;

    private List<QueryDocumentSnapshot> tournaments = new ArrayList<>();
    private List<QueryDocumentSnapshot> matches = new ArrayList<>();
    private Match match;
    private Tournament tournament;
    private Tournament salesTournament;
    private Team team1, team2;
    private final List<Player> players1 = new ArrayList<>();
    private final List<Player> players2 = new ArrayList<>();

    public void displayTournaments() throws ExecutionException, InterruptedException {

        tournaments = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION)
                .whereEqualTo(Constants.ADMIN_ID, LoginController.user.getId()).get().get().getDocuments();

        for (QueryDocumentSnapshot queryDocumentSnapshot : tournaments) {
            salesTourComboBox.getItems().add(queryDocumentSnapshot.toObject(Tournament.class).getName());
            selectTournamentCombobox.getItems().add(queryDocumentSnapshot.toObject(Tournament.class).getName());
        }
    }

    public void selectTournament() throws ExecutionException, InterruptedException {
        selectMatchCombobox.setDisable(false);
        //set match combobox with values from database of chosen tournament
        int index = selectTournamentCombobox.getItems().indexOf(selectTournamentCombobox.getValue());
        tournament = tournaments.get(index).toObject(Tournament.class);
        matches = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.MATCH_COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot snapshot : matches) {
            selectMatchCombobox.getItems().add(snapshot.toObject(Match.class).getDeclare());
        }
    }

    public void selectMatch() throws ExecutionException, InterruptedException {
        updateMatchButton.setDisable(false);
        int index = selectMatchCombobox.getItems().indexOf(selectMatchCombobox.getValue());
        match = matches.get(index).toObject(Match.class);
        DocumentSnapshot snapshot = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION)
                .document(tournament.getId()).collection(Constants.MATCH_COLLECTION).document(match.getId())
                .get().get();

        team1 = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.TEAMS_COLLECTION).document(Objects.requireNonNull(snapshot.toObject(Match.class)).getHomeTeamId())
                .get().get().toObject(Team.class);
        assert team1 != null;
        team1Label.setText(team1.getName());
        team1ScoreField.setText(String.valueOf(match.getFirstScore()));
        team2 = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.TEAMS_COLLECTION).document(Objects.requireNonNull(snapshot.toObject(Match.class)).getAwayTeamId())
                .get().get().toObject(Team.class);
        assert team2 != null;
        team2Label.setText(team2.getName());
        team2ScoreField.setText(String.valueOf(match.getSecondScore()));
        List<QueryDocumentSnapshot> snapshotList = FirestoreClient.getFirestore()
                .collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.PLAYERS_COLLECTION).whereEqualTo(Constants.TEAM_ID, team1.getId()).get().get().getDocuments();
        for (QueryDocumentSnapshot snapshot1 : snapshotList) {
            scorerTeam1Combo.getItems().add(snapshot1.toObject(Player.class).getName());
            players1.add(snapshot.toObject(Player.class));
        }
        snapshotList = FirestoreClient.getFirestore()
                .collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.PLAYERS_COLLECTION).whereEqualTo(Constants.TEAM_ID, team2.getId()).get().get().getDocuments();
        for (QueryDocumentSnapshot snapshot1 : snapshotList) {
            scorerTeam2Combo.getItems().add(snapshot1.toObject(Player.class).getName());
            players2.add(snapshot1.toObject(Player.class));
        }

    }

    public void updateMatchResult() {
        int team1Score = Integer.parseInt(team1ScoreField.getText());
        int team2Score = Integer.parseInt(team2ScoreField.getText());

        match.setFirstScore(team1Score);
        match.setSecondScore(team2Score);
        match.setPlayed(true);

        FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.MATCH_COLLECTION).document(match.getId()).set(match);

        FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(team1.getId())
                .collection(Constants.MATCH_COLLECTION).document(match.getId()).set(match);

        FirestoreClient.getFirestore().collection(Constants.TEAMS_COLLECTION).document(team2.getId())
                .collection(Constants.MATCH_COLLECTION).document(match.getId()).set(match);

        team1.setGa(team1.getGa() + team2Score);
        team1.setGf(team1.getGf() + team1Score);
        team1.setGd(team1.getGf() - team1.getGa());
        team2.setGa(team2.getGa() + team1Score);
        team2.setGf(team2.getGf() + team2Score);
        team2.setGd(team2.getGf() - team2.getGa());
        team1.setMatchesPlayed(team1.getMatchesPlayed() + 1);
        team2.setMatchesPlayed(team2.getMatchesPlayed() + 1);

        if (team1Score > team2Score) {
            team1.setPoints(team1.getPoints() + 3);
        } else if (team1Score < team2Score) {
            team2.setPoints(team2.getPoints() + 3);
        } else {
            team1.setPoints(team1.getPoints() + 1);
            team2.setPoints(team2.getPoints() + 1);
        }

        // Update DB
        FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.TEAMS_COLLECTION).document(team1.getId()).set(team1);

        FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                .collection(Constants.TEAMS_COLLECTION).document(team2.getId()).set(team2);

        for (Player player : players1)
            FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                    .collection(Constants.PLAYERS_COLLECTION).document(player.getId()).set(player);

        for (Player player : players2)
            FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION).document(tournament.getId())
                    .collection(Constants.PLAYERS_COLLECTION).document(player.getId()).set(player);

        updateMatchButton.setDisable(true);
    }

    public void addScorer1ComboAction() {
        int index = scorerTeam1Combo.getItems().indexOf(scorerTeam1Combo.getValue());
        Player player = players1.get(index);
        player.setGoals(player.getGoals() + 1);
    }

    public void addScorer1FieldAction() {
        Player player = new Player(scorer1Field.getText());
        player.setGoals(1);
        player.setTeamId(team1.getId());
        scorerTeam1Combo.getItems().add(player.getName());

        players1.add(player);
        scorer1Field.clear();
    }

    public void addScorer2ComboAction() {
        int index = scorerTeam2Combo.getItems().indexOf(scorerTeam2Combo.getValue());
        Player player = players2.get(index);
        player.setGoals(player.getGoals() + 1);
    }

    public void addScorer2FieldAction() {
        Player player = new Player(scorer2Field.getText());
        player.setGoals(1);
        player.setTeamId(team2.getId());
        scorerTeam2Combo.getItems().add(player.getName());
        players2.add(player);
        scorer2Field.clear();
    }

    public void switchToMainPage(javafx.event.ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("mainpage.fxml")));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void getTournament() {
        int index = salesTourComboBox.getItems().indexOf(salesTourComboBox.getValue());
        salesTournament = tournaments.get(index).toObject(Tournament.class);
        generateButton.setDisable(false);
    }

    public void generateReport() throws ExecutionException, InterruptedException, IOException {
        List<Match> matches = new ArrayList<>();
        List<QueryDocumentSnapshot> snapshots = FirestoreClient.getFirestore().collection(Constants.TOURNAMENTS_COLLECTION)
                .document(salesTournament.getId()).collection(Constants.MATCH_COLLECTION).get().get().getDocuments();
        for (QueryDocumentSnapshot snapshot : snapshots) matches.add(snapshot.toObject(Match.class));
        createPDF(matches);
    }

    public void createPDF(List<Match> matches) throws IOException {
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(salesTournament.getName() + " Report.pdf"));
            document.open();
            Image image = Image.getInstance(System.getProperty("user.dir") + "/src/main/resources/logo2.png");
            image.scaleToFit(200, 200);
            image.setAlignment(Image.ALIGN_CENTER);
            document.add(image);
            PdfPTable pdfPTable = new PdfPTable(8);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(10);
            pdfPTable.setSpacingAfter(10);
            document.addTitle(salesTournament.getName() + " Sales Report");
            document.add(new Paragraph(salesTournament.getName() + " Sales Report"));
            document.add(new Paragraph(new SimpleDateFormat("dd/MM/yyyy").format(salesTournament.getStartDate()) + " - " + new SimpleDateFormat("dd/MM/yyyy").format(salesTournament.getEndDate())));
            pdfPTable.addCell("#");
            pdfPTable.addCell("Details");
            pdfPTable.addCell("Date");
            pdfPTable.addCell("Available");
            pdfPTable.addCell("Sold");
            pdfPTable.addCell("Ticket Price");
            pdfPTable.addCell("Total");
            pdfPTable.addCell("Played");
            int n = matches.size();
            int available = 0, sold = 0, revenue = 0;
            for (int i = 0; i < matches.size(); i++) {
                pdfPTable.addCell(String.valueOf(i + 1));
                pdfPTable.addCell(matches.get(i).getDeclare());
                pdfPTable.addCell(new SimpleDateFormat("dd/MM/yyyy").format(matches.get(i).getDate()));
                pdfPTable.addCell(String.valueOf(matches.get(i).getNoOfAvailableTickets()));
                pdfPTable.addCell(String.valueOf(matches.get(i).getNoOfSoldTickets()));
                pdfPTable.addCell(String.valueOf(matches.get(i).getTicketPrice()));
                pdfPTable.addCell(String.valueOf(matches.get(i).getRevenue()));
                pdfPTable.addCell(String.valueOf(matches.get(i).isPlayed()));
                available += matches.get(i).getNoOfAvailableTickets();
                sold += matches.get(i).getNoOfSoldTickets();
                revenue += matches.get(i).getRevenue();
            }
            pdfPTable.addCell("Total");
            pdfPTable.addCell(String.valueOf(n));
            pdfPTable.addCell(" ");
            pdfPTable.addCell(String.valueOf(available));
            pdfPTable.addCell(String.valueOf(sold));
            pdfPTable.addCell(String.valueOf(matches.get(0).getTicketPrice()));
            pdfPTable.addCell(String.valueOf(revenue));
            pdfPTable.addCell(" ");
            document.add(pdfPTable);
            document.close();
            writer.close();
            Desktop.getDesktop().open(
                    new File(System.getProperty("user.dir") + "/" + salesTournament.getName() + " Report.pdf"));
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
        generateButton.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            displayTournaments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
