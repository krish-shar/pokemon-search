package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a response from the PokeApi API. This is used by Gson to
 * create an object from the JSON response body.
 */
public class PokeApiResponse {

    public String name;
    public int id;
    public int height;
    public int weight;
    @SerializedName("base_experience")
    public int baseExperience;
    public Forms[] forms;
    @SerializedName("game_indices")
    public Game[] gameIndices;
    public Sprites sprites;
    public Ability[] abilities;
    public Type[] types;
    public Stat[] stats;
    public Move[] moves;

    /**
     * Holds the forms of the Pokémon.
     */
    public static class Forms {
        public String name;
        public String url;
    }

    /**
     * Holds the games in which this Pokémon is present.
     */
    public static class Game {
        @SerializedName("game_index")
        public int gameIndex;
        public Version version;

        /**
         * Holds the version of the game.
         */
        public static class Version {
            public String name;
            public String url;
        }
    }

    /**
     * Holds the sprites of the Pokémon.
     */
    public static class Sprites {
        @SerializedName("front_default")
        public String frontDefault;
        @SerializedName("front_shiny")
        public String frontShiny;

        public OtherSprites other;

    }

    /**
     * Holds the other sprites of the Pokémon.
     */
    public static class OtherSprites {
        @SerializedName("official-artwork")
        public OfficialArtwork official_artwork;

        /**
         * Holds the official artwork of the Pokémon.
         */
        public static class OfficialArtwork {
            @SerializedName("front_default")
            public String frontDefault;
            @SerializedName("front_shiny")
            public String frontShiny;
        }
    }

    /**
     * Holds the abilities of the Pokémon.
     */
    public static class Ability {
        public Ability2 ability;
        @SerializedName("is_hidden")
        public boolean isHidden;
        public int slot;

        /**
         * Holds the ability of the Pokémon.
         */
        public static class Ability2 {
            public String name;
            public String url;
        }
    }

    /**
     * Holds the type of the Pokémon.
     */
    public static class Type {
        public int slot;
        public Type2 type;

        /**
         * Holds the type of the Pokémon.
         */
        public static class Type2 {
            public String name;
            public String url;
        }
    }

    /**
     * Holds the stats of the Pokémon.
     */
    public static class Stat {
        @SerializedName("base_stat")
        public int baseStat;
        public int effort;
        public Stat2 stat;

        /**
         * Holds the stat of the Pokémon.
         */
        public static class Stat2 {
            public String name;
            public String url;
        }

    }

    /**
     * Holds the moves of the Pokémon.
     */
    public static class Move {
        public Move2 move;
        @SerializedName("version_group_details")
        public VersionGroupDetails[] versionGroupDetails;

        /**
         * Holds the move of the Pokémon.
         */
        public static class Move2 {
            public String name;
            public String url;
        }

        /**
         * Holds the version group details of the Pokémon.
         */
        public static class VersionGroupDetails {
            @SerializedName("level_learned_at")
            public int levelLearnedAt;
            @SerializedName("move_learn_method")
            public MoveLearnMethod moveLearnMethod;
            @SerializedName("version_group")
            public VersionGroup versionGroup;

            /**
             * Holds the move learn method of the Pokémon.
             */
            public static class MoveLearnMethod {
                public String name;
                public String url;
            }

            /**
             * Holds the version group of the Pokémon.
             */
            public static class VersionGroup {
                public String name;
                public String url;
            }
        }
    }

}
