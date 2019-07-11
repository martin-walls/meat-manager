package com.martinwalls.nea.data;

public class Order {
    private class ProductQuantity {
        private Product product;
        private double quantityMass;
        private int quantityBoxes;

        public ProductQuantity(Product product, double quantityMass) {
            this.product = product;
            this.quantityMass = quantityMass;
            this.quantityBoxes = -1;
        }

        public ProductQuantity(Product product, double quantityMass, int quantityBoxes) {
            this.product = product;
            this.quantityMass = quantityMass;
            this.quantityBoxes = quantityBoxes;
        }

        public Product getProduct() {
            return product;
        }

        public double getQuantityMass() {
            return quantityMass;
        }

        public int getQuantityBoxes() {
            return quantityBoxes;
        }
    }

    private int orderId;
    private int destId;
    private String destName;
    private String orderDate; //TODO make this a date object
    private boolean isCompleted;

    private List<ProductQuantity> productList = new ArrayList<>();

    public Order() {
    }

    public Order(int orderId, int destId, String destName, String orderDate, boolean isCompleted) {
        this.orderId = orderId;
        this.destId = destId;
        this.destName = destName;
        this.orderDate = orderDate;
        this.isCompleted = isCompleted;
    }

    //TODO getters and setters
}