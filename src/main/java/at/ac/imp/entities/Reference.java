package at.ac.imp.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * Entity implementation class for Entity: Reference
 *
 */
@Entity

public class Reference implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Collection<Gene> genes = new ArrayList<Gene>();
	
	public Collection<Gene> getGenes() {
		return genes;
	}

	public void setGenes(Collection<Gene> genes) {
		this.genes = genes;
	}
	
	public void addGene(final Gene gene) {
		genes.add(gene);
	}

	public Reference() {
		super();
	}
   
}
