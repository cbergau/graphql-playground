package de.christianbergau.graphqlspring.resolver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Component
public class DataResolver {
    private String basketServiceBaseUrl = "https://www.esprit.de/services/basketservice";

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

            // Convert JSON String to Map and return it
            Type mapType = new TypeToken<Map<String, Map>>() {}.getType();
            Map<String, String[]> result = new Gson().fromJson(content.toString(), mapType);
            return result;
        };
    }
}
