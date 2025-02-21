package com.shashimadushan.aliscapper.controller;

import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@RestController
@RequestMapping("/api/scrape")
@CrossOrigin(origins = "*")
public class ExtentionDataController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/receive")
    public Response receiveScrapedData(@RequestBody Map<String, Object> scrapedData) {
        try {
            String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(scrapedData);
            System.out.println("Received Scraped Data as JSON:");
            System.out.println(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Response response = new Response();
        response.setStatus("success");
        response.setMessage("Data received successfully!");

        return response;
    }

    public static class Response {
        private String status;
        private String message;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}