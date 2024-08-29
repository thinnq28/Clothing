package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.CommodityDTO;
import com.datn.shop_app.DTO.SupplierDTO;
import com.datn.shop_app.entity.Commodity;
import com.datn.shop_app.entity.Supplier;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.commodity.CommodityResponse;
import com.datn.shop_app.response.commodity.ListCommodityResponse;
import com.datn.shop_app.response.supplier.ListSupplierResponse;
import com.datn.shop_app.response.supplier.SupplierResponse;
import com.datn.shop_app.service.CommodityService;
import com.datn.shop_app.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/commodities")
@RequiredArgsConstructor
public class CommodityController {
    private final CommodityService commodityService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody CommodityDTO commodityDTO,
                                                 BindingResult result) {

        if(result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for(FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Create commodity is not successful")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errors).build());
        }

        Commodity commodity = commodityService.save(commodityDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create commodity is successful")
                .status(HttpStatus.OK)
                .data(commodity).build());
    }

    @GetMapping("/by-name")
    public ResponseEntity<ResponseObject> getSuppliers(@RequestParam(defaultValue = "", name = "supplier_name") String name){
        try{
            List<CommodityResponse> commodityResponses = commodityService.getAllCommodities(name, true);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get commodity successfully")
                    .status(HttpStatus.OK)
                    .data(commodityResponses)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Get commodity is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getSuppliers(@RequestParam(defaultValue = "") String name,
                                                       @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<CommodityResponse> commodityPage = commodityService.getAllCommodities(name, active, pageRequest);
        totalPages = commodityPage.getTotalPages();
        List<CommodityResponse> commodityResponses = commodityPage.getContent();

        ListCommodityResponse listCommodityResponse = ListCommodityResponse.builder()
                .commodities(commodityResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get commodity successfully")
                .status(HttpStatus.OK)
                .data(listCommodityResponse)
                .build());
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getSupplierDetail(@PathVariable Integer id) {
        Commodity commodity = commodityService.getCommodity(id);

        if(commodity == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Commodity is not exist")
                    .status(HttpStatus.NOT_FOUND)
                    .data("").build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get supplier is successful")
                .status(HttpStatus.OK)
                .data(commodity).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_UPDATE')")
    public ResponseEntity<ResponseObject> updateSupplier(@PathVariable Integer id,
                                                         @Valid @RequestBody CommodityDTO commodityDTO,
                                                         BindingResult result){

        Commodity commodity = commodityService.getCommodity(id);

        if(commodity == null) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Commodity is not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        if(result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for(FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Create commodity is not successful")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errors).build());
        }

        commodity = commodityService.update(id, commodityDTO);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update commodity is successful")
                        .status(HttpStatus.OK)
                        .data(commodity).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELETE')")
    public ResponseEntity<ResponseObject> deleteSupplier(@PathVariable Integer id) {
        Commodity commodity = commodityService.getCommodity(id);
        if(commodity == null) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Commodity is not exist")
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }

        commodityService.delete(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Delete commodity is successful")
                .status(HttpStatus.OK)
                .build());
    }
}
