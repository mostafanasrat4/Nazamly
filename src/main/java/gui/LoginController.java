package gui;

import classes.Constants;
import classes.User;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class LoginController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    public static User user;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField pwField;
    @FXML
    private Label wrongErrorLabel;
    @FXML
    private Label resetErrorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button createButton;
    @FXML
    private Button homeButton;


    public void switchToHomePage(javafx.event.ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("mainpage.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void switchToRegisterPage(javafx.event.ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("registerPage.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void login() throws ExecutionException, InterruptedException {
        String email = emailField.getText();
        String pw = pwField.getText();
        List<QueryDocumentSnapshot> snapshotList = FirestoreClient.getFirestore().collection(Constants.USERS_COLLECTION)
                .whereEqualTo("email", email).get().get().getDocuments();
        if (!snapshotList.isEmpty()) {
            for (QueryDocumentSnapshot snapshot : snapshotList) {
                if (snapshot.toObject(User.class).getPassword().equals(pw)) {
                    wrongErrorLabel.setOpacity(1);
                    wrongErrorLabel.setTextFill(Color.GREEN);
                    wrongErrorLabel.setText("You logged in successfully!");
                    loginButton.setOpacity(0);
                    loginButton.setDisable(true);
                    resetButton.setOpacity(0);
                    resetButton.setDisable(true);
                    createButton.setOpacity(0);
                    createButton.setDisable(true);
                    homeButton.setStyle("-fx-background-color: #00ff00;");
                    homeButton.setTextFill(Color.WHITE);
                    user = snapshot.toObject(User.class);

                } else {
                    wrongErrorLabel.setOpacity(1);
                    wrongErrorLabel.setTextFill(Color.RED);
                    wrongErrorLabel.setText("WRONG PASSWORD");
                }
            }
        } else {
            wrongErrorLabel.setOpacity(1);
            wrongErrorLabel.setTextFill(Color.RED);
            wrongErrorLabel.setText("Email NOT registered");
        }

    }

    public void resetPw() {
        if (!emailField.getText().trim().isEmpty()) {
            try {
                FirebaseAuth.getInstance().getUserByEmail(emailField.getText());
                resetErrorLabel.setText("Please check your inbox");
                resetErrorLabel.setOpacity(1);
                resetErrorLabel.setTextFill(Color.GREEN);
            } catch (FirebaseAuthException ex) {
                resetErrorLabel.setTextFill(Color.RED);
                resetErrorLabel.setOpacity(1);
                resetErrorLabel.setText("This email is not registered");
            }
        } else {
            resetErrorLabel.setTextFill(Color.RED);
            resetErrorLabel.setOpacity(1);
            resetErrorLabel.setText("Please enter your email");
        }
    }
}
