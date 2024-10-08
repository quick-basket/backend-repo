package com.grocery.quickbasket.store.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.exceptions.StoreNameSameException;
import com.grocery.quickbasket.exceptions.StoreNotFoundException;
import com.grocery.quickbasket.exceptions.UserIdNotFoundException;
import com.grocery.quickbasket.store.dto.StoreAdminDto;
import com.grocery.quickbasket.store.dto.StoreDto;
import com.grocery.quickbasket.store.dto.StoreResponseDto;
import com.grocery.quickbasket.store.repository.StoreAdminRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.grocery.quickbasket.store.entity.Store;
import com.grocery.quickbasket.store.entity.StoreAdmin;
import com.grocery.quickbasket.store.repository.StoreRepository;
import com.grocery.quickbasket.store.service.StoreService;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService{

    private final StoreRepository storeRepository;
    private final StoreAdminRepository storeAdminRepository;
    private final UserRepository userRepository;

    public StoreServiceImpl (StoreRepository storeRepository, StoreAdminRepository storeAdminRepository, UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.storeAdminRepository = storeAdminRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<StoreDto> getAllStores() {
        List<Store> storePage = storeRepository.getStoreByDeletedAtIsNull();

        return storePage.stream().map(StoreDto::fromEntity).toList();
    }

    @Override
    public StoreDto getStoreById(Long id) {
//        var claims = Claims.getClaimsFromJwt();
//        Long userId = (Long) claims.get("userId");
//        String scope = (String) claims.get("scope");
//
//        if (!storeAdminRepository.existsByUserId(userId) && !Objects.equals(scope, "super_admin")){
//            throw new UserIdNotFoundException("User Id is not authorize");
//        }

        Store currentStore = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));

        return StoreDto.fromEntity(currentStore);
    }

    @Override
    public StoreDto addStore(StoreDto dto) {
        boolean exist = storeRepository.existsByName(dto.getName());
        if (exist) {
            throw new StoreNameSameException("Store name already exist");
        }
        Store store = StoreDto.toEntity(dto);
        GeometryFactory gef = new GeometryFactory();
        Point storeLocation = gef.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));

        store.setLocation(storeLocation);
        storeRepository.save(store);

        return StoreDto.fromEntity(store);
    }

    @Override
    public StoreDto updateStore(StoreDto dto) {
        Optional<Store> storeOptional = storeRepository.findById(dto.getId());
        if (storeOptional.isEmpty()) {
            throw new StoreNotFoundException("Store not found");
        }
        Store currentStore = storeOptional.get();

        currentStore.updateFromDto(dto);

        storeRepository.save(currentStore);
        return StoreDto.fromEntity(currentStore);
    }

    @Transactional
    @Override
    public String deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Store not found"));

        store.softDelete();
        storeRepository.save(store);

        return "Delete successfully";
    }

    @Override
    public Store findNearestStore(double longitude, double latitude) {
        return storeRepository.findNearestStore(longitude, latitude);

    }

    @Override
    public double calculateDistance(Long storeId, Point userLocation) {
        return storeRepository.calculateDistanceInKm(storeId, userLocation);
    }

    @Override
    public List<StoreResponseDto> getAllStoreNotInStoreAdmins() {
        return storeRepository.findAllStoreNotInStoreAdmins();
    }

    @Override
    public List<StoreAdminDto> getAllStoreAdmins() {
        List<StoreAdmin> storeAdmins = storeAdminRepository.findByDeletedAtIsNull();
        return storeAdmins.stream()
            .map(StoreAdminDto::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteStoreAdmin(Long storeAdminId) {
        StoreAdmin storeAdmin = storeAdminRepository.findById(storeAdminId)
            .orElseThrow(() -> new DataNotFoundException("store admin not found for this id :: " + storeAdminId));
        storeAdmin.softDelete();

        User user = storeAdmin.getUser();
        if (user != null && !user.isDeleted()) {
            user.softDelete();
        }
        storeAdminRepository.save(storeAdmin);
        userRepository.save(user);
    }

}
