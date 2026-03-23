package com.asset.asset_management.controller;


import com.asset.asset_management.model.Asset;
import com.asset.asset_management.repository.AssetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://asset-frontend-xi.vercel.app"
})

public class AssetController {

    private final AssetRepository repo;

    public AssetController(AssetRepository repo) {
        this.repo = repo;
    }

    // GET all assets
    @GetMapping
    public List<Asset> getAllAssets() {
        return repo.findAll();
    }

    // GET asset by ID
    @GetMapping("/{id}")
    public Asset getAsset(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    // ADD new asset
    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return repo.save(asset);
    }

    // UPDATE asset
    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id, @RequestBody Asset updated) {

        Asset asset = repo.findById(id).orElse(null);

        if(asset != null) {
            asset.setAssetName(updated.getAssetName());
            asset.setAssetType(updated.getAssetType());
            asset.setLocation(updated.getLocation());
            asset.setStatus(updated.getStatus());
        }

        return repo.save(asset);
    }

    // DELETE asset
    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        repo.deleteById(id);
    }
}