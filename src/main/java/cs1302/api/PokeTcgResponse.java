package cs1302.api;

import java.util.List;

/**
 * Represents a response from the PokeTcg API. This is used by Gson to
 * create an object from the JSON response body.
 */
public class PokeTcgResponse {
    public int count;
    public List<Card> data;

    public static class Card {
        public String id;
        public boolean favorite = false;
        public String name;
        public String level;
        public String hp;
        public String rarity;
        public Images images;
        public CardBuy tcgplayer;
        public CardBuy cardmarket;

        /**
         * Holds the image urls for the card.
         */
        public static class Images {
            public String small;
            public String large;
        }
        /**
         * Holds the url for the card.
         */

        public static class CardBuy {
            public String url;
        }
    }




}
