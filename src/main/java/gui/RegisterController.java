package gui;

import classes.Constants;
import classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private Button createAccButton;
    @FXML
    private CheckBox adminCheckbox;
    @FXML
    private Label emailError;
    @FXML
    private Label pwErrorLabel;
    @FXML
    private PasswordField pwField;
    @FXML
    private PasswordField rpwField;
    @FXML
    private Label successLabel;

    public void createButton() throws FirebaseAuthException {
        boolean admin = adminCheckbox.isSelected();
        if (((pwField.getText()).equals(rpwField.getText()))) {
            if (!(pwField.getText().length() < 5)) {
                if (!nameField.getText().trim().isEmpty()) {
                    try {
                        FirebaseAuth.getInstance().getUserByEmail(emailField.getText());
                        emailError.setOpacity(1);
                        emailError.setTextFill(Color.rgb(255, 0, 0));
                    } catch (FirebaseAuthException ex) {
                        FirebaseAuth.getInstance().createUser(new UserRecord.CreateRequest().setDisplayName(nameField.getText())
                                .setEmail(emailField.getText()).setPassword(pwField.getText()));
                        User user = new User(nameField.getText(), emailField.getText(), admin, pwField.getText());
                        FirestoreClient.getFirestore().collection(Constants.USERS_COLLECTION).document(user.getId())
                                .set(user);
                        createAccButton.setDisable(true);
                        emailError.setOpacity(0);
                        pwErrorLabel.setOpacity(0);
                        successLabel.setOpacity(1);
                    }
                } else {
                    emailError.setOpacity(1);
                    emailError.setText("Please write your name");
                    emailError.setTextFill(Color.RED);
                }
            } else {
                pwErrorLabel.setOpacity(1);
                pwErrorLabel.setText("Password must be 6 characters or more");
                pwErrorLabel.setTextFill(Color.RED);
            }
        } else {
            pwErrorLabel.setOpacity(1);
            pwErrorLabel.setTextFill(Color.RED);
            //if 1
        }
    }

    public void switchToMainPage(javafx.event.ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("mainpage.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLoginPage(javafx.event.ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("loginPage.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
