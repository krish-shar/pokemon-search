package cs1302.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Stores the response of the PokeApi for the Pokédex
 * entries.
 */
public class DexResponse {
    @SerializedName("flavor_text_entries")
    public List<TextEntry> flavorTextEntries;
    public int id;


    /**
     * Stores the text entries for the Pokédex.
     */
    public static class TextEntry {
        @SerializedName("flavor_text")
        public String flavorText;

        public Language language;

        /**
         * Stores the language of the text entry.
         */
        public static class Language {
            public String name;


        } // Language
    } // TextEntry
}
