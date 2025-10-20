package com.yrgo.dataaccess;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;

@Repository
@Transactional
public class CustomerDaoJpaImpl implements CustomerDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(Customer customer) {
        em.persist(customer);
    }

    @Override
    public Customer getById(String customerId) throws RecordNotFoundException {
        Customer c = em.find(Customer.class, customerId);
        if (c == null) {
            throw new RecordNotFoundException();
        }
        return c;
    }

    @Override
    public List<Customer> getByName(String name) {
        final String jpql = "SELECT c FROM Customer c WHERE c.companyName = :name";
        return em.createQuery(jpql, Customer.class)
                .setParameter("name", name)
                .getResultList(); // tom lista om inget hittas
    }

    @Override
    public void update(Customer customerToUpdate) throws RecordNotFoundException {
        // Säkerställ att den finns
        Customer existing = em.find(Customer.class, customerToUpdate.getCustomerId());
        if (existing == null) {
            throw new RecordNotFoundException();
        }
        // Kopiera fält till managed entity (alternativt: em.merge(customerToUpdate))
        existing.setCompanyName(customerToUpdate.getCompanyName());
        existing.setEmail(customerToUpdate.getEmail());
        existing.setTelephone(customerToUpdate.getTelephone());
        existing.setNotes(customerToUpdate.getNotes());
    }

    @Override
    public void delete(Customer oldCustomer) throws RecordNotFoundException {
        Customer managed = em.find(Customer.class, oldCustomer.getCustomerId());
        if (managed == null) {
            throw new RecordNotFoundException();
        }
        em.remove(managed);
    }

    @Override
    public List<Customer> getAllCustomers() {
        final String jpql = "SELECT c FROM Customer c";
        return em.createQuery(jpql, Customer.class).getResultList();
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws RecordNotFoundException {
        final String jpql =
                "SELECT c FROM Customer c LEFT JOIN FETCH c.calls WHERE c.customerId = :id";
        try {
            return em.createQuery(jpql, Customer.class)
                    .setParameter("id", customerId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new RecordNotFoundException();
        }
    }

    @Override
    public void addCall(Call newCall, String customerId) throws RecordNotFoundException {
        // OBS: signatur enligt interfacet = (Call, String)
        Customer customer = em.find(Customer.class, customerId);
        if (customer == null) {
            throw new RecordNotFoundException();
        }
        customer.addCall(newCall); // JPA registrerar ändringen och persisterar vid commit
    }
}
