package corona.games.client.view.signin;

import corona.games.client.controller.GUIManager;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.*;

public class WelcomeController {

    @FXML
    TextField usernameField;

    @FXML
    TextField hostField;

    @FXML
    TextField portField;

    @FXML
    Button submitButton;

    @FXML
    public void submit() {
        System.out.println("Got here");
        Stage stage = (Stage) submitButton.getScene().getWindow();
        GUIManager.setUserInfo(usernameField.getText(), hostField.getText(), portField.getText());
        stage.close();
    }

}