package org.basket;

import org.basket.exception.ConfigFileLoadingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BasketSplitterTest {
    private final String absolutePathToTestConfigFile;

    public BasketSplitterTest() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("config.json")).getFile());
        this.absolutePathToTestConfigFile = file.getAbsolutePath();

        System.out.println("Absolute path to test config file:\t" + absolutePathToTestConfigFile);
    }

    @Test
    public void invalidConfigFilePathShouldReturnConfigFileLoadingException() {
        String invalidPathToConfigFile = "/invalid/path/to/test/config/file.json";

        assertThrows(ConfigFileLoadingException.class, () -> {
            BasketSplitter splitter = new BasketSplitter(invalidPathToConfigFile);
        });
    }

    @Test
    public void validTestConfigFileShouldReturnProductsDeliveryMethods() {
        assertDoesNotThrow(() -> {
            BasketSplitter splitter = new BasketSplitter(absolutePathToTestConfigFile);
            assertNotNull(splitter);

            Map<String, List<String>> productsDeliveryMethods = splitter.getProductsDeliveryMethods();
            assertNotNull(productsDeliveryMethods);
            assertFalse(productsDeliveryMethods.isEmpty());
            assertEquals(100, productsDeliveryMethods.size());
            assertTrue(productsDeliveryMethods.containsKey("Longos - Chicken Curried"));
            List<String> longosDeliveryMethods = productsDeliveryMethods.get("Longos - Chicken Curried");
            assertEquals(3, longosDeliveryMethods.size());
            assertTrue(longosDeliveryMethods.contains("Express Collection"));
            assertTrue(longosDeliveryMethods.contains("Same day delivery"));
            assertTrue(longosDeliveryMethods.contains("Courier"));
        });
    }

    @Test
    public void emptyProductListShouldReturnEmptyDeliveryGroup() {
        List<String> productsList = List.of();

        assertDoesNotThrow(() -> {
            BasketSplitter splitter = new BasketSplitter(absolutePathToTestConfigFile);
            assertNotNull(splitter);

            Map<String, List<String>> result = splitter.split(productsList);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        });
    }

    @Test
    public void productListNO1ShouldReturnOneDeliveryGroups() {
        List<String> productList = Arrays.asList(
                "Cookies Oatmeal Raisin",
                "Cheese Cloth",
                "English Muffin"
        );

        assertDoesNotThrow(() -> {
            BasketSplitter splitter = new BasketSplitter(absolutePathToTestConfigFile);
            assertNotNull(splitter);

            Map<String, List<String>> result = splitter.split(productList);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertTrue(result.containsKey("Parcel locker"));

            List<String> parcelLockerDeliveryProducts = result.get("Parcel locker");
            assertEquals(3, parcelLockerDeliveryProducts.size());
            assertTrue(parcelLockerDeliveryProducts.contains("Cookies Oatmeal Raisin"));
            assertTrue(parcelLockerDeliveryProducts.contains("Cheese Cloth"));
            assertTrue(parcelLockerDeliveryProducts.contains("English Muffin"));
        });
    }

    @Test
    public void productListNO2ShouldReturnTwoDeliveryGroups() {
        List<String> productsList = Arrays.asList(
                "Cocoa Butter",
                "Tart - Raisin And Pecan",
                "Table Cloth 54x72 White",
                "Flower - Daisies",
                "Fond - Chocolate",
                "Cookies - Englishbay Wht"
        );

        assertDoesNotThrow(() -> {
            BasketSplitter splitter = new BasketSplitter(absolutePathToTestConfigFile);
            assertNotNull(splitter);

            Map<String, List<String>> result = splitter.split(productsList);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(2, result.size());

            assertTrue(result.containsKey("Courier"));
            List<String> courierProducts = result.get("Courier");
            assertEquals(5, courierProducts.size());
            assertTrue(courierProducts.contains("Tart - Raisin And Pecan"));
            assertTrue(courierProducts.contains("Table Cloth 54x72 White"));
            assertTrue(courierProducts.contains("Cookies - Englishbay Wht"));
            assertTrue(courierProducts.contains("Flower - Daisies"));
            assertTrue(courierProducts.contains("Cocoa Butter"));

            assertFalse(result.containsKey("Pick-up point"));
            assertFalse(result.containsKey("Parcel locker"));
            assertFalse(result.containsKey("Next day shipping"));
            assertFalse(result.containsKey("In-store pick-up"));
            assertFalse(result.containsKey("Same day delivery"));
        });
    }

    @Test
    public void productListNO3ShouldReturnThreeDeliveryGroups() {
        List<String> productsList = Arrays.asList(
                "Fond - Chocolate",
                "Chocolate - Unsweetened",
                "Nut - Almond, Blanched, Whole",
                "Haggis",
                "Mushroom - Porcini Frozen",
                "Cake - Miini Cheesecake Cherry",
                "Sauce - Mint",
                "Longan",
                "Bag Clear 10 Lb",
                "Nantucket - Pomegranate Pear",
                "Puree - Strawberry",
                "Numi - Assorted Teas",
                "Apples - Spartan",
                "Garlic - Peeled",
                "Cabbage - Nappa",
                "Bagel - Whole White Sesame",
                "Tea - Apple Green Tea"
        );

        assertDoesNotThrow(() -> {
            BasketSplitter splitter = new BasketSplitter(absolutePathToTestConfigFile);
            assertNotNull(splitter);

            Map<String, List<String>> result = splitter.split(productsList);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(3, result.size());

            assertTrue(result.containsKey("Courier"));
            List<String> courierProducts = result.get("Courier");
            assertEquals(1, courierProducts.size());
            assertTrue(courierProducts.contains("Cake - Miini Cheesecake Cherry"));

            assertTrue(result.containsKey("Same day delivery"));
            List<String> sameDayDeliveryProducts = result.get("Same day delivery");
            assertEquals(3, sameDayDeliveryProducts.size());
            assertTrue(sameDayDeliveryProducts.contains("Garlic - Peeled"));
            assertTrue(sameDayDeliveryProducts.contains("Numi - Assorted Teas"));
            assertTrue(sameDayDeliveryProducts.contains("Sauce - Mint"));

            assertTrue(result.containsKey("Express Collection"));
            List<String> expressCollectionProducts = result.get("Express Collection");
            assertEquals(13, expressCollectionProducts.size());
            assertTrue(expressCollectionProducts.contains("Apples - Spartan"));
            assertTrue(expressCollectionProducts.contains("Fond - Chocolate"));
            assertTrue(expressCollectionProducts.contains("Nut - Almond, Blanched, Whole"));
            assertTrue(expressCollectionProducts.contains("Nantucket - Pomegranate Pear"));
            assertTrue(expressCollectionProducts.contains("Puree - Strawberry"));
            assertTrue(expressCollectionProducts.contains("Mushroom - Porcini Frozen"));
            assertTrue(expressCollectionProducts.contains("Cabbage - Nappa"));
            assertTrue(expressCollectionProducts.contains("Bagel - Whole White Sesame"));
            assertTrue(expressCollectionProducts.contains("Haggis"));
            assertTrue(expressCollectionProducts.contains("Longan"));
            assertTrue(expressCollectionProducts.contains("Bag Clear 10 Lb"));
            assertTrue(expressCollectionProducts.contains("Chocolate - Unsweetened"));
            assertTrue(expressCollectionProducts.contains("Tea - Apple Green Tea"));
        });
    }
}