package at.ac.imp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceProvider {
  INSTANCE;
  private EntityManagerFactory emFactory;
  private PersistenceProvider() {
    emFactory = Persistence.createEntityManagerFactory("Palantir");
  }
  public EntityManager getEntityManager() {
    return emFactory.createEntityManager();
  }
  public void close() {
    emFactory.close();
  }
}
