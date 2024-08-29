package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.InsertVariantDTO;
import com.datn.shop_app.DTO.QuantityVariantDTO;
import com.datn.shop_app.DTO.UpdateVariantDTO;
import com.datn.shop_app.DTO.VariantDTO;
import com.datn.shop_app.entity.Image;
import com.datn.shop_app.entity.Variant;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.variant.ListVariantResponse;
import com.datn.shop_app.response.variant.VariantResponse;
import com.datn.shop_app.service.VariantService;
import com.datn.shop_app.utils.FileUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("${api.prefix}/variants")
@RestController
@RequiredArgsConstructor
public class VariantController {
    private final VariantService variantService;
    public static final int MAXIMUM_IMAGES_PER_PRODUCT = 5;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getAllVariants(@RequestParam(defaultValue = "") String name,
                                                         @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        try {
            int totalPages = 0;
            PageRequest pageRequest = PageRequest.of(page, limit);
            Page<VariantResponse> variantPage = variantService.getVariants(name, active, pageRequest);
            totalPages = variantPage.getTotalPages();
            List<VariantResponse> variantResponses = variantPage.getContent();

            ListVariantResponse listVariantResponse = ListVariantResponse.builder()
                    .variants(variantResponses)
                    .totalPages(totalPages).build();

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get variant is successfully")
                    .status(HttpStatus.OK)
                    .data(listVariantResponse)
                    .build());
        } catch (
                Exception e) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get variant is not successful")
                    .status(HttpStatus.OK)
                    .build());
        }
    }

    @GetMapping("/for_promotion")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getAllVariants(@RequestParam(defaultValue = "") String name,
                                                         @RequestParam(defaultValue = "", name = "product_name") String productName,
                                                         @RequestParam(defaultValue = "", name = "promotion_id") Integer promotionId,
                                                         @RequestParam(defaultValue = "0", name = "min_quantity") Integer minQuantity,
                                                         @RequestParam(defaultValue = "0", name = "max_quantity") Integer maxQuantity,
                                                         @RequestParam(defaultValue = "0", name = "min_price") Float minPrice,
                                                         @RequestParam(defaultValue = "0", name = "max_price") Float maxPrice,
                                                         @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        try {
            int totalPages = 0;
            PageRequest pageRequest = PageRequest.of(page, limit);
            Page<VariantResponse> variantPage;
            if (active) {
                variantPage = variantService.getVariants(name, productName, promotionId, true, pageRequest);
            }else{
                variantPage = variantService.getVariants(name, productName, true, pageRequest);
            }

            List<VariantResponse> variantResponses = variantPage.getContent();
            totalPages = variantPage.getTotalPages();


            if (maxQuantity <= 0) {
                variantResponses = variantResponses.stream().filter(e -> e.getQuantity() >= minQuantity).toList();
            } else {
                variantResponses = variantResponses.stream().filter(e -> e.getQuantity() >= minQuantity && e.getQuantity() <= maxQuantity).toList();
            }

            if (maxPrice <= 0) {
                variantResponses = variantResponses.stream().filter(e -> e.getPrice() >= minPrice).toList();
            } else {
                variantResponses = variantResponses.stream().filter(e -> e.getPrice() >= minPrice && e.getPrice() <= maxPrice).toList();
            }

            ListVariantResponse listVariantResponse = ListVariantResponse.builder()
                    .variants(variantResponses)
                    .totalPages(totalPages).build();

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get variant is successfully")
                    .status(HttpStatus.OK)
                    .data(listVariantResponse)
                    .build());
        } catch (
                Exception e) {
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get variant is not successful")
                    .status(HttpStatus.OK)
                    .build());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
//                logger.info(imageName + " not found");
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
                //return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
