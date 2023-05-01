package cs1302.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.control.Separator;
import javafx.scene.control.ProgressBar;
import javafx.geometry.*;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
            .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
            .build();                                     // builds and returns a HttpClient object

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()                          // enable nice output when printing
            .create();

    public final String PokeAPI = "https://pokeapi.co/api/v2/pokemon/";
    public final String PokemonTCG = "https://api.pokemontcg.io/v2/cards?q=name:";

    Stage stage;
    Scene scene;
    VBox root;
    HBox searchLayer;
    TextField searchField;
    Button searchButton;
    Text loadingText;

    HBox pokemonContainer;
    VBox pokemonImages;
    ImageView normalView;
    ImageView shinyView;

    TextFlow pokemonInfo;

    Separator divider;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        searchLayer = new HBox();
        searchLayer.setAlignment(Pos.CENTER);
        pokemonImages = new VBox();
        pokemonContainer = new HBox();
        searchField = new TextField();
        searchButton = new Button("Search");
        loadingText = new Text("Enter a pokemon name above, and click search!");
        divider = new Separator();
        pokemonInfo = new TextFlow();
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setMaxHeight(720);
        stage.setMaxWidth(1280);
        Image image = new Image("file:resources/pokeball.png");
        normalView = new ImageView(image);
        shinyView = new ImageView(image);
        // set pref size for image views
        normalView.setFitWidth(100);
        normalView.setFitHeight(100);
        shinyView.setFitWidth(100);
        shinyView.setFitHeight(100);
        // setup scene
        root.getChildren().addAll(searchLayer, pokemonContainer);
        searchLayer.getChildren().addAll(searchField, searchButton);
        pokemonImages.getChildren().addAll(normalView, divider, shinyView);
        pokemonContainer.getChildren().addAll(pokemonImages, pokemonInfo);
        scene = new Scene(root);
        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    // init method
    @Override
    public void init() {
        searchButton.setOnAction(event -> {
            getPokemonInfo(searchField.getText());
        });
    } // init

    public void getPokemonInfo(String pokemonName) {
        if (pokemonName.equals("")) {
            System.out.println("No pokemon name entered");
            // TODO: Throw exception
            return;
        } // if
        String pokeApiURL = PokeAPI + pokemonName;
        String pokemonTcgURL = PokemonTCG + pokemonName;

        System.out.println(pokeApiURL);
        System.out.println(pokemonTcgURL);


    }

} // ApiApp
