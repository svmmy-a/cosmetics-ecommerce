package com.cosmetics.dto;

public class CartItemDto {
    private Integer productId;
    private String name;
    private String description;
    private double price;
    private String image;
    private int quantity;

    // default constructor
    public CartItemDto() {
    }

    // constructor w/ params
    public CartItemDto(Integer productId, String name, String description, double price, String image, int quantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    // constructor w/o productId for backward compatibility
    public CartItemDto(String name, String description, double price, String image, int quantity) {
        this(null, name, description, price, image, quantity);
    }

    // getters and setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
