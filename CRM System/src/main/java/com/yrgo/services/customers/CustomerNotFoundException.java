package com.yrgo.services.customers;

public class CustomerNotFoundException extends RuntimeException {  // OBS! RuntimeException istället för Exception
	public CustomerNotFoundException(String message) {
		super(message);
	}
}
