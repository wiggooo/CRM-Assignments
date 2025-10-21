package com.yrgo.dataaccess;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yrgo.domain.Action;

@Repository
@Transactional
@Primary
public class ActionDaoJpaImpl implements ActionDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(Action newAction) {
        em.persist(newAction);
    }

    @Override
    public List<Action> getIncompleteActions(String userId) {
        final String jpql = "SELECT a FROM Action a " +
                "WHERE a.owningUser = :owningUser " +
                "AND a.complete = :isComplete " +
                "ORDER BY a.requiredBy";
        TypedQuery<Action> q = em.createQuery(jpql, Action.class)
                .setParameter("owningUser", userId)
                .setParameter("isComplete", false);
        return q.getResultList();
    }

    @Override
    public void update(Action actionToUpdate) throws RecordNotFoundException {
        // Primärnyckelstypen i Action är int
        Action existing = em.find(Action.class, actionToUpdate.getActionId());
        if (existing == null) {
            throw new RecordNotFoundException();
        }

        // Kopiera över fält till managed entity
        existing.setDetails(actionToUpdate.getDetails());
        existing.setRequiredBy(actionToUpdate.getRequiredBy());
        existing.setOwningUser(actionToUpdate.getOwningUser());
        existing.setComplete(actionToUpdate.isComplete());
        // Eftersom 'existing' är managed sparas ändringarna vid commit
    }

    @Override
    public void delete(Action oldAction) throws RecordNotFoundException {
        Action managed = em.find(Action.class, oldAction.getActionId());
        if (managed == null) {
            throw new RecordNotFoundException();
        }
        em.remove(managed);
    }
}
