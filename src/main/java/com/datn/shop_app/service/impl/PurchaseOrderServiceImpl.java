package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.PurchaseOrderDTO;
import com.datn.shop_app.entity.*;
import com.datn.shop_app.model.PurchaseOrderModel;
import com.datn.shop_app.repository.PurchaseOrderDetailRepository;
import com.datn.shop_app.repository.PurchaseOrderRepository;
import com.datn.shop_app.repository.SupplierRepository;
import com.datn.shop_app.repository.VariantRepository;
import com.datn.shop_app.response.purchase_order.PurchaseOrderResponse;
import com.datn.shop_app.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final SupplierRepository supplierRepository;
    private final VariantRepository variantRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @Override
    public List<PurchaseOrderModel> readExcelFileToProducts(MultipartFile file) throws IOException {
        List<PurchaseOrderModel> purchaseOrders = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0); // Assuming you want to read the first sheet

        Iterator<Row> rowIterator = sheet.iterator();

        // Skip header row if necessary
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // Check if the row is not empty
            if (isRowEmpty(row)) {
                continue; // Skip empty rows
            }
            PurchaseOrderModel purchaseOrder = new PurchaseOrderModel();

            // Handle potential number format exceptions and null values
            try {
                double ordinalNumber = Double.parseDouble(getCellValue(row.getCell(0)));
                int intOrdinalNumber = (int) ordinalNumber;
                purchaseOrder.setOrdinalNumber(intOrdinalNumber);
            } catch (NumberFormatException e) {
                // Log or handle the error
                continue; // Skip this row if there's a number format exception
            }

            purchaseOrder.setSkuId(getCellValue(row.getCell(1)));

            try {
                double quantity = Double.parseDouble(getCellValue(row.getCell(2)));
                int intQuantity = (int) Math.round(quantity);
                purchaseOrder.setQuantity(intQuantity);
            } catch (NumberFormatException e) {
                // Log or handle the error
                continue; // Skip this row if there's a number format exception
            }

            try {
                purchaseOrder.setUnitPrice(Double.parseDouble(getCellValue(row.getCell(3))));
                purchaseOrder.setTotalAmount(Double.parseDouble(getCellValue(row.getCell(4))));
            } catch (NumberFormatException e) {
                // Log or handle the error
                continue; // Skip this row if there's a number format exception
            }

            purchaseOrders.add(purchaseOrder);
        }

        workbook.close();
        inputStream.close();

        return purchaseOrders;
    }

    @Override
    public PurchaseOrder save(PurchaseOrderDTO purchaseOrderDTO) {
        Optional<Supplier> supplier = supplierRepository.findByIdAndActive(purchaseOrderDTO.getSupplierId(), true);
        if (supplier.isPresent()) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setSupplier(supplier.get());
            purchaseOrder.setOrderDate(LocalDate.now());
            purchaseOrder.setTotalAmount(purchaseOrderDTO.getTotalAmount());
            purchaseOrder.setActive(true);
            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

            List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
            for (PurchaseOrderModel purchaseOrderModel : purchaseOrderDTO.getPurchaseOrderModels()) {
                Variant variant = variantRepository.findBySkuIdAndActive(purchaseOrderModel.getSkuId(), true);
                if (variant != null) {
                    PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
                    purchaseOrderDetail.setPurchaseOrder(purchaseOrder);
                    purchaseOrderDetail.setVariant(variant);
                    purchaseOrderDetail.setQuantity(purchaseOrderModel.getQuantity());
                    purchaseOrderDetail.setUnitPrice(purchaseOrderModel.getUnitPrice());
                    purchaseOrderDetail.setTotalPrice(purchaseOrderModel.getUnitPrice() * purchaseOrderModel.getQuantity());
                    purchaseOrderDetails.add(purchaseOrderDetail);
                }
            }

            purchaseOrderDetailRepository.saveAll(purchaseOrderDetails);
            return purchaseOrder;
        }
        return null;
    }

    @Override
    public List<String> validateInsertion(PurchaseOrderDTO purchaseOrderDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if(bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return errors;
        }

        Optional<Supplier> supplier = supplierRepository.findByIdAndActive(purchaseOrderDTO.getSupplierId(), true);
        if(supplier.isEmpty()) {
            errors.add("Supplier is not found");
        }

        if(purchaseOrderDTO.getPurchaseOrderModels() == null || purchaseOrderDTO.getPurchaseOrderModels().isEmpty()) {
            errors.add("Variant cannot be null or empty");
        }

        return errors;
    }

    @Override
    public Page<PurchaseOrderResponse> getPurchaseOrders(String supplierName, LocalDate orderDate, Pageable pageable){
        Page<PurchaseOrder> orders = purchaseOrderRepository.getPurchaseOrders(supplierName, orderDate, pageable);
        return orders.map(PurchaseOrderResponse::fromPurchaseOrder);
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false; // Row is not empty
            }
        }
        return true; // Row is empty
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluate the formula and return the value based on the result type
                CellValue cellValue = evaluator.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case STRING:
                        return cellValue.getStringValue();
                    case NUMERIC:
                        return String.valueOf(cellValue.getNumberValue());
                    case BOOLEAN:
                        return String.valueOf(cellValue.getBooleanValue());
                    default:
                        return "";
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
