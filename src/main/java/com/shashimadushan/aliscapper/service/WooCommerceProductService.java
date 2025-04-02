package com.shashimadushan.aliscapper.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class WooCommerceProductService {

    public JSONObject convertAliExpressToWooCommerce(String aliExpressProductJson) {
        try {
            JSONObject aliExpressProduct = new JSONObject(aliExpressProductJson);
            JSONObject wooProduct = new JSONObject();

            // Set product name
            wooProduct.put("name", aliExpressProduct.optString("productName", ""));


            String currentPrice = aliExpressProduct.optString("currentPrice", "0").replaceAll("[^0-9.,]", "");
            String originalPrice = aliExpressProduct.optString("originalPrice", "").replaceAll("[^0-9.,]", "");

            if (originalPrice.isEmpty() || originalPrice.equals("0") || originalPrice.equals("0.0") ||
                    originalPrice.equals("0.00") || aliExpressProduct.optString("originalPrice", "").equals("N/A") ||
                    (convertToDouble(originalPrice) <= convertToDouble(currentPrice))) {
                // Set original price 20% higher than current price
                double current = convertToDouble(currentPrice);
                double original = current * 1.2;
                originalPrice = String.format("%.2f", original);
            }

            wooProduct.put("regular_price", originalPrice);
            wooProduct.put("sale_price", currentPrice);

            // Build description
            StringBuilder descriptionBuilder = new StringBuilder();
            descriptionBuilder.append("<h2>Product Details</h2>");
            if (aliExpressProduct.has("rating")) {
                descriptionBuilder.append("<p>Rating: ").append(aliExpressProduct.optString("rating", "").trim()).append("</p>");
            }
            if (aliExpressProduct.has("soldCount")) {
                descriptionBuilder.append("<p>Sold: ").append(aliExpressProduct.optString("soldCount", "")).append("</p>");
            }
            if (aliExpressProduct.has("description")) {
                JSONArray descriptions = aliExpressProduct.getJSONArray("description");
                for (int i = 0; i < descriptions.length(); i++) {
                    JSONObject descItem = descriptions.getJSONObject(i);
                    if (descItem.has("text")) {
                        descriptionBuilder.append("<p>").append(descItem.getString("text")).append("</p>");
                    }
                }
            }
            wooProduct.put("description", descriptionBuilder.toString());

            // Process images
            JSONArray images = new JSONArray();
            if (aliExpressProduct.has("imageLinks")) {
                JSONArray imageLinks = aliExpressProduct.getJSONArray("imageLinks");
                for (int i = 0; i < imageLinks.length(); i++) {
                    String imageUrl = imageLinks.getString(i).replace(".avif", ".jpg");
                    JSONObject imageObj = new JSONObject();
                    imageObj.put("src", imageUrl);
                    images.put(imageObj);
                }
            }
            wooProduct.put("images", images);

            // Process specifications as attributes
            if (aliExpressProduct.has("specifications")) {
                JSONObject specs = aliExpressProduct.getJSONObject("specifications");
                JSONArray attributes = new JSONArray();
                for (String key : specs.keySet()) {
                    JSONObject attribute = new JSONObject();
                    attribute.put("name", key);
                    attribute.put("option", specs.getString(key));
                    attribute.put("visible", true);
                    attributes.put(attribute);
                }
                wooProduct.put("attributes", attributes);
            }

            return wooProduct;
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private double convertToDouble(String price) {
        try {
            return Double.parseDouble(price.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
