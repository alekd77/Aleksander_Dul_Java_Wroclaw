package org.basket;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.basket.exception.ConfigFileLoadingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ConfigFileLoader {
    public static Map<String, List<String>> retrieveProductsDeliveryMethodsFromConfigFile(
            String absolutePathToConfigFile) throws ConfigFileLoadingException {
        Path configFilePath = Paths.get(absolutePathToConfigFile);

        if (!Files.exists(configFilePath)) {
            throw new ConfigFileLoadingException(
                    "Failed to read " + absolutePathToConfigFile + " JSON config file due to: file does not exist");
        }

        if (!Files.isRegularFile(configFilePath) || !Files.isReadable(configFilePath)) {
            throw new ConfigFileLoadingException(
                    "Failed to read " + absolutePathToConfigFile + " JSON config file due to: file is not readable");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(Files.newBufferedReader(configFilePath),
                    new TypeReference<Map<String, List<String>>>() {});

        } catch (IOException ex) {
            throw new ConfigFileLoadingException(
                    "Failed to read " + absolutePathToConfigFile + " JSON config file due to: I/O error occurred", ex);
        }
    }
}
