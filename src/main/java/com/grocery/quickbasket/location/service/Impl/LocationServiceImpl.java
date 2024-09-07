package com.grocery.quickbasket.location.service.Impl;

import com.grocery.quickbasket.location.service.LocationService;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.dto.StoreWithDistanceDto;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.service.StoreService;
import com.grocery.quickbasket.user.entity.UserAddress;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {
    private final StoreService storeService;
    private final GeometryFactory gef = new GeometryFactory();

    public LocationServiceImpl(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public StoreWithDistanceDto findNearestStore(UserAddress userAddress) {
        Point location = userAddress.getLocation();
        Store storeLocation = storeService.findNearestStore(location.getX(), location.getY());

        double distance = calculateDistance(userAddress, storeLocation);

        StoreDto storeDto = StoreDto.fromEntity(storeLocation);
        StoreWithDistanceDto storeWithDistanceDto = new StoreWithDistanceDto();
        storeWithDistanceDto.setStore(storeDto);
        storeWithDistanceDto.setDistance(distance);

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
}
