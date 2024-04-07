package org.basket;

import org.basket.exception.ConfigFileLoadingException;

import java.util.*;

public class BasketSplitter {
    private final Map<String, List<String>> productsDeliveryMethods;

    public BasketSplitter(String absolutePathToConfigFile) throws ConfigFileLoadingException {
        this.productsDeliveryMethods = ConfigFileLoader
                .retrieveProductsDeliveryMethodsFromConfigFile(absolutePathToConfigFile);
    }

    public Map<String, List<String>> getProductsDeliveryMethods() {
        return productsDeliveryMethods;
    }

    public Map<String, List<String>> split(List<String> products) {
        Map<String, List<String>> resultDeliveryGroups = new HashMap<>();
        List<List<String>> deliveryMethodChoosingPriority =
                calculateDeliveryMethodsChoosingPriorityBasedOnCommonOccurrences(products, productsDeliveryMethods);
        List<String> sortedProducts =
                sortProductsListInAscendingOrderBasedOnAvailableDeliveryMethodsNumber(products, productsDeliveryMethods);

        for (String product : sortedProducts) {
            List<String> productDeliveryMethods = productsDeliveryMethods.get(product);
            String deliveryMethod = findPrioritizedDeliveryMethod(
                    productDeliveryMethods,
                    deliveryMethodChoosingPriority,
                    resultDeliveryGroups
            );

            resultDeliveryGroups.computeIfAbsent(deliveryMethod, k -> new ArrayList<>()).add(product);
        }

        return resultDeliveryGroups;
    }

    private List<List<String>> calculateDeliveryMethodsChoosingPriorityBasedOnCommonOccurrences(
            List<String> products, Map<String, List<String>> productsDeliveryMethods) {
        Map<String, Integer> deliveryMethodCommonOccurrencesCounter = new HashMap<>();

        // Count occurrences of delivery methods for each product
        for (String product : products) {
            List<String> deliveryMethodsForProduct = productsDeliveryMethods.get(product);
            if (deliveryMethodsForProduct != null) {
                for (String deliveryMethod : deliveryMethodsForProduct) {
                    deliveryMethodCommonOccurrencesCounter.put(
                            deliveryMethod,
                            deliveryMethodCommonOccurrencesCounter
                                    .getOrDefault(deliveryMethod, 0) + 1
                    );
                }
            }
        }

        // Sort delivery methods based on occurrence count
        List<Map.Entry<String, Integer>> sortedDeliveryMethods = new ArrayList<>(
                deliveryMethodCommonOccurrencesCounter.entrySet());
        sortedDeliveryMethods.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Construct list representing delivery method choosing priority
        List<List<String>> deliveryMethodPriority = new ArrayList<>();

        // Initialize last occurrence counter
        int lastEntryCommonOccurrencesCounter = -1;
        for (Map.Entry<String, Integer> entry : sortedDeliveryMethods) {
            // Check if a new list needs to be added
            if (entry.getValue() != lastEntryCommonOccurrencesCounter) {
                List<String> newList = new ArrayList<>();
                newList.add(entry.getKey());
                deliveryMethodPriority.add(newList);
            } else {
                // Add to the existing list
                deliveryMethodPriority.get(deliveryMethodPriority.size() - 1).add(entry.getKey());
            }
            // Update last occurrence counter
            lastEntryCommonOccurrencesCounter = entry.getValue();
        }


        return deliveryMethodPriority;
    }

    private List<String> sortProductsListInAscendingOrderBasedOnAvailableDeliveryMethodsNumber(
            List<String> products, Map<String, List<String>> productsDeliveryMethods) {

        if (products.size() < 2) {
            return products;
        }

        // Create a map to store the count of delivery methods for each product
        Map<String, Integer> productDeliveryCountMap = new HashMap<>();

        // Calculate the count of delivery methods for each product
        for (String product : products) {
            List<String> deliveryMethods = productsDeliveryMethods.get(product);
            if (deliveryMethods != null) {
                productDeliveryCountMap.put(product, deliveryMethods.size());
            } else {
                productDeliveryCountMap.put(product, 0); // If no delivery methods found, count as 0
            }
        }

        // Sort the product list based on the count of delivery methods
        products.sort(Comparator.comparingInt(productDeliveryCountMap::get));

        return products;
    }

    private String findPrioritizedDeliveryMethod(List<String> productDeliveryMethods,
                                                 List<List<String>> deliveryMethodPriority,
                                                 Map<String, List<String>> resultDeliveryGroups) {

        if (productDeliveryMethods.size() == 1) {
            return productDeliveryMethods.getFirst();
        }

        for (List<String> topPriorityDeliveryMethods : deliveryMethodPriority) {
            String delivery = topPriorityDeliveryMethods.getFirst();

            if (topPriorityDeliveryMethods.size() > 1) {
                for (String topPriorityWithPossibleExistingDeliveryGroup : topPriorityDeliveryMethods) {
                    if (resultDeliveryGroups.containsKey(topPriorityWithPossibleExistingDeliveryGroup)) {
                        delivery = topPriorityWithPossibleExistingDeliveryGroup;
                    }
                }
            }

            if (productDeliveryMethods.contains(delivery)) {
                return delivery;
            }
        }

        return productDeliveryMethods.getFirst();
    }
}