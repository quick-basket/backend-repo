package com.grocery.quickbasket.location.service;

import com.grocery.quickbasket.store.dto.StoreWithDistanceDto;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.user.entity.UserAddress;
import org.locationtech.jts.geom.Point;

public interface LocationService {
    StoreWithDistanceDto findNearestStore(UserAddress userAddress);
    StoreWithDistanceDto findNearestStore(double latitude, double longitude);
    double calculateDistance(UserAddress userAddress, Store store);
    Point createPoint(double longitude, double latitude);
}