//            logger.error("Error occurred while retrieving image: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> uploadImages(
            @PathVariable("id") Integer variantId,
            @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Variant variant = variantService.getVariantById(variantId);

            if (variant == null) {
                return ResponseEntity.notFound().build();
            }

            files = files == null ? new ArrayList<>() : files;

            int fileSize = files.size();
            if (fileSize > MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .message("You can only update maximum 5 images")
                                .status(HttpStatus.BAD_REQUEST).build());
            } else {
                int totalImages = 0;
                int totalImageOfVariant = variant.getImages().size();
                if (variant.getImages() != null) totalImages = totalImageOfVariant + fileSize;

                if (totalImages > MAXIMUM_IMAGES_PER_PRODUCT) {
                    int images = Math.max(fileSize - totalImageOfVariant, 0);
                    ResponseEntity.badRequest().body(
                            ResponseObject.builder()
                                    .message(String.format("You can only update maximum {0} images\nBecause Variant has {1} images", images, totalImageOfVariant))
                                    .status(HttpStatus.BAD_REQUEST).build());
                }
            }


            List<Image> images = new ArrayList<>();

            for (MultipartFile file : files) {
                if (file.getSize() == 0) continue;
                //kiểm tra kích thước file và định dạng
                if (file.getSize() > 10 * 1024 * 1024) {// > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                            ResponseObject.builder().message("File is too large! Maximum size is 10MB")
                                    .status(HttpStatus.BAD_REQUEST).build());
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(ResponseObject.builder().message("File must be an image")
                                    .status(HttpStatus.BAD_REQUEST).build());
                }
                // Lưu file và cập nhật thumbnail trong DTO
                String filename = FileUtils.storeFile(file);
                //lưu vào đối tượng product trong DB
                Image image = Image.builder()
                        .imageUrl(filename)
                        .variant(variant)
                        .build();
                images.add(image);
            }

            variantService.saveAllImage(images);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload images is successfully")
                    .status(HttpStatus.OK).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder().message("Upload images is failed")
                            .status(HttpStatus.BAD_REQUEST).build());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ResponseObject> createVariant(@Valid @RequestBody InsertVariantDTO variantDTO, BindingResult bindingResult) {
        try {
            List<String> errors = variantService.validateInsertion(variantDTO, bindingResult);
            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .message("Create Variant is not successful")
                                .data(errors)
                                .status(HttpStatus.BAD_REQUEST).build());
            } else {
                List<Variant> variants = variantService.save(variantDTO);
                return ResponseEntity.ok().body(ResponseObject.builder()
                        .message("Create variant is successfully")
                        .status(HttpStatus.OK)
                                .data(variants.stream().map(Variant::getId).toList())
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder().message("Create Variant is not successful")
                            .status(HttpStatus.BAD_REQUEST).build());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_UPDATE')")
    public ResponseEntity<ResponseObject> updateVariant(@PathVariable Integer id,
                                                        @Valid @RequestBody UpdateVariantDTO variantDTO,
                                                        BindingResult bindingResult) {
        try {
            List<String> errors = variantService.validateUpgrade(id, variantDTO, bindingResult);
            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .message("Update Variant is not successful")
                                .data(errors)
                                .status(HttpStatus.BAD_REQUEST).build());
            } else {
                Variant variant = variantService.update(id, variantDTO);
                return ResponseEntity.ok().body(ResponseObject.builder()
                        .message("Update variant is successfully")
                        .status(HttpStatus.OK)
                        .data(VariantResponse.fromVariant(variant))
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder().message("Update Variant is not successful")
                            .status(HttpStatus.BAD_REQUEST).build());
        }
    }

    @PostMapping("/update-quantity")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_IMPORT')")
    public ResponseEntity<ResponseObject> updateQuantity(@RequestBody List<QuantityVariantDTO> variantDTOs){

        try{
            List<String> errors = variantService.validateUpdateQuantity(variantDTOs);
            if(!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Update Quantity of variant is failed")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors)
                        .build());
            }
            int totalVariants = variantService.updateQuantity(variantDTOs);
            int oldSize = variantDTOs.size();
            String message = String.format("update quantity of variant is successfully %d out of %d", totalVariants, oldSize);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(message)
                    .status(HttpStatus.OK)
                    .build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Update Quantity of variant is failed")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_READ')")
    public ResponseEntity<ResponseObject> getVariant(@PathVariable Integer id) {
        try {
            Variant variant = variantService.getVariantById(id);

            if (variant == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Variant is not exist")
                        .status(HttpStatus.NOT_FOUND)
                        .data("").build());
            }

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get Variant is successful")
                    .data(VariantResponse.fromVariant(variant))
                    .status(HttpStatus.OK).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Get Variant is not successful").build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELETE')")
    public ResponseEntity<ResponseObject> deleteVariant(@PathVariable Integer id) {
        try {
            Variant variant = variantService.getVariantById(id);
            if (variant == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Variant is not exist")
                        .status(HttpStatus.NOT_FOUND)
                        .data("").build());
            }

            variantService.delete(id);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Delete variant is successful")
                    .status(HttpStatus.OK)
                    .data("").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Delete variant is not successful")
                    .status(HttpStatus.BAD_REQUEST)
                    .data("").build());
        }
    }

    @GetMapping("/by-product/{product_id}")
    public ResponseEntity<ResponseObject> getVariantsByProduct(@PathVariable(name = "product_id") Integer id) {
        List<VariantResponse> variantResponses = variantService.getVariants(id);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get variant is successful")
                .status(HttpStatus.OK)
                .data(variantResponses).build());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<ResponseObject> getVariantsByProduct(@RequestParam List<Integer> ids) {
        List<VariantResponse> variantResponses = variantService.getVariantByIds(ids);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get variant is successful")
                .status(HttpStatus.OK)
                .data(variantResponses).build());
    }
}
