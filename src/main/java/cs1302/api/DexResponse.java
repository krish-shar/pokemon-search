package cs1302.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DexResponse {
    @SerializedName("flavor_text_entries")
    public List<TextEntry> flavorTextEntries;

    public static class TextEntry {
        @SerializedName("flavor_text")
        public String flavorText;
        public Language language;

        public static class Language {
            public String name;


        } // Language
    } // TextEntry
}
