package at.ac.imp.resources;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import at.ac.imp.palantir.exceptions.DatabaseException;
import at.ac.imp.palantir.model.Alignment;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.palantir.model.Result;
import at.ac.imp.palantir.model.Sample;

public class EntityProvider {

	private EntityManager em;

	public EntityProvider() {
		em = PersistenceProvider.INSTANCE.getEntityManager();
	}

	public void sessionStart() {
		em.getTransaction().begin();
	}

	public void sessionEnd() {
		em.getTransaction().commit();
	}
	
	public void sessionClear() {
		em.flush();
		em.clear();
	}

	public void persist(Object entity) {
		em.persist(entity);
	}

	public void merge(Object entity) {
		em.merge(entity);
	}

	public Reference getReferenceByName(String name) {
		TypedQuery<Reference> query = em.createNamedQuery("Reference.findByName", Reference.class);
		Reference result = null;

		try {
			result = query.setParameter("name", name).getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
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

	public Result findResultByAlignmentIdAndReference(int alignmentId, String referenceName) {
		TypedQuery<Result> query = em.createNamedQuery("Result.findByAlignmentId", Result.class);
		
		Result result = null;

		List<Result> results = query.setParameter("id", alignmentId).getResultList();
		for (Result res : results) {
			if (res.getReference().getName().equals(referenceName)) {
				result = res;
			}
		}

		return result;
	}

	public List<Reference> getAllReferences() {
			
		List<Reference> references = null;
		TypedQuery<Reference> query = em.createQuery("SELECT r FROM Reference r", Reference.class);
		references = query.getResultList();
		
		return references;
	}
}
