package org.ecommerce.backend.exception;

public enum ErrorMessage {
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    INVALID_CREDENTIALS("Invalid credentials"),
    PRODUCT_NOT_FOUND("Product not found"),
    ORDER_NOT_FOUND("Order not found"),
    INSUFFICIENT_STOCK("Insufficient stock for the product"),
    PAYMENT_FAILED("Payment processing failed"),
    UNAUTHORIZED_ACCESS("Unauthorized access"),
    INVALID_REQUEST("Invalid request data"),
    CART_EMPTY("Shopping cart is empty");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
