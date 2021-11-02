package com.example.android.stockkeepingassistant.model;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    /* Class variables */
    private UUID id;
    private String title;
    private int quantity;
    private BigDecimal price;
    private String supplierName;
    private String supplierEmail;

    public Product() {
        this(UUID.randomUUID());
    }

    public Product(UUID id) {
       this.id = id;
       quantity = 1;


       BigDecimal init = new BigDecimal(0);
       price = init.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public UUID getId() {
        return id;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public BigDecimal getPrice() {

        BigDecimal formattedPrice = price.setScale(2, BigDecimal.ROUND_HALF_UP);
        return formattedPrice;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Nullable
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }
}
