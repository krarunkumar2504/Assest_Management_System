package com.asset.asset_management.controller;

import com.asset.asset_management.model.Asset;
import com.asset.asset_management.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ─────────────────────────────────────────────────────────────────────────────
// AssetController — corrected against actual DB schema (Dump20260319.sql)
//
// CHANGE SUMMARY vs previous version:
//
// ┌─────────────────────────────────┬──────────────────────────────────────────────────────────────┐
// │ What changed                    │ Why (from schema)                                            │
// ├─────────────────────────────────┼──────────────────────────────────────────────────────────────┤
// │ DELETE child table name fixed:  │ Schema has NO table called "maintenance".                    │
// │  OLD: "maintenance"             │ Real table confirmed in dump:                                │
// │  NEW: "maintenance_records"     │   maintenance_records  (FK: maintenance_records_ibfk_1       │
// │                                 │   → assets.id)                                               │
// ├─────────────────────────────────┼──────────────────────────────────────────────────────────────┤
// │ DELETE: removed ghost tables    │ "asset_history" and "asset_documents" do NOT exist in        │
// │  OLD: "asset_history"           │ the schema dump at all. Removed to eliminate dead code.      │
// │  OLD: "asset_documents"         │                                                              │
// ├─────────────────────────────────┼──────────────────────────────────────────────────────────────┤
// │ UPDATE: removed setDepartment   │ `assets` table has NO `department` column.                   │
// │  OLD: safeSet("setDepartment")  │ Schema columns for assets: asset_name, asset_type,           │
// │  NEW: (removed)                 │ serial_number, purchase_date, purchase_cost, current_value,  │
// │                                 │ useful_life_years, salvage_value, warranty_expiry,           │
// │                                 │ status, location, created_at, updated_at                     │
// ├─────────────────────────────────┼──────────────────────────────────────────────────────────────┤
// │ UPDATE: added real schema cols  │ Schema has purchase_cost, serial_number, salvage_value,      │
// │  NEW: setPurchaseCost           │ useful_life_years, warranty_expiry — all updatable fields    │
// │  NEW: setSerialNumber           │ that the old controller was ignoring.                        │
// │  NEW: setSalvageValue           │                                                              │
// │  NEW: setUsefulLifeYears        │                                                              │
// │  NEW: setWarrantyExpiry         │                                                              │
// └─────────────────────────────────┴──────────────────────────────────────────────────────────────┘
// ─────────────────────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS
})
public class AssetController {

    private final AssetRepository repo;

    // EntityManager needed to run raw SQL for child-table cleanup before delete
    @PersistenceContext
    private EntityManager entityManager;

    public AssetController(AssetRepository repo) {
        this.repo = repo;
    }

