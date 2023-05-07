module com.game.javathreadgamemultiplayer2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires com.google.gson;


    opens com.game.javathreadgamemultiplayer2 to javafx.fxml, com.google.gson;

    exports com.game.javathreadgamemultiplayer2;
}