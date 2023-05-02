package cs1302.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;


/**
 * A Pokédex Application which allows the user to find and save Pokémon.
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
    public final String DexEntryAPI = "https://pokeapi.co/api/v2/pokemon-species/";
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
    HBox buttonContainer;
    Button saveButton;
    Button loadButton;
    TextInputDialog saveDialog;
    TextInputDialog loadDialog;

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
        cardButtons.setAlignment(Pos.CENTER);
        buttonContainer = new HBox(8);
        buttonContainer.setAlignment(Pos.CENTER);
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
        loadingBar.setStyle("-fx-accent: green; -fx-background-color: gray;");
        loadingBar.setProgress(0);
        saveButton = new Button("Save");
        loadButton = new Button("Load");
        saveDialog = new TextInputDialog();
        saveDialog.setTitle("Save");
        saveDialog.setHeaderText("Save your favorite cards!");
        saveDialog.setContentText("Please enter a unique key you would like to set:");
        loadDialog = new TextInputDialog();
        loadDialog.setTitle("Load");
        loadDialog.setHeaderText("Load your favorite cards!");
        loadDialog.setContentText("Please enter the key you would like to load:");
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
        pokemonImages.getChildren().addAll(normalView, divider, shinyView, loadingBar, buttonContainer);
        buttonContainer.getChildren().addAll(saveButton, loadButton);
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
            runInNewThread(this::checkFavorite);
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
            runInNewThread(this::checkFavorite);
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
        saveButton.setOnAction(event -> {
            if (favoriteCards.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No favorite cards to load!");
                alert.setContentText("Please favorite some cards first!");
                alert.showAndWait();
                return;
            } // if
            // show dialog
            saveDialog.showAndWait().ifPresent(key ->
            runInNewThread(() ->{
                try {
                    saveFavorites( favoriteCards, key);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));

        });
        loadButton.setOnAction(event -> {

            loadDialog.showAndWait().ifPresent(key -> {
                if (favoritesStage.isShowing()) {
                    favoritesStage.close();
                } // if


                runInNewThread(() ->{
                    try {
                        loadFavorites(key);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
    });




    } // init

    public void getPokemonInfo(String pokemonName) {


        try {
            Platform.runLater(() -> loadingText.setText("Loading..."));
            Platform.runLater(() -> loadingBar.setProgress(0));
            saveButton.setDisable(true);
            loadButton.setDisable(true);
            nextCard.setDisable(true);

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
            String dexEntryURL = DexEntryAPI + pokemonTerm;
            System.out.println(dexEntryURL);
            HttpRequest dexEntryRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(dexEntryURL))
                    .GET()
                    .build();
            HttpResponse<String> dexEntryResponse = HTTP_CLIENT.send(dexEntryRequest, HttpResponse.BodyHandlers.ofString());
            String dexEntryBody = dexEntryResponse.body();

            if (dexEntryResponse.statusCode() != 200){
                throw new IOException(dexEntryResponse.toString());
            }
            DexResponse dexResponse = GSON.fromJson(dexEntryBody, DexResponse.class);



            String pokeApiURL = PokeAPI + dexResponse.id;
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

            cardImages.clear();
            cards.clear();

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

            String dexEntry = "No dex entry found";
            for (int i = dexResponse.flavorTextEntries.size() - 1; i > 0; i--) {
                if (dexResponse.flavorTextEntries.get(i).language.name.equals("en")) {
                    dexEntry = dexResponse.flavorTextEntries.get(i).flavorText;
                    break;
                } // if
            } // for

            // add information about the Pokémon in the text area
            StringBuilder pokemonInfo = new StringBuilder();
            pokemonInfo.append("Name: ").append(pokeApiReponse.name.toUpperCase()).append("\n\n");
            pokemonInfo.append("ID: ").append(pokeApiReponse.id).append("\n\n");
            pokemonInfo.append("Height: ").append(pokeApiReponse.height).append("\n\n");
            pokemonInfo.append("Weight: ").append(pokeApiReponse.weight).append("\n\n");
            pokemonInfo.append("Pokédex Entry: ").append(dexEntry).append("\n\n");
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
            runInNewThread(this::checkFavorite);
            System.out.println(dexEntry);


            nextCard.setDisable(false);
            favoriteCard.setDisable(false);
            prevCard.setDisable(false);
            saveButton.setDisable(false);
            loadButton.setDisable(false);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error sending request");
            Platform.runLater(() -> {
                loadingText.setText("Error finding " + pokemonName.toLowerCase() + "!");
                loadingBar.setProgress(1);
                pokemonInfoText.setText("Error finding " + pokemonName.toLowerCase() + "!" + "\n\n" + e.getMessage());
                sendAlert(e);
                if (cards.size() > 0) {
                    prevCard.setDisable(false);
                    nextCard.setDisable(false);
                    favoriteCard.setDisable(false);
                }
                saveButton.setDisable(false);
                loadButton.setDisable(false);

            });
            e.printStackTrace();
        } // try/catch
        // activate buttons


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
     * and updates the favorite button accordingly.
     * @param index the index of the current card
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
        VBox favoritesRoot = new VBox(8);
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
            Button viewButton = new Button("View");
            final int finalI = i;
            removeButton.setOnAction(e -> {

                favoriteCards.remove(finalI);
                favoriteCardIDs.remove(finalI);
                favoriteCardImages.remove(finalI);
                favoritesGrid.getChildren().removeAll(removeButton, favoriteView);
                Platform.runLater(this::checkFavorite);
                showFavorites();
                if (favoriteCards.size() == 0) {
                    favoritesStage.close();
                } // if
            });
            viewButton.setOnAction(e -> showLink(finalI));
            VBox cardInfo = new VBox(5);
            cardInfo.setAlignment(Pos.CENTER);
            cardInfo.getChildren().addAll(removeButton, viewButton);
            StackPane cardPane = new StackPane(favoriteView, cardInfo);
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
     * Sends an alert to the user with the exception's message
     */
    public void sendAlert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not find Pokémon");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
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

    /**
     * Saves the list of favorite cards to a database in Firebase
     */
    public void saveFavorites(List<PokeTcgResponse.Card> favoriteCards, String key) throws IOException, InterruptedException {
        searchButton.setDisable(true);
        favoriteCard.setDisable(true);
        loadButton.setDisable(true);
        saveButton.setDisable(true);

        Gson gson = new Gson();
        String json = gson.toJson(favoriteCards);


        String databaseName = "pokemon-api-992c5-default-rtdb";
        // push the json string as the path
        String path = "/" + key + ".json";

        String url = String.format("https://%s.firebaseio.com%s?", databaseName, path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
           Platform.runLater(() -> loadingText.setText("Favorites saved successfully."));
        } else {
            sendAlert(new IOException(response.toString()));
            Platform.runLater(() -> favoriteCard.setText("Error saving favorites"));
        }
        searchButton.setDisable(false);
        favoriteCard.setDisable(false);
        loadButton.setDisable(false);
        saveButton.setDisable(false);
    }

    /**
     * Loads the list of favorite cards from a database in Firebase
     */
    public void loadFavorites(String key) throws IOException, InterruptedException {
        loadButton.setDisable(true);
        saveButton.setDisable(true);
        searchButton.setDisable(true);
        prevCard.setDisable(true);
        nextCard.setDisable(true);
        favoriteCard.setDisable(true);

        Platform.runLater(() -> loadingText.setText("Loading favorites..."));

        String databaseName = "pokemon-api-992c5-default-rtdb";
        // push the json string as the path
        String path = "/" + key + ".json";

        String url = String.format("https://%s.firebaseio.com%s?", databaseName, path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Favorites loaded successfully.");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PokeTcgResponse.Card>>() {
            }.getType();
            List<PokeTcgResponse.Card> favoriteCards = gson.fromJson(response.body(), listType);



            if (favoriteCards == null) {
                Platform.runLater(() -> loadingText.setText("No favorites found. \n Check your key and try again."));
                searchButton.setDisable(false);
                prevCard.setDisable(false);
                nextCard.setDisable(false);
                favoriteCard.setDisable(false);
                loadButton.setDisable(false);
                saveButton.setDisable(false);
                return;
            }
            favoriteCardIDs.clear();
            favoriteCardImages.clear();
            this.favoriteCards = favoriteCards;

            // get the images for the favorite cards
            for (int i = 0; i < favoriteCards.size(); i++) {
                PokeTcgResponse.Card card = favoriteCards.get(i);
                favoriteCardIDs.add(card.id);
                String imageUrl = card.images.small;

                Image image = new Image(imageUrl);
                int finalI = i;
                Platform.runLater(() -> loadingBar.setProgress((double) finalI / favoriteCards.size()));

                favoriteCardImages.add(image);
            }
            Platform.runLater(() -> loadingBar.setProgress(1));


            Platform.runLater(() -> {
                loadingText.setText("Favorites loaded successfully.");
                showFavorites();
                if (cards.size() > 0) {
                    checkFavorite();
                }
            });

        } else {
            sendAlert(new IOException(response.toString()));
            Platform.runLater(() -> loadingText.setText("Error loading favorites."));
            return;
        }
        // check if cards are loaded
        if (cards.size() > 0) {
            prevCard.setDisable(false);
            nextCard.setDisable(false);
            favoriteCard.setDisable(false);
        }

        searchButton.setDisable(false);

        loadButton.setDisable(false);
        saveButton.setDisable(false);
    }

    /**
     * Shows the link to the card's page on the Pokémon TCG website
     * @param index the index of the card in the list of favorite cards
     */
    public void showLink(int index) {
        String link = favoriteCards.get(index).tcgplayer.url;
        Platform.runLater(() -> {

            Stage linkStage = new Stage();
            linkStage.initModality(Modality.APPLICATION_MODAL);
            linkStage.setTitle("Link");
            linkStage.setMinWidth(250);

            VBox linkText = new VBox(10);
            Text titleText = new Text("Link to card:");
            TextField tcgPlayerURL = new TextField(link);
            TextField cardName = new TextField(favoriteCards.get(index).name);
            TextField cardmarketURL = new TextField(favoriteCards.get(index).cardmarket.url);
            cardName.setEditable(false);
            cardmarketURL.setEditable(false);
            tcgPlayerURL.setEditable(false);
            linkText.getChildren().addAll(titleText, cardName, tcgPlayerURL, cardmarketURL);

            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> linkStage.close());

            VBox layout = new VBox(10);
            layout.getChildren().addAll(linkText, closeButton);
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout);
            linkStage.setScene(scene);
            linkStage.showAndWait();
        });
    }

} // ApiApp
