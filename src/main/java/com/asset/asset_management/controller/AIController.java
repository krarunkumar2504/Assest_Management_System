package com.asset.asset_management.controller;

import com.asset.asset_management.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService service;

    public AIController(AIService service) {
        this.service = service;
    }

    @PostMapping("/recommendation")
    public String getAI(@RequestBody Map<String, Object> data) {

        String prompt = "You are an asset management expert.\n\n" +
                "Asset Name: " + data.get("assetName") + "\n" +
                "Type: " + data.get("assetType") + "\n" +
                "Purchase Cost: " + data.get("purchaseCost") + "\n" +
                "Current Value: " + data.get("currentValue") + "\n" +
                "Useful Life: " + data.get("usefulLifeYears") + " years\n" +
                "Maintenance Cost: " + data.get("maintenanceCost") + "\n\n" +
                "Should we repair or replace? Give reason.";

        return service.getRecommendation(prompt);
    }
}