package at.ac.imp;

import javax.persistence.EntityManager;

import at.ac.imp.entities.Datapoint;
import at.ac.imp.entities.ExpressionValue;
import at.ac.imp.entities.FoldChange;
import at.ac.imp.entities.Gene;
import at.ac.imp.entities.Reference;
import at.ac.imp.entities.ScreenFoldChange;

public class Main {
  public static void main(String[] args) {
	Gene gene1 = new Gene("chr1", 0, 2, "Myc", 234234);
	Gene gene2 = new Gene("chr2", 100, 2000, "Myc", 57285);
	
	Datapoint point1 = new ExpressionValue(10, 0.1, 0.2);
	Datapoint point2 = new FoldChange(-1.2, 0.04);
	Datapoint point3 = new ScreenFoldChange(-2, 0.01, "serious", "shit");
	
	point1.setGene(gene1);
	point2.setGene(gene1);
	point3.setGene(gene2);
	
	Reference ref = new Reference();
	
	ref.addGene(gene1);
	ref.addGene(gene2);
	
    EntityManager em = PersistenceProvider.INSTANCE.getEntityManager();
    
    em.getTransaction().begin();
    em.persist(ref);
    em.persist(point1);
    em.persist(point2);
    em.persist(point3);
    em.getTransaction().commit();
    em.close();
    PersistenceProvider.INSTANCE.close();
  }
}