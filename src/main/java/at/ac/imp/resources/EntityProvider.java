package at.ac.imp.resources;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import at.ac.imp.palantir.exceptions.DatabaseException;
import at.ac.imp.palantir.model.Alignment;
import at.ac.imp.palantir.model.Essentialome;
import at.ac.imp.palantir.model.ExternalRNASeqResource;
import at.ac.imp.palantir.model.GenericGene;
import at.ac.imp.palantir.model.Reference;
import at.ac.imp.palantir.model.Result;
import at.ac.imp.palantir.model.Sample;
import at.ac.imp.palantir.model.ScreenGene;

public class EntityProvider {

	private EntityManager em;
	private EntityTransaction t;

	public EntityProvider() {
		em = PersistenceProvider.INSTANCE.getEntityManager();
		t = em.getTransaction();
	}

	public void sessionStart() {
		if (t.isActive()) {
			t.commit();
		}
		t.begin();
		
		//em.getTransaction().begin();
	}

	public void sessionEnd() {
		if (t.isActive()) {
			t.commit();
		}
		//em.getTransaction().commit();
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
	
	public void refresh(Object entity) {
		em.refresh(entity);
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
	
	public Essentialome findEssentialomeByName(String name) {
		
		TypedQuery<Essentialome> query = em.createNamedQuery("Essentialome.findByName", Essentialome.class);
		
		Essentialome essentialome = null;

		List<Essentialome> results = query.setParameter("name", name).getResultList();
		
		if (results.size() == 1) {
			essentialome = results.get(0);
		}		
		
		return essentialome;
	}
	
	public ExternalRNASeqResource findExternalRNASeqResourceByName(String name) {
		
		TypedQuery<ExternalRNASeqResource> query = em.createNamedQuery("ExternalRNASeqResource.findByName", ExternalRNASeqResource.class);
		
		ExternalRNASeqResource resource = null;

		List<ExternalRNASeqResource> results = query.setParameter("name", name).getResultList();
		
		if (results.size() == 1) {
			resource = results.get(0);
		}		
		
		return resource;
	}
	
	public void delete(Object object) {
		em.remove(object);
	}

	public List<Reference> getAllReferences() {
			
		List<Reference> references = null;
		TypedQuery<Reference> query = em.createQuery("SELECT r FROM Reference r", Reference.class);
		references = query.getResultList();
		
		return references;
	}
	
	public List<GenericGene> getAllGenericGenes() {
		TypedQuery<GenericGene> query = em.createQuery("SELECT g FROM GenericGene g", GenericGene.class);
		
		return query.getResultList();
	}
	
	public List<ScreenGene> getAllScreenGenes() {
		TypedQuery<ScreenGene> query = em.createQuery("SELECT g FROM ScreenGene g", ScreenGene.class);
		
		return query.getResultList();
	}
}
