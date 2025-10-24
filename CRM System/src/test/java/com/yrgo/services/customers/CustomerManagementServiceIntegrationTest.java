package com.yrgo.services.customers;

import com.yrgo.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstester för CustomerManagementService med JPA.
 * Läser Spring-kontext från application-test.xml (in-memory test-datasource).
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:application-test.xml")
@Transactional
class CustomerManagementServiceIntegrationTest {

    @Autowired
    private CustomerManagementService customerService;

    private static final String EXISTING_ID = "EXISTING_1";

    @BeforeEach
    void setUp() {
        // Skapa en kund som ska finnas inför "find existing"-testet.
        // Du har två konstruktörer – här använder vi den fulla:
        Customer existing = new Customer(
                EXISTING_ID,
                "Testbolaget AB",
                "test@example.com",
                "031-000000",
                "Init-skapad i @BeforeEach"
        );
        customerService.newCustomer(existing);
    }

    @Test
    void testCreateNewCustomer() {
        // Arrange
        String id = "C123";
        Customer c = new Customer(
                id,
                "Anna Consulting AB",
                "anna@consulting.example",
                "070-111111",
                "Ny kund via integrationstest"
        );

        // Act
        customerService.newCustomer(c);

        // Assert
        Customer fetched = customerService.findCustomerById(id);
        assertNotNull(fetched, "Ny kund ska kunna hämtas efter persist");
        assertEquals("Anna Consulting AB", fetched.getCompanyName(), "companyName ska sparas korrekt");
        assertEquals("anna@consulting.example", fetched.getEmail(), "email ska sparas korrekt");
        assertEquals("070-111111", fetched.getTelephone(), "telephone ska sparas korrekt");
        assertEquals("Ny kund via integrationstest", fetched.getNotes(), "notes ska sparas korrekt");
    }

    @Test
    void testFindExistingCustomer() {
        // Act
        Customer found = customerService.findCustomerById(EXISTING_ID);

        // Assert
        assertNotNull(found, "Setup-kunden ska hittas");
        assertEquals("Testbolaget AB", found.getCompanyName(), "companyName för setup-kunden ska stämma");
        assertEquals("test@example.com", found.getEmail(), "email för setup-kunden ska stämma");
    }
}
