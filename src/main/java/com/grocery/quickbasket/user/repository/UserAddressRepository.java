package com.grocery.quickbasket.user.repository;

import com.grocery.quickbasket.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    @Query(value = "SELECT * FROM user_addresses WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :radiusInMeters)", nativeQuery = true)
    List<UserAddress> findAddressesWithinRadius(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radiusInMeters") double radiusInMeters);

    @Query(value = "SELECT ST_Distance(a.location, b.location) FROM user_addresses a, user_addresses b WHERE a.id = :addressId1 AND b.id = :addressId2", nativeQuery = true)
    double calculateDistance(@Param("addressId1") int addressId1, @Param("addressId2") int addressId2);

    @Query(value = "SELECT * FROM user_addresses WHERE user_id = :userId", nativeQuery = true)
    List<UserAddress> findByUserId(@Param("userId") int userId);

    @Query(value = "SELECT ST_AsText(location) FROM user_addresses WHERE id = :addressId", nativeQuery = true)
    String getAddressLocationAsText(@Param("addressId") int addressId);
}
