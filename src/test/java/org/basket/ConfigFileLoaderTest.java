package org.basket;

import org.basket.exception.ConfigFileLoadingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigFileLoaderTest {
    private static String configFileDirPath = null;

    @BeforeAll
    public static void setup() {
        configFileDirPath = System.getenv("OCADO_BASKET_TEST_FILES_DIR_PATH");

        if (configFileDirPath == null) {
            throw new IllegalArgumentException("Failed to set config test file dir path");
        }

        System.out.println("Config test file dir path: " + configFileDirPath);
    }

    @Test
    public void nonExistingTestFileShouldReturnConfigFileLoadingException() {
        String testFileName = "non-existing.json";
        String absolutePathToTestConfigFile = configFileDirPath + testFileName;

        assertThrows(ConfigFileLoadingException.class, () -> {
            ConfigFileLoader.retrieveProductsDeliveryMethodsFromConfigFile(absolutePathToTestConfigFile);
        });
    }

    @Test
    public void malformedJsonTestFileShouldReturnConfigFileLoadingException() {
        String testFileName = "malformed-config.json";
        String absolutePathToTestConfigFile = configFileDirPath + testFileName;

        assertThrows(ConfigFileLoadingException.class, () -> {
            ConfigFileLoader.retrieveProductsDeliveryMethodsFromConfigFile(absolutePathToTestConfigFile);
        });
    }

    @Test
    public void validTestFileShouldReturnValidProductsDeliveryMethodsMapContent() {
        String testFileName = "config.json";
        String absolutePathToTestConfigFile = configFileDirPath + testFileName;

        assertDoesNotThrow(() -> {
            Map<String, List<String>> productsDeliveryMethods = ConfigFileLoader
                    .retrieveProductsDeliveryMethodsFromConfigFile(absolutePathToTestConfigFile);

            assertNotNull(productsDeliveryMethods);
            assertFalse(productsDeliveryMethods.isEmpty());
            assertTrue(productsDeliveryMethods.containsKey("Carrots (1kg)"));
            assertTrue(productsDeliveryMethods.containsKey("Cold Beer (330ml)"));
            assertTrue(productsDeliveryMethods.containsKey("Steak (300g)"));
            assertTrue(productsDeliveryMethods.containsKey("AA Battery (4 Pcs.)"));
            assertTrue(productsDeliveryMethods.containsKey("Espresso Machine"));
            assertTrue(productsDeliveryMethods.containsKey("Garden Chair"));

            List<String> carrotsDeliveryOptions = productsDeliveryMethods.get("Carrots (1kg)");
            assertEquals(2, carrotsDeliveryOptions.size());
            assertTrue(carrotsDeliveryOptions.contains("Express Delivery"));
            assertTrue(carrotsDeliveryOptions.contains("Click&Collect"));
        });
    }
}
