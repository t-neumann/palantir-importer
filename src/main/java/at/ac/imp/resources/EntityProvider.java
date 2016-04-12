package at.ac.imp.resources;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import at.ac.imp.palantir.exceptions.DatabaseException;
import at.ac.imp.palantir.model.Alignment;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.palantir.model.Sample;

public class EntityProvider {
	
	private EntityManager em;
	
	public EntityProvider() {
		em = PersistenceProvider.INSTANCE.getEntityManager();
	}
	
	public void sessionStart()  {
		em.getTransaction().begin();
	}
	
	public void sessionEnd()  {
		em.getTransaction().commit();
	}
	
	public void persist(Object entity) {
		em.persist(entity);
	}
	
	public void merge(Object entity) {
		em.merge(entity);
	}
	
	public Reference getReferenceByName(String name) throws DatabaseException {
		TypedQuery<Reference> query = em.createNamedQuery("Reference.findByName", Reference.class);
		Reference result = null;
		
		try {
			result = query.setParameter("name", name).getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			throw new DatabaseException(e.getMessage(),e.getCause());
		}
		return result;
	}
	
	public Sample getSampleById(int id) {
		Sample sample = null;
		
		sample = em.find(Sample.class, id);
		
		return sample;
	}
	
	public Alignment getAlignmentFromSample(Sample sample, String alignmentName) {
		Alignment result = null;
		
		for (Alignment alignment : sample.getAlignments()) {
			if (alignment.getName().equals(alignmentName)) {
				result = alignment;
			}
		}
		
		return result;
	}

}
