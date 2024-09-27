package com.grocery.quickbasket.store.repository;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.store.dto.StoreResponseDto;
import com.grocery.quickbasket.store.entity.Store;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>{
    boolean existsByName(String name);
    List<Store> getStoreByDeletedAtIsNull();

    @Query(value = "SELECT s.* FROM stores s " +
            "ORDER BY ST_Distance(s.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) " +
            "LIMIT 1", nativeQuery = true)
    Store findNearestStore(@Param("longitude") double longitude, @Param("latitude") double latitude);

    @Query(value = "SELECT ST_Distance(s.location, :userLocation) / 1000 as distance_km " +
            "FROM stores s WHERE s.id = :storeId", nativeQuery = true)
    double calculateDistanceInKm(@Param("storeId") Long storeId, @Param("userLocation") Point userLocation);

    @Query("SELECT NEW com.grocery.quickbasket.store.dto.StoreResponseDto(s.id, s.name) FROM Store s WHERE s.id NOT IN (SELECT sa.store.id FROM StoreAdmin sa)")
    List<StoreResponseDto> findAllStoreNotInStoreAdmins();
}
