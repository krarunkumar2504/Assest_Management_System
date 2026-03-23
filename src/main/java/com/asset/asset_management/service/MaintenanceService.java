package com.asset.asset_management.service;

import com.asset.asset_management.model.MaintenanceRecord;
import com.asset.asset_management.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    // ── Keep your original variable name "repo" unchanged ────
    private final MaintenanceRepository repo;

    public MaintenanceService(MaintenanceRepository repo) {
        this.repo = repo;
    }

    // ── GET all records (already existed — unchanged) ─────────
    public List<MaintenanceRecord> getAll() {
        return repo.findAll();
    }

    // ── GET single record by ID ───────────────────────────────
    // Returns Optional — controller sends 404 if empty
    public Optional<MaintenanceRecord> getById(Long id) {
        return repo.findById(id);
    }

    // ── CREATE new maintenance record ─────────────────────────
    // Called when user submits the "Schedule Maintenance" modal.
    // Sets id to null to force a DB INSERT (not an accidental UPDATE).
    public MaintenanceRecord create(MaintenanceRecord record) {
        record.setId(null);
        return repo.save(record);
    }

    // ── UPDATE existing maintenance record ────────────────────
    // Called when user submits the Edit modal.
    // Loads the existing row first, copies every field from the
    // request body, then saves — so the id never changes.
    public Optional<MaintenanceRecord> update(Long id, MaintenanceRecord updated) {
        return repo.findById(id).map(existing -> {

            existing.setAssetId(updated.getAssetId());
            existing.setMaintenanceDate(updated.getMaintenanceDate());
            existing.setMaintenanceType(updated.getMaintenanceType());
            existing.setCost(updated.getCost());
            existing.setDescription(updated.getDescription());
            existing.setVendorName(updated.getVendorName());
            existing.setPerformedBy(updated.getPerformedBy());
            existing.setNextDueDate(updated.getNextDueDate());

            return repo.save(existing);
        });
    }

    // ── DELETE maintenance record ─────────────────────────────
    // Called when user confirms the delete dialog.
    // Returns true  → controller sends 204 No Content
    // Returns false → controller sends 404 Not Found
    public boolean delete(Long id) {
        if (!repo.existsById(id)) {
            return false;
        }
        repo.deleteById(id);
        return true;
    }
}
