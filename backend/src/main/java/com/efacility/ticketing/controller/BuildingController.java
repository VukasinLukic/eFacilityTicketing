package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.BuildingDTO;
import com.efacility.ticketing.dto.request.CreateBuildingRequest;
import com.efacility.ticketing.dto.request.UpdateBuildingRequest;
import com.efacility.ticketing.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/buildings")
@CrossOrigin("http://localhost:3000")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll() {
        List<BuildingDTO> buildings = buildingService.getAll();
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Buildings fetched", Map.of("buildings", buildings), HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getBuilding(@PathVariable Long id) {
        BuildingDTO building = buildingService.getBuilding(id);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Building fetched", Map.of("building", building), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@Valid @RequestBody CreateBuildingRequest request) {
        BuildingDTO building = buildingService.addBuilding(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Building added successfully", Map.of("building", building), HttpStatus.OK)
        );
    }

    @PostMapping("/update")
    public ResponseEntity<Response> update(@Valid @RequestBody UpdateBuildingRequest request) {
        BuildingDTO building = buildingService.updateBuilding(request);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Building updated successfully", Map.of("building", building), HttpStatus.OK)
        );
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        String result = buildingService.deleteBuilding(id);
        return ResponseEntity.ok(
                HttpResponse.getResponse(result, HttpStatus.OK)
        );
    }
}
