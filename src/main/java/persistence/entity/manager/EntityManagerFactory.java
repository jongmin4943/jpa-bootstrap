package persistence.entity.manager;

public interface EntityManagerFactory {
    EntityManager openSession();
}
