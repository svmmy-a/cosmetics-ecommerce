package com.cosmetics.dto;

public class ProductDto {
    private Integer productId;
    private String name;
    private String description;
    private Double price;
    private String size;
    private String imageUrl;
    private Boolean isNew;
    private Integer stockQuantity;
    private CategoryDto category;

    public ProductDto() {}

    public ProductDto(Integer productId,
                      String name,
                      String description,
                      Double price,
                      String size,
                      String imageUrl,
                      Boolean isNew,
                      Integer stockQuantity,
                      CategoryDto category) {
        this.productId  = productId;
        this.name       = name;
        this.description= description;
        this.price      = price;
        this.size       = size;
        this.imageUrl   = imageUrl;
        this.isNew      = isNew;
        this.stockQuantity = stockQuantity;
        this.category   = category;
    }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsNew() { return isNew; }
    public void setIsNew(Boolean isNew) { this.isNew = isNew; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public CategoryDto getCategory() { return category; }
    public void setCategory(CategoryDto category) { this.category = category; }
}
