package cs1302.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * A Pokédex Application which allows the user to find and save pokemon.
 */
public class ApiApp extends Application {

    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)       // uses HTTP protocol version 2 where possible
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();                                     // builds and returns a HttpClient object

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()                          // enable nice output when printing
            .create();

    public final String PokeAPI = "https://pokeapi.co/api/v2/pokemon/";
    public final String PokemonTCG = "https://api.pokemontcg.io/v2/cards?q=nationalPokedexNumbers:";

    Stage stage;
    Scene scene;
    Stage favoritesStage;
    VBox root;
    VBox searchContainer;
    HBox searchLayer;
    TextField searchField;
    Button searchButton;
    Button showFavorites;
    Text loadingText;
    HBox pokemonContainer;
    VBox pokemonImages;
    ImageView normalView;
    ImageView shinyView;
    VBox cardContainer;
    ImageView cardView;
    HBox cardButtons;
    Button nextCard;
    Button favoriteCard;
    Button prevCard;
    ScrollPane scrollPane;
    Text pokemonInfoText;
    Separator divider;
    ProgressBar loadingBar;

    int cardIndex = 0;
    List<Image> cardImages = new LinkedList<>();
    List<Image> favoriteCardImages = new LinkedList<>();
    List<PokeTcgResponse.Card> cards = new LinkedList<>();
    List<PokeTcgResponse.Card> favoriteCards = new LinkedList<>();
    List<String> favoriteCardIDs = new LinkedList<>();

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox(8);
        searchContainer = new VBox(8);
        searchContainer.setAlignment(Pos.CENTER);
        searchLayer = new HBox(8);
        searchLayer.setAlignment(Pos.CENTER);
        pokemonImages = new VBox(8);
        pokemonContainer = new HBox(8);
        pokemonContainer.setAlignment(Pos.CENTER);
        cardContainer = new VBox(8);
        cardContainer.setAlignment(Pos.CENTER);
        cardButtons = new HBox(8);
        searchField = new TextField("Charizard");
        searchButton = new Button("Search");
        loadingText = new Text("Enter a pokemon name above, and click search!");
        divider = new Separator();
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setMinWidth(200);
        pokemonInfoText = new Text();
        nextCard = new Button("Next");
        prevCard = new Button("Back");
        favoriteCard = new Button("Favorite");
        nextCard.setDisable(true);
        favoriteCard.setDisable(true);
        prevCard.setDisable(true);
        showFavorites = new Button("Show Favorites");
        loadingBar = new ProgressBar();
        loadingBar.setProgress(0);
        favoritesStage = new Stage();

    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setMaxHeight(720);
        stage.setMaxWidth(1280);
        Image pokeballImage = new Image("file:resources/pokeball.png");
        normalView = new ImageView(pokeballImage);
        Image greatballImage = new Image("file:resources/greatball.png");
        shinyView = new ImageView(greatballImage);
        Image cardImage = new Image("file:resources/pokemon_card_back.png");
        cardView = new ImageView(cardImage);
        // set pref size for image views
        normalView.setFitWidth(125);
        normalView.setFitHeight(125);
        shinyView.setFitWidth(125);
        shinyView.setFitHeight(125);
        cardView.setFitWidth(192.5);
        cardView.setFitHeight(250);
        // setup scene
        root.getChildren().addAll(searchContainer, pokemonContainer);
        searchContainer.getChildren().addAll(searchLayer, loadingText);
        searchLayer.getChildren().addAll(searchField, searchButton, showFavorites);
        pokemonImages.getChildren().addAll(normalView, divider, shinyView, loadingBar);
        pokemonContainer.getChildren().addAll(pokemonImages, scrollPane,
                cardContainer);
        cardContainer.getChildren().addAll(cardView, cardButtons);
        cardButtons.getChildren().addAll(prevCard, favoriteCard, nextCard);
        scrollPane.setContent(pokemonInfoText);
        scene = new Scene(root);
        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.setWidth(600);
        stage.setResizable(false);
        stage.show();

    } // start

    // init method
    @Override
    public void init() {
        searchButton.setOnAction(event -> runInNewThread(() -> getPokemonInfo(searchField.getText())));
        nextCard.setOnAction(event -> {
            if (cardIndex < cards.size() - 1) {
                cardIndex++;
            } else {
                cardIndex = 0;
            } // if
            checkFavorite();
            cardView.setImage(cardImages.get(cardIndex));
            if (favoriteCards.contains(cards.get(cardIndex))) {
                favoriteCard.setText("Unfavorite");
            } else {
                favoriteCard.setText("Favorite");
            } // if
        });
        prevCard.setOnAction(event -> {
            if (cardIndex > 0) {
                cardIndex--;
            } else {
                cardIndex = cardImages.size() - 1;
            } // if
            checkFavorite();
            cardView.setImage(cardImages.get(cardIndex));
            if (favoriteCards.contains(cards.get(cardIndex))) {
                favoriteCard.setText("Unfavorite");
            } else {
                favoriteCard.setText("Favorite");
            } // if
        });
        favoriteCard.setOnAction(event -> {
            toggleFavorite();
            if (favoritesStage.isShowing()) {
                favoritesStage.close();
                showFavorites.fire();
            } // if
        });

        showFavorites.setOnAction(event -> showFavorites());
    } // init

    public void getPokemonInfo(String pokemonName) {

        try {
            Platform.runLater(() -> loadingText.setText("Loading..."));
            Platform.runLater(() -> loadingBar.setProgress(0));
            cardImages.clear();
            cards.clear();
            nextCard.setDisable(true);
            Platform.runLater(() -> favoriteCard.setText("Favorite"));
            favoriteCard.setDisable(true);
            prevCard.setDisable(true);
            cardIndex = 0;
            if (pokemonName.equals("")) {
                System.out.println("No pokemon name entered");
                // TODO: Throw exception
                return;
            } // if
            String pokemonTerm = URLEncoder.encode(pokemonName.toLowerCase(), StandardCharsets.UTF_8);
            String pokeApiURL = PokeAPI + pokemonTerm;
            System.out.println(pokeApiURL);

            HttpRequest pokeApiRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(pokeApiURL))
                    .GET()
                    .build();
            HttpResponse<String> pokeApiResponse = HTTP_CLIENT.send(pokeApiRequest, HttpResponse.BodyHandlers.ofString());


            if (pokeApiResponse.statusCode() != 200){
                throw new IOException(pokeApiResponse.toString());
            }

            String pokeApiBody = pokeApiResponse.body();

            System.out.println("*********PRETTY PRINTED POKE API BODY**********");
            System.out.println(GSON.toJson(pokeApiBody));

            PokeApiResponse pokeApiReponse = GSON.fromJson(pokeApiBody, PokeApiResponse.class);

            String pokemonTcgURL = PokemonTCG + pokeApiReponse.id + "&pageSize=20";

            System.out.println(pokemonTcgURL);
            HttpRequest pokemonTcgRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(pokemonTcgURL))
                    .GET()
                    .build();
            HttpResponse<String> pokemonTcgResponse = HTTP_CLIENT.send(pokemonTcgRequest, HttpResponse.BodyHandlers.ofString());
            String pokemonTcgBody = pokemonTcgResponse.body();

            System.out.println("*********PRETTY PRINTED POKEMON TCG BODY**********");
            System.out.println(GSON.toJson(pokemonTcgBody));

            if (pokemonTcgResponse.statusCode() != 200){
                throw new IOException(pokemonTcgResponse.toString());
            }
            PokeTcgResponse pokeTcgResponse = GSON.fromJson(pokemonTcgBody, PokeTcgResponse.class);

            cards = new LinkedList<>();
            cardImages = new LinkedList<>();

            // get pokemon images
            Image normalImage = new Image(pokeApiReponse.sprites.other.official_artwork.frontDefault);
            Platform.runLater(() -> loadingBar.setProgress((double) 1 /(pokeTcgResponse.data.size() + 2)));
            Image shinyImage = new Image(pokeApiReponse.sprites.other.official_artwork.frontShiny);
            Platform.runLater(() -> loadingBar.setProgress((double) 2 /(pokeTcgResponse.data.size() + 2)));
            for (int i = 0; i < pokeTcgResponse.data.size(); i++) {
                Image image = new Image(pokeTcgResponse.data.get(i).images.small);
                if (image.isError()) {
                    System.out.println("Error loading image");
                    continue;
                }
                cards.add(pokeTcgResponse.data.get(i));
                cardImages.add(new Image(pokeTcgResponse.data.get(i).images.small));
                final int progress = i + 2;
                Platform.runLater(() -> loadingBar.setProgress((double) progress / (pokeTcgResponse.data.size() + 2)));
            } // for
            setCardImage(cardIndex);
            normalView.setImage(normalImage);
            shinyView.setImage(shinyImage);
            Platform.runLater(() -> loadingText.setText("Found " + pokemonName.toLowerCase() + "!"));
            Platform.runLater(() -> loadingBar.setProgress(1));

            // add information about the Pokémon in the text area
            StringBuilder pokemonInfo = new StringBuilder();
            pokemonInfo.append("Name: ").append(pokeApiReponse.name.toUpperCase()).append("\n\n");
            pokemonInfo.append("ID: ").append(pokeApiReponse.id).append("\n\n");
            pokemonInfo.append("Height: ").append(pokeApiReponse.height).append("\n\n");
            pokemonInfo.append("Weight: ").append(pokeApiReponse.weight).append("\n\n");
            pokemonInfo.append("Base Experience: ").append(pokeApiReponse.baseExperience).append("\n\n");
            pokemonInfo.append("Abilities: ").append("\n");
            for (int i = 0; i < pokeApiReponse.abilities.length; i++) {
                pokemonInfo.append("\t").append(pokeApiReponse.abilities[i].ability.name).append("\n");
            } // for
            pokemonInfo.append("\n").append("Types: ").append("\n");
            for (int i = 0; i < pokeApiReponse.types.length; i++) {
                pokemonInfo.append("\t").append(pokeApiReponse.types[i].type.name).append("\n");
            } // for

            pokemonInfo.append("\n").append("Base Stats: ").append("\n");
            for (int i = 0; i < pokeApiReponse.stats.length; i++) {
                pokemonInfo.append("\t").append(pokeApiReponse.stats[i].stat.name).append(": ").append(pokeApiReponse.stats[i].baseStat).append("\n");
            } // for

            pokemonInfo.append("\n").append("Moves: ").append("\n");
            for (int i = 0; i < pokeApiReponse.moves.length; i++) {
                pokemonInfo.append("\t").append(pokeApiReponse.moves[i].move.name).append("\n");
            } // for
            Platform.runLater(() -> pokemonInfoText.setText(pokemonInfo.toString()));

            System.out.println(favoriteCardIDs);
            System.out.println(cards.get(cardIndex).id);
            checkFavorite();


            nextCard.setDisable(false);
            favoriteCard.setDisable(false);
            prevCard.setDisable(false);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error sending request");
            e.printStackTrace();
        } // try/catch
    } // getPokemonInfo

    public void toggleFavorite() {
        if (favoriteCard.getText().equals("Favorite")) {
            favoriteCard.setText("Unfavorite");
            favoriteCards.add(cards.get(cardIndex));
            favoriteCardIDs.add(cards.get(cardIndex).id);
            favoriteCardImages.add(cardImages.get(cardIndex));
        } else {
            int index = favoriteCardIDs.indexOf(cards.get(cardIndex).id);
            favoriteCard.setText("Favorite");
            favoriteCards.remove(index);
            favoriteCardIDs.remove(index);
            favoriteCardImages.remove(index);
        } // if
    } // toggleFavorite


    /**
     * Checks if the current card is a favorite
     * @param index
     */
    public void setCardImage(int index) {
        Image cardImage = cardImages.get(index % cardImages.size());
        cardView.setImage(cardImage);
    } // setCardImage

    /**
     * Shows all favourite cards.
     */
    public void showFavorites() {
        if (favoritesStage != null && favoritesStage.isShowing()) {
            favoritesStage.close();
        } // if
        favoritesStage = new Stage();
        VBox favoritesRoot = new VBox();
        favoritesStage.setTitle("Favorites");
        Scene favoritesScene = new Scene(favoritesRoot);
        favoritesStage.setScene(favoritesScene);
        favoritesStage.setWidth(800);
        favoritesStage.setHeight(600);
        favoritesStage.setMaxHeight(600);
        favoritesStage.setResizable(false);
        favoritesStage.sizeToScene();
        if (favoriteCards.size() == 0) {
            Label noFavorites = new Label("No favorites");
            noFavorites.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
            favoritesRoot.getChildren().add(noFavorites);
            favoritesStage.show();
            return;
        } // if
        favoritesRoot.setAlignment(Pos.CENTER);
        ScrollPane favoritesScrollPane = new ScrollPane();
        favoritesScrollPane.setFitToWidth(true);
        favoritesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        GridPane favoritesGrid = new GridPane();
        favoritesGrid.setAlignment(Pos.CENTER);
        favoritesGrid.setHgap(10);
        favoritesGrid.setVgap(10);
        favoritesScrollPane.setContent(favoritesGrid);
        favoritesRoot.getChildren().add(favoritesScrollPane);

        int cardsPerRow = 3;
        for (int i = 0; i < favoriteCards.size(); i++) {
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;
            ImageView favoriteView = new ImageView(favoriteCardImages.get(i));
            favoriteView.setFitWidth(192.5);
            favoriteView.setFitHeight(250);
            Button removeButton = new Button("Remove");
            final int finalI = i;
            removeButton.setOnAction(e -> {
                favoriteCards.remove(finalI);
                favoriteCardIDs.remove(finalI);
                favoriteCardImages.remove(finalI);
                favoritesGrid.getChildren().removeAll(removeButton, favoriteView);
                checkFavorite();
                showFavorites();
                if (favoriteCards.size() == 0) {
                    favoritesStage.close();
                } // if

            });
            StackPane cardPane = new StackPane(favoriteView, removeButton);
            favoritesGrid.add(cardPane, col, row);
        } // for
        favoritesStage.show();
    }

    /**
     * Checks if the current card is in the list of favorite cards and updates the favorite button accordingly
     */
    public void checkFavorite() {
        // check if the current card's id is in the list of favorite cards
        if (favoriteCardIDs.contains(cards.get(cardIndex).id)) {
            cards.set(cardIndex, favoriteCards.get(favoriteCardIDs.indexOf(cards.get(cardIndex).id)));
            Platform.runLater(() -> favoriteCard.setText("Unfavorite"));
        } else {
            Platform.runLater(() -> favoriteCard.setText("Favorite"));
        } // if
    }

    /**
     * Runs a runnable in a new thread
     * @param runnable the runnable to run
     */
    public void runInNewThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    } // runInNewThread



} // ApiApp
