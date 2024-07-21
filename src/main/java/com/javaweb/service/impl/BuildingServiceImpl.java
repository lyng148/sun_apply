package com.javaweb.service.impl;

import com.javaweb.builder.BuildingSearchBuilder;
import com.javaweb.converter.BuildingConverter;
import com.javaweb.converter.BuildingSearchBuilderConverter;
import com.javaweb.model.dto.BuildingDTO;
import com.javaweb.model.request.BuildingSearchRequest;
import com.javaweb.model.response.BuildingSearchResponse;
import com.javaweb.repository.BuildingRepository;
import com.javaweb.repository.RentAreaRepository;
import com.javaweb.repository.entity.BuildingEntity;
import com.javaweb.repository.entity.RentAreaEntity;
import com.javaweb.service.BuildingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    private BuildingSearchBuilderConverter buildingSearchBuilderConverter;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private BuildingConverter buildingConverter;

    @Autowired
    private RentAreaRepository rentAreaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ArrayList<BuildingSearchResponse> findAll(BuildingSearchRequest request) {
        BuildingSearchBuilder builder = buildingSearchBuilderConverter.toBuildingSearchBuilder(request);
        ArrayList<BuildingEntity> buildingEntities = buildingRepository.find(builder);
        ArrayList<BuildingSearchResponse> buildingSearchResponses = buildingConverter.convert(buildingEntities);
        return buildingSearchResponses;
    }

    @Override
    public void createAndUpdateBuilding(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = modelMapper.map(buildingDTO, BuildingEntity.class);
        String type = "";
        if (buildingDTO.getType() != null) {
            type = buildingDTO.getType().stream().collect(Collectors.joining(","));
        }
        buildingEntity.setType(type == null ? "" : type);
        // Vẫn chưa update rent area, hang muc để sau
        buildingRepository.save(buildingEntity);
    }

    @Override
    public void deleteBuildings(ArrayList<Long> ids) {
        rentAreaRepository.deleteAllByBuildingIdIn(ids);
        buildingRepository.deleteByIdIn(ids);
    }

    @Override
    public BuildingDTO findById(Long id) {
        BuildingEntity bd = buildingRepository.findById(id).get();
        BuildingDTO dto = modelMapper.map(bd, BuildingDTO.class);
        String rentArea = "";
        ArrayList<RentAreaEntity> rentAreas = rentAreaRepository.findAllByBuildingId(id);
        if (rentAreas != null && !rentAreas.isEmpty()) {
            rentArea = rentAreas.stream().map(it -> it.getValue().toString()).collect(Collectors.joining(","));
        }
        dto.setRentArea(rentArea == "" ? null : rentArea);
        return dto;
    }
}
