package com.grocery.quickbasket.exceptions;

public class PendingOrderExcerption extends RuntimeException{
    public PendingOrderExcerption() {
        super("Please complete the ongoing order");
    }
}
