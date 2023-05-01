package cs1302.api;

import java.util.List;

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

        public static class Images {
            public String small;
            public String large;
        }

        public static class CardBuy {
            public String url;
        }
    }


}
