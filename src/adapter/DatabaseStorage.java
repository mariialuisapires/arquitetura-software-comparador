package adapter;

import domain.EntityInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseStorage<T extends EntityInterface> implements PersistInterface {
    private static final String PERSISTENCE_UNIT = "default";

    private final EntityManagerFactory emf;
    private final Class<T> entityClass;

    public DatabaseStorage(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    @Override
    public void save(EntityInterface entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (entity.getUUID() != null && em.find(entity.getClass(), entity.getUUID()) != null) {
                em.merge(entity);
            } else {
                em.persist(entity);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(EntityInterface entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            EntityInterface managed = em.find(entity.getClass(), entity.getUUID());
            if (managed != null) em.remove(managed);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public ArrayList<EntityInterface> listAll() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            List<T> result = em.createQuery(jpql, entityClass).getResultList();
            return new ArrayList<>(result);
        } finally {
            em.close();
        }
    }

    @Override
    public EntityInterface findOneById(UUID id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public void close() {
        if (emf != null && emf.isOpen()) emf.close();
    }
}
