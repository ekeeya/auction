
package com.skycastle.auction.controllers;

import com.skycastle.auction.entities.forms.requests.ModelRequestDTO;
import com.skycastle.auction.entities.forms.responses.BaseResponseDTO;
import com.skycastle.auction.entities.forms.responses.ListResponseDTO;
import com.skycastle.auction.entities.forms.responses.VehicleModelResponseDTO;
import com.skycastle.auction.entities.products.vehicles.VehicleModel;
import com.skycastle.auction.repositories.vehicles.VehicleModelRepository;
import com.skycastle.auction.services.vehicles.VehicleModelService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Produces;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON_VALUE)
public class VehicleModelController {
    private final VehicleModelService modelService;
    private final VehicleModelRepository  vehicleModelRepository;

    @GetMapping("/vehicle-models")
    public ResponseEntity<ListResponseDTO<VehicleModelResponseDTO>> getModels(
            @RequestParam(value = "make", required = false) String make
    ) {

        List<VehicleModelResponseDTO> models;
        if(make != null){
            models = modelService.filterByMake(make).stream().map(VehicleModelResponseDTO::new).collect(Collectors.toList());
        }else{
            models = vehicleModelRepository.findAll().stream().map(VehicleModelResponseDTO::new).collect(Collectors.toList());
        }
        ListResponseDTO<VehicleModelResponseDTO> response = new ListResponseDTO<>(models);
        response.setTotalCount(models.size());
        response.setTotalPages(1);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vehicle-models")
    @Secured({"ROLE_ADMIN", "ROLE_SELLER"})
    public ResponseEntity<BaseResponseDTO> createModel(
            @RequestBody @Valid ModelRequestDTO request, BindingResult bindingResult
    ) {
        BaseResponseDTO response;
        try {
            response = new BaseResponseDTO(bindingResult);
            if (response.isSuccess()) {
                VehicleModel model = new VehicleModel();
                model.setModel(request.getModel());
                model.setMake(request.getMake());
                model.setYear(request.getYear());
                VehicleModel newModel = modelService.create(model);
                response.setMessage("Vehicle model %s added".formatted(newModel.getModel().toUpperCase()));
                response.setStatusCode(HttpStatus.OK.value());
                response.setData(newModel);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response = new BaseResponseDTO(e);
            return ResponseEntity.internalServerError().body(response);
        }

    }
}
