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
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class AssetController {

    private final AssetRepository repo;

    public AssetController(AssetRepository repo) {
        this.repo = repo;
    }

    // ── GET all ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(repo.findAll());
    }

    // ── GET by ID ────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getAsset(@PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, Object> err = new HashMap<>();
                    err.put("success", false);
                    err.put("message", "Asset not found with ID: " + id);
                    return ResponseEntity.status(404).body(err);
                });
    }

    // ── CREATE ───────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAsset(@RequestBody Asset asset) {
        Map<String, Object> response = new HashMap<>();
        try {
            Asset saved = repo.save(asset);
            response.put("success", true);
            response.put("message", "Asset \"" + saved.getAssetName() + "\" created successfully");
            response.put("asset", saved);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create asset: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ── UPDATE ───────────────────────────────────────────────
    // Core fields (assetName, assetType, location, status) always updated.
    // Optional fields (department, currentValue, description, purchaseDate)
    // are set via reflection — silently skipped if not in your model yet.
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAsset(
            @PathVariable Long id,
            @RequestBody Asset updated) {

        Map<String, Object> response = new HashMap<>();
        try {
            Asset asset = repo.findById(id).orElse(null);
            if (asset == null) {
                response.put("success", false);
                response.put("message", "Asset not found with ID: " + id);
                return ResponseEntity.status(404).body(response);
            }

            // Core fields — always safe
            if (updated.getAssetName() != null) asset.setAssetName(updated.getAssetName());
            if (updated.getAssetType() != null) asset.setAssetType(updated.getAssetType());
            if (updated.getLocation()  != null) asset.setLocation(updated.getLocation());
            if (updated.getStatus()    != null) asset.setStatus(updated.getStatus());

            // Optional fields — safe even if not in model yet
            safeSet(asset, "setDepartment",   updated, "getDepartment");
            safeSet(asset, "setCurrentValue", updated, "getCurrentValue");
            safeSet(asset, "setDescription",  updated, "getDescription");
            safeSet(asset, "setPurchaseDate", updated, "getPurchaseDate");

            Asset saved = repo.save(asset);
            response.put("success", true);
            response.put("message", "Asset \"" + saved.getAssetName() + "\" updated successfully");
            response.put("asset", saved);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update asset: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ── DELETE ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAsset(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
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
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete asset: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ── Reflection helper: set optional fields without crashing ──
    private void safeSet(Asset target, String setterName, Asset source, String getterName) {
        try {
            java.lang.reflect.Method getter = source.getClass().getMethod(getterName);
            Object value = getter.invoke(source);
            if (value != null) {
                java.lang.reflect.Method setter = target.getClass().getMethod(setterName, getter.getReturnType());
                setter.invoke(target, value);
            }
        } catch (NoSuchMethodException ignored) {
            // Field not in model yet — skip silently, no crash
        } catch (Exception ignored) {
            // Any reflection error — skip silently
        }
    }
}