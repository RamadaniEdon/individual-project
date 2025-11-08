package com.database.federation.utils;

public class CategoryJson {
    private String categoryName;
    private double categoryPrice;
    private String categoryAccessControl;

    public CategoryJson(String categoryName, double categoryPrice, String categoryAccessControl) {
        this.categoryName = categoryName;
        this.categoryPrice = categoryPrice;
        this.categoryAccessControl = categoryAccessControl;
    }

    public CategoryJson() {}


    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public double getCategoryPrice() {
        return categoryPrice;
    }
    public void setCategoryPrice(double categoryPrice) {
        this.categoryPrice = categoryPrice;
    }
    public String getCategoryAccessControl() {
        return categoryAccessControl;
    }
    public void setCategoryAccessControl(String categoryAccessControl) {
        this.categoryAccessControl = categoryAccessControl;
    }
    
}
