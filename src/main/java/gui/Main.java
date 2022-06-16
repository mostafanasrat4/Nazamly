package gui;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) throws IOException {
        FirebaseInit();
        launch(args);
    }

    private static void FirebaseInit() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://nazaml.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("mainpage.fxml")));
        Image logo = new Image("logo2.png");
        primaryStage.getIcons().add(logo);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Nazamly app");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
