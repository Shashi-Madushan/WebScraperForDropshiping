package com.shashimadushan.aliscapper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;


@Service
public class WebScraperService {
    private static final Logger logger = LoggerFactory.getLogger(WebScraperService.class);
    private static final String CHROME_DRIVER_PATH = "C:\\chromedriver-win64\\chromedriver.exe";
    private static final String CHROME_BINARY_PATH = "C:\\Users\\Shashi\\AppData\\Local\\Google\\Chrome SxS\\Application\\chrome.exe";
    private static final int IMPLICIT_WAIT_SECONDS = 5;
    private static final int EXPLICIT_WAIT_SECONDS = 20;

    public Map<String, Object> scrapeAliExpressProduct(String url) {
        WebDriver driver = null;
        Map<String, Object> result = new HashMap<>();

        try {
            validateUrl(url);
            driver = initializeWebDriver();
            return scrapeProductDetails(driver, url);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid URL provided: {}", url, e);
            result.put("error", "Invalid URL format: " + e.getMessage());
            return result;
        } catch (WebDriverException e) {
            logger.error("WebDriver error while scraping: {}", url, e);
            result.put("error", "Browser automation error: " + e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("Unexpected error while scraping: {}", url, e);
            result.put("error", "Internal server error: " + e.getMessage());
            return result;
        } finally {
            safelyQuitDriver(driver);
        }
    }

    private void validateUrl(String url) {
        if (url == null || !url.matches(".*aliexpress\\.com.*item/\\d+\\.html.*")) {
            throw new IllegalArgumentException("Invalid AliExpress product URL");
        }
    }

    private WebDriver initializeWebDriver() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);

        ChromeOptions options = new ChromeOptions();
        options.setBinary(CHROME_BINARY_PATH);
        options.addArguments("--headless");
        options.addArguments("window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");


        return new ChromeDriver(options);
    }

    private Map<String, Object> scrapeProductDetails(WebDriver driver, String url) {
        Map<String, Object> result = new HashMap<>();
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));

        try {
            result.put("productID", extractProductId(url));
            result.putAll(extractBasicProductDetails(wait));
            result.put("images", extractImages(driver));
            result.put("videos", extractVideos(driver));
            result.put("specifications", extractSpecifications(driver));
            result.put("description", extractDescription(driver));
        } catch (TimeoutException e) {
            logger.error("Timeout while waiting for elements", e);
            throw new RuntimeException("Failed to load product details: Timeout");
        } catch (NoSuchElementException e) {
            logger.error("Required element not found", e);
            throw new RuntimeException("Failed to find required product information");
        }

        return result;
    }

    private String extractProductId(String url) {
        return url.matches(".*item/(\\d+)\\.html.*") ?
                url.replaceAll(".*item/(\\d+)\\.html.*", "$1") :
                "N/A";
    }

    private Map<String, Object> extractBasicProductDetails(WebDriverWait wait) {
        Map<String, Object> details = new HashMap<>();

        details.put("productName", getElementText(wait, ".title--wrap--UUHae_g h1"));
        details.put("currentPrice", getElementText(wait, ".product-price-value"));
        details.put("originalPrice", getElementText(wait, ".price--originalText--gxVO5_d"));
        details.put("discount", getElementText(wait, ".price--discount--Y9uG2LK"));
        details.put("rating", getElementText(wait, ".reviewer--rating--xrWWFzx strong"));
        details.put("soldCount", getElementText(wait, ".reviewer--sold--ytPeoEy"));

        return details;
    }

    private Map<String, String> extractImages(WebDriver driver) {
        Map<String, String> images = new HashMap<>();
        List<WebElement> imageElements = driver.findElements(By.cssSelector(".slider--item--FefNjlj img"));
        for (int i = 0; i < imageElements.size(); i++) {
            String src = imageElements.get(i).getAttribute("src");
            if (src != null && !src.isEmpty()) {
                images.put("image_" + (i + 1), src);
            }
        }
        return images;
    }

    private Map<String, String> extractVideos(WebDriver driver) {
        Map<String, String> videos = new HashMap<>();
        List<WebElement> videoElements = driver.findElements(By.cssSelector(".video--wrap--EhkqzuR video source"));
        for (int i = 0; i < videoElements.size(); i++) {
            String src = videoElements.get(i).getAttribute("src");
            if (src != null && !src.isEmpty()) {
                videos.put("video_" + (i + 1), src);
            }
        }
        return videos;
    }

    private Map<String, String> extractSpecifications(WebDriver driver) {
        Map<String, String> specifications = new HashMap<>();
        List<WebElement> specElements = driver.findElements(By.cssSelector(".specification--list--GZuXzRX li"));

        for (WebElement specElement : specElements) {
            try {
                List<WebElement> props = specElement.findElements(By.cssSelector("div[class^='specification--prop']"));
                for (WebElement prop : props) {
                    WebElement titleElement = prop.findElement(By.cssSelector("div[class^='specification--title']"));
                    WebElement descElement = prop.findElement(By.cssSelector("div[class^='specification--desc']"));
                    if (titleElement != null && descElement != null) {
                        String title = titleElement.getText().trim();
                        String desc = descElement.getText().trim();
                        if (!title.isEmpty() && !desc.isEmpty()) {
                            specifications.put(title, desc);
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                logger.warn("Failed to extract specification element", e);
            }
        }
        return specifications;
    }
    private String extractDescription(WebDriver driver) {
        Map<String, List<String>> descriptionContent = new HashMap<>();
        descriptionContent.put("text", new ArrayList<>());
        descriptionContent.put("images", new ArrayList<>());

        // Click the link to ensure the description is loaded
        try {
            WebElement descriptionLink = driver.findElement(By.cssSelector("a.comet-v2-anchor-link[title='Description']"));
            descriptionLink.click();
            // Wait for the description to be visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".description--wrap--LscZ0He")));
        } catch (NoSuchElementException | TimeoutException e) {
            logger.warn("Description link not found or not clickable", e);
            return "{\"text\":[],\"images\":[]}";
        }

        List<WebElement> elements = driver.findElements(By.cssSelector(".description--wrap--LscZ0He"));

        for (WebElement element : elements) {
            try {
                // Extract and append only relevant paragraph text
                List<WebElement> paragraphs = element.findElements(By.tagName("p"));
                for (WebElement paragraph : paragraphs) {
                    String text = paragraph.getText().trim();
                    if (!text.isEmpty()) {
                        descriptionContent.get("text").add(text);
                    }
                }

                // Extract and append images inside the element
                List<WebElement> images = element.findElements(By.tagName("img"));
                for (WebElement img : images) {
                    String src = img.getAttribute("src");
                    if (src != null && !src.isEmpty()) {
                        descriptionContent.get("images").add(src);
                    }
                }
            } catch (StaleElementReferenceException e) {
                logger.warn("Element became stale while extracting description", e);
            }
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(descriptionContent);
        } catch (Exception e) {
            logger.error("Error serializing description content", e);
            return "{\"text\":[],\"images\":[]}";
        }
    }




    private String getElementText(WebDriverWait wait, String selector) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector))).getText();
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("Element not found: {}", selector);
            return "N/A";
        }
    }

    private void safelyQuitDriver(WebDriver driver) {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            logger.error("Error while closing WebDriver", e);
        }
    }
}