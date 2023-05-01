package cs1302.api;
import com.google.gson.annotations.SerializedName;

public class PokeApiResponse {

    public String name;
    public int id;
    public int height;
    public int weight;
    public int base_experience;
    public Forms[] forms;
    public Game[] game_indices;
    public Sprites sprites;





    public static class Forms {
        public String name;
        public String url;
    }

    public static class Game {
        public int game_index;
        public Version version;

        public static class Version {
            public String name;
            public String url;
        }
    }

    public static class Sprites {
        public String front_default;
        public String front_shiny;

        public OtherSprites other;

    }

    public static class OtherSprites {
        @SerializedName("official-artwork")
        public OfficialArtwork official_artwork;

        public static class OfficialArtwork {
            public String front_default;
            public String front_shiny;
        }
    }

}
