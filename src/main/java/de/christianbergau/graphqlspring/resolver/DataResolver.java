package de.christianbergau.graphqlspring.resolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

@Component
public class DataResolver {
    static final Gson GSON = new GsonBuilder()
            //
            // This is important because the graphql spec says that null values should be present
            //
            .serializeNulls()
            .create();

    private String basketServiceBaseUrl = "https://www.esprit.de/services/basketservice";

    private static Map<String, Object> toMap(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().length() == 0) {
            return Collections.emptyMap();
        }
        // gson uses type tokens for generic input like Map<String,Object>
        TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {
        };
        Map<String, Object> map = GSON.fromJson(jsonStr, typeToken.getType());
        return map == null ? Collections.emptyMap() : map;
    }

    public DataFetcher basketByIdFetcher() {
        return dataFetchingEnvironment -> {
            String basketId = dataFetchingEnvironment.getArgument("basketId");

            // Call Basket Service
            URL url = new URL(basketServiceBaseUrl + "/v1/basket/" + basketId + "?isoCode=de-DE");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StringBuilder content;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {

                    String line;
                    content = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        content.append(line);
                        content.append(System.lineSeparator());
                    }
                }

                System.out.println(content.toString());
            } finally {
                connection.disconnect();
            }

            return toMap(content.toString()).get("basket");
        };
    }
}
