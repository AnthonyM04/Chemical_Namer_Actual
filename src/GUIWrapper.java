import WebRequest.RequestType;
import WebRequest.URL_Requester;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;



import java.util.ArrayList;

public class GUIWrapper extends Application {
    public void start(Stage pStage) throws Exception {
        GridPane gPane = new GridPane();
        Text txt1 = new Text("ChemicalNamer v0.0.1");
        txt1.setId("title");


        gPane.setAlignment(Pos.CENTER);


        // Input textfield
        TextField inputChemical = new TextField();
        inputChemical.getStyleClass().addAll("inputBox", "inputBox:focused");
        inputChemical.setMinHeight(35);

        // Submit button
        Button submit = new Button("Analyze");

        // Output Label
        Text output = new Text("");

        CheckBox getWeb = new CheckBox("Use web lookup instead of offline calculator");

        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String input = inputChemical.getText();
                try {
                    ArrayList<Element> elementList = Main.readElements();
                    Tokenizer tokenizer = new Tokenizer(input, elementList);
                    Token first = tokenizer.nextToken();

                    if(getWeb.isSelected()) {
                        boolean went = false;
                        while (!went) {
                            try {
                                URL_Requester url_requester = new URL_Requester(RequestType.FORMULA, inputChemical.getText());

                                String line9 = url_requester.readURLs().split("\n")[8];
                                line9 = line9.substring(22);

                                output.setText((line9.substring(0, line9.length() - 1)));

                                went = true;
                            } catch (NullPointerException e) {e.printStackTrace();}
                        }

                    }
                    else {
                        if (first instanceof NumberToken) {
                            throw new InvalidExpressionException();
                        }
                        ElementToken firstElement = (ElementToken) first;
                        output.setText(Main.molecularCompound(tokenizer, firstElement));
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    output.setText("Illegal or unknown chemical");
                }
            }
        });



        // All the .add() function calls
        gPane.add(txt1, 0, 0);
        gPane.add(inputChemical, 0, 1);
        gPane.add(submit, 1,1);
        gPane.add(output, 0, 2);
        gPane.add(getWeb, 1, 2);

        // Padding
        gPane.setPadding(new Insets(10));

        // Vertgap + Horigap
        gPane.setVgap(10);
        gPane.setHgap(10);

        gPane.getStyleClass().add("background");


        Scene scene = new Scene(gPane, 800, 400);
        scene.getStylesheets().add("gui.css");
        pStage.setScene(scene);
        pStage.setTitle("ChemicalNamer");
        pStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
