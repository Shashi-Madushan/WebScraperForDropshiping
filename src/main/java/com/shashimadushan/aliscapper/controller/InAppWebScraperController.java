package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.service.WebScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scrape")
public class InAppWebScraperController {

    private final WebScraperService webScraperService;

    public InAppWebScraperController(WebScraperService webScraperService) {
        this.webScraperService = webScraperService;
    }

    @GetMapping("/aliexpress")
    public Map<String, Object> scrapeAliExpress(@RequestParam String url) {
        return webScraperService.scrapeAliExpressProduct(url);


    }
}