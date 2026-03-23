package com.asset.asset_management.repository;


import com.asset.asset_management.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AssetRepository extends JpaRepository<Asset, Long> {
}