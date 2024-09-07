package com.grocery.quickbasket.location;

import com.grocery.quickbasket.location.service.LocationService;
import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.user.entity.UserAddress;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/nearest-store")
    public ResponseEntity<?> getNearestStore(@RequestParam double longitude, @RequestParam double latitude) {
        return Response.successResponse("Find nearest store", locationService.findNearestStore(longitude, latitude));
    }

    @GetMapping("/calculate-distance")
    public ResponseEntity<?> calculateDistance(
            @RequestParam double userLat, @RequestParam double userLon,
            @RequestParam Long storeId) {
        Point userPoint = locationService.createPoint(userLon, userLat);
        UserAddress dummyAddress = new UserAddress();
        dummyAddress.setLocation(userPoint);
        Store store = new Store();
        store.setId(storeId);
        double distance = locationService.calculateDistance(dummyAddress, store);
        return ResponseEntity.ok(Map.of("distance", distance));
    }
}
