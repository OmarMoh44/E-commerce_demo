package org.ecommerce.backend.exception;

public enum ErrorMessage {
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    INVALID_CREDENTIALS("Invalid credentials"),
    CATEGORY_NOT_FOUND("Category not found"),
    CATEGORY_ALREADY_EXISTS("Category already exists"),
    PRODUCT_NOT_FOUND("Product not found"),
    PRODUCT_ALREADY_EXISTS("Product already exists"),
    UNAUTHORIZED_ACCESS("Unauthorized access"),
    INVALID_REQUEST("Invalid request data"),
    CART_EMPTY("Shopping cart is empty"),
    CART_NOT_FOUND("Shopping cart not found"),
    CART_ALREADY_EXISTS("Shopping cart already exists"),
    CART_ITEM_ALREADY_EXISTS("Cart item already exists in the cart"),
    CART_ITEM_NOT_EXISTS("Cart item does not exist in the cart"),
    ADMIN_ACCESS_DENIED("Cannot access admin user information"),
    ADMIN_DELETE_DENIED("Cannot delete admin user"),
    USER_ALREADY_DELETED("User is already deleted"),
    INVALID_PAGE_PARAMETERS("Invalid pagination parameters");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
