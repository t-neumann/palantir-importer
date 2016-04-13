package at.ac.imp.resources;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceProvider {
	INSTANCE;
	private EntityManagerFactory emFactory;
	
	public void configureProvider(Properties properties) {
		if (emFactory != null) {
			emFactory.close();
		}
		emFactory = Persistence.createEntityManagerFactory("Palantir",properties);
	}

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
