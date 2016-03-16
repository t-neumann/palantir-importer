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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: Reference
 *
 */
@Entity
@NamedQuery(name="Reference.findByName",query="SELECT r FROM Reference r WHERE r.name = :name")
public class Reference implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Collection<Gene> genes = new ArrayList<Gene>();
	
	private String name;
	
	private String build;
	
	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
	
	public Reference(String name, String build) {
		super();
		this.name = name;
		this.build = build;
	}

	@Override
	public String toString() {
		return "Reference [name=" + name + ", build=" + build + "]";
	}
   
}
