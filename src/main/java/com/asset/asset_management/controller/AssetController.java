package com.asset.asset_management.controller;

import com.asset.asset_management.model.Asset;
import com.asset.asset_management.repository.AssetRepository;
import com.asset.asset_management.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://asset-frontend-xi.vercel.app"
})
public class AssetController {

    private final AssetRepository  assetRepo;
    private final AuditLogService  auditLogService;

    public AssetController(AssetRepository assetRepo,
                           AuditLogService auditLogService) {
        this.assetRepo       = assetRepo;
        this.auditLogService = auditLogService;
    }

    // ✅ GET ALL ASSETS
    @GetMapping("/assets")
    public List<Asset> getAllAssets() {
        return assetRepo.findAll();
    }

    // ✅ GET ASSET BY ID
    @GetMapping("/assets/{id}")
    public Asset getAssetById(@PathVariable Long id) {
        return assetRepo.findById(id).orElse(null);
    }

    // ✅ CREATE ASSET
    @PostMapping("/assets")
    public Asset createAsset(@RequestBody Asset asset) {
        Asset saved = assetRepo.save(asset);

        auditLogService.saveLog(
                "CREATE_ASSET",
                "Created asset " + saved.getAssetName() + " (ID: " + saved.getId() + ")",
                "Admin"
        );

        return saved;
    }

    // ✅ UPDATE ASSET
    @PutMapping("/assets/{id}")
    public Asset updateAsset(@PathVariable Long id, @RequestBody Asset updated) {
        Asset asset = assetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + id));

        if (updated.getAssetName()       != null) asset.setAssetName(updated.getAssetName());
        if (updated.getAssetType()       != null) asset.setAssetType(updated.getAssetType());
        if (updated.getStatus()          != null) asset.setStatus(updated.getStatus());
        if (updated.getLocation()        != null) asset.setLocation(updated.getLocation());
        if (updated.getSerialNumber()    != null) asset.setSerialNumber(updated.getSerialNumber());
        if (updated.getPurchaseCost()    != null) asset.setPurchaseCost(updated.getPurchaseCost());
        if (updated.getCurrentValue()    != null) asset.setCurrentValue(updated.getCurrentValue());
        if (updated.getUsefulLifeYears() != null) asset.setUsefulLifeYears(updated.getUsefulLifeYears());
        if (updated.getSalvageValue()    != null) asset.setSalvageValue(updated.getSalvageValue());
        if (updated.getWarrantyExpiry()  != null) asset.setWarrantyExpiry(updated.getWarrantyExpiry());
        if (updated.getPurchaseDate()    != null) asset.setPurchaseDate(updated.getPurchaseDate());

        Asset saved = assetRepo.save(asset);

        auditLogService.saveLog(
                "UPDATE_ASSET",
                "Updated asset " + saved.getAssetName() + " (ID: " + saved.getId() + ")",
                "Admin"
        );

        return saved;
    }

    // ✅ DELETE ASSET
    @DeleteMapping("/assets/{id}")
    public String deleteAsset(@PathVariable Long id) {
        Asset asset = assetRepo.findById(id).orElse(null);
        if (asset == null) return "❌ Asset not found";

        try {
            String name = asset.getAssetName();
            assetRepo.delete(asset);

            auditLogService.saveLog(
                    "DELETE_ASSET",
                    "Deleted asset " + name + " (ID: " + id + ")",
                    "Admin"
            );

            return "✅ Asset deleted successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Cannot delete asset (linked records exist)";
        }
    }
}