    // ── GET all ───────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(repo.findAll());
    }

    // ── GET by ID ─────────────────────────────────────────────────────────────
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

    // ── CREATE ────────────────────────────────────────────────────────────────
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

    // ── UPDATE ────────────────────────────────────────────────────────────────
    //
    // CHANGE: removed safeSet("setDepartment") — `department` is NOT a column
    //         in the `assets` table per schema dump. It was causing reflection
    //         noise and possible confusion if a getter existed elsewhere.
    //
    // CHANGE: added 5 real schema columns that were previously ignored:
    //   purchase_cost      → setPurchaseCost
    //   serial_number      → setSerialNumber
    //   salvage_value      → setSalvageValue
    //   useful_life_years  → setUsefulLifeYears
    //   warranty_expiry    → setWarrantyExpiry
    //
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

            // Core fields — guaranteed in every Asset model
            if (updated.getAssetName() != null) asset.setAssetName(updated.getAssetName());
            if (updated.getAssetType() != null) asset.setAssetType(updated.getAssetType());
            if (updated.getLocation()  != null) asset.setLocation(updated.getLocation());
            if (updated.getStatus()    != null) asset.setStatus(updated.getStatus());

            // Optional fields — all confirmed columns in `assets` schema.
            // safeSet uses reflection: silently skips if getter/setter missing in model.
            //
            // Schema column       Java getter / setter
            // ------------------  ----------------------------------------
            // current_value     → getCurrentValue   / setCurrentValue
            // purchase_date     → getPurchaseDate   / setPurchaseDate
            // purchase_cost     → getPurchaseCost   / setPurchaseCost      ← NEW
            // serial_number     → getSerialNumber   / setSerialNumber      ← NEW
            // salvage_value     → getSalvageValue   / setSalvageValue      ← NEW
            // useful_life_years → getUsefulLifeYears/ setUsefulLifeYears   ← NEW
            // warranty_expiry   → getWarrantyExpiry / setWarrantyExpiry    ← NEW
            //
            // REMOVED: setDepartment — no such column in assets table
            safeSet(asset, "setCurrentValue",    updated, "getCurrentValue");
            safeSet(asset, "setPurchaseDate",    updated, "getPurchaseDate");
            safeSet(asset, "setPurchaseCost",    updated, "getPurchaseCost");
            safeSet(asset, "setSerialNumber",    updated, "getSerialNumber");
            safeSet(asset, "setSalvageValue",    updated, "getSalvageValue");
            safeSet(asset, "setUsefulLifeYears", updated, "getUsefulLifeYears");
            safeSet(asset, "setWarrantyExpiry",  updated, "getWarrantyExpiry");

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

    // ── DELETE (cascade) ──────────────────────────────────────────────────────
    //
    // WHY delete fails: assets.id is referenced by 2 FK constraints:
    //   CONSTRAINT asset_assignments_ibfk_1
    //       FOREIGN KEY (asset_id) REFERENCES assets(id)
    //   CONSTRAINT maintenance_records_ibfk_1
    //       FOREIGN KEY (asset_id) REFERENCES assets(id)
    //
    // FIX: delete child rows in both tables first, then delete parent.
    //
    // CHANGE: "maintenance" → "maintenance_records"
    //   OLD: safeDeleteChildren("maintenance", "asset_id", id)
    //   NEW: safeDeleteChildren("maintenance_records", "asset_id", id)
    //   WHY: No table named "maintenance" exists in schema.
    //        Real table is "maintenance_records" (confirmed in dump).
    //
    // CHANGE: removed "asset_history" and "asset_documents"
    //   WHY: Neither table exists anywhere in the schema dump.
    //
    // Execution order (must be children before parent):
    //   Step 1 → DELETE FROM asset_assignments   WHERE asset_id = ?
    //   Step 2 → DELETE FROM maintenance_records WHERE asset_id = ?
    //   Step 3 → repo.deleteById(id)   ← parent, now safe
    //
    @Transactional
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

            // Step 1 — remove assignment rows (FK: asset_assignments_ibfk_1)
            safeDeleteChildren("asset_assignments",   "asset_id", id);

            // Step 2 — remove maintenance rows (FK: maintenance_records_ibfk_1)
            // CHANGE: was "maintenance" (wrong) → now "maintenance_records" (correct)
            safeDeleteChildren("maintenance_records", "asset_id", id);

            // Step 3 — parent delete is now safe
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

    // ── Raw SQL child-row deleter ─────────────────────────────────────────────
    // Runs: DELETE FROM `table` WHERE `fkColumn` = :id
    // Backticks guard against reserved-word table names.
    // Exception swallowed so missing tables cause no crash.
    private void safeDeleteChildren(String table, String fkColumn, Long id) {
        try {
            entityManager
                    .createNativeQuery(
                            "DELETE FROM `" + table + "` WHERE `" + fkColumn + "` = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        } catch (Exception ignored) {
            // Table or column does not exist — skip silently
        }
    }

    // ── Reflection helper for optional model fields ───────────────────────────
    // Calls getter on source; if non-null, calls matching setter on target.
    // NoSuchMethodException → field not in model yet → skip silently.
    private void safeSet(Asset target, String setterName, Asset source, String getterName) {
        try {
            java.lang.reflect.Method getter = source.getClass().getMethod(getterName);
            Object value = getter.invoke(source);
            if (value != null) {
                java.lang.reflect.Method setter =
                        target.getClass().getMethod(setterName, getter.getReturnType());
                setter.invoke(target, value);
            }
        } catch (NoSuchMethodException ignored) {
            // Getter/setter absent in model — skip silently
        } catch (Exception ignored) {
            // Any other reflection error — skip silently
        }
    }
}