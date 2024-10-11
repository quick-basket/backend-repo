package com.grocery.quickbasket.location.service.Impl;

import com.grocery.quickbasket.location.service.LocationService;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.dto.StoreWithDistanceDto;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.service.StoreService;
import com.grocery.quickbasket.user.entity.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final StoreService storeService;
    private final GeometryFactory gef = new GeometryFactory();

    public LocationServiceImpl(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public StoreWithDistanceDto findNearestStore(UserAddress userAddress) {
        Point location = userAddress.getLocation();
        log.info("user address user: {}", userAddress.getId());
        log.info("user address user: {}", userAddress.getAddress());
        log.info("user address user: {}", userAddress.getLocation());
        log.info("location user: {}", location);
        log.info("location user X: {}", location.getX());
        log.info("location user Y: {}", location.getY());
        Store storeLocation = storeService.findNearestStore(location.getX(), location.getY());

        double distance = calculateDistance(userAddress, storeLocation);

        StoreDto storeDto = StoreDto.fromEntity(storeLocation);
        StoreWithDistanceDto storeWithDistanceDto = new StoreWithDistanceDto();
        storeWithDistanceDto.setStore(storeDto);
        storeWithDistanceDto.setDistance(distance);
        storeWithDistanceDto.setDeliveryCost(calculateDeliveryCost(distance));

        return storeWithDistanceDto;
    }

    @Override
    public StoreWithDistanceDto findNearestStore(double longitude, double latitude) {
        Point location = gef.createPoint(new Coordinate(longitude, latitude));

        Store storeLocation = storeService.findNearestStore(longitude, latitude);
        double distance = storeService.calculateDistance(storeLocation.getId(), location);

        StoreDto storeDto = StoreDto.fromEntity(storeLocation);
        StoreWithDistanceDto storeWithDistanceDto = new StoreWithDistanceDto();
        storeWithDistanceDto.setStore(storeDto);
        storeWithDistanceDto.setDistance(distance);
        storeWithDistanceDto.setDeliveryCost(calculateDeliveryCost(distance));
        return storeWithDistanceDto;
    }

    @Override
    public double calculateDistance(UserAddress userAddress, Store store) {
        return storeService.calculateDistance(store.getId(), userAddress.getLocation());
    }

    @Override
    public Point createPoint(double longitude, double latitude) {
        return gef.createPoint(new Coordinate(longitude, latitude));
    }

    @Override
    public BigDecimal calculateDeliveryCost(double distance) {
        if (distance <= 5.0){
            return BigDecimal.ZERO;
        } else {
            double extraDistance = Math.max(0, distance - 5.0);
            long extraKm = Math.round(Math.ceil(extraDistance));
            return new BigDecimal(extraKm * 5000);
        }
    }
}
