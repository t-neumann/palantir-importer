package at.ac.imp.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Entity implementation class for Entity: Datapoint
 *
 */
@Entity
public abstract class Datapoint implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private Gene gene;

	public Datapoint() {
		super();
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}
   
}
