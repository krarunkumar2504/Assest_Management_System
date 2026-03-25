package com.asset.asset_management.controller;

import com.asset.asset_management.model.Asset;
import com.asset.asset_management.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetRepository repo;

    public AssetController(AssetRepository repo) {
        this.repo = repo;
    }

    // GET all assets
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(repo.findAll());
    }

    // GET asset by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAsset(@PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("success", false, "message", "Asset not found")));
    }

    // ADD new asset
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAsset(@RequestBody Asset asset) {
        Asset saved = repo.save(asset);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Asset \"" + saved.getAssetName() + "\" created successfully");
        response.put("asset", saved);
        return ResponseEntity.status(201).body(response);
    }

    // UPDATE asset
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAsset(@PathVariable Long id, @RequestBody Asset updated) {
        Map<String, Object> response = new HashMap<>();

        Asset asset = repo.findById(id).orElse(null);
        if (asset == null) {
            response.put("success", false);
            response.put("message", "Asset not found with ID: " + id);
            return ResponseEntity.status(404).body(response);
        }

        // Update all provided fields
        if (updated.getAssetName()    != null) asset.setAssetName(updated.getAssetName());
        if (updated.getAssetType()    != null) asset.setAssetType(updated.getAssetType());
        if (updated.getLocation()     != null) asset.setLocation(updated.getLocation());
        if (updated.getStatus()       != null) asset.setStatus(updated.getStatus());
        if (updated.getDepartment()   != null) asset.setDepartment(updated.getDepartment());
        if (updated.getCurrentValue() != null) asset.setCurrentValue(updated.getCurrentValue());
        if (updated.getDescription()  != null) asset.setDescription(updated.getDescription());
        if (updated.getPurchaseDate() != null) asset.setPurchaseDate(updated.getPurchaseDate());

        Asset saved = repo.save(asset);
        response.put("success", true);
        response.put("message", "Asset \"" + saved.getAssetName() + "\" updated successfully");
        response.put("asset", saved);
        return ResponseEntity.ok(response);
    }

    // DELETE asset
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAsset(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Asset asset = repo.findById(id).orElse(null);
        if (asset == null) {
            response.put("success", false);
            response.put("message", "Asset not found with ID: " + id);
            return ResponseEntity.status(404).body(response);
        }

        String assetName = asset.getAssetName();
        repo.deleteById(id);

        response.put("success", true);
        response.put("message", "Asset \"" + assetName + "\" deleted successfully");
        return ResponseEntity.ok(response);
    }
}