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
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: Sample
 *
 */
@Entity

public class Sample implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private int id;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Collection<Alignment> alignments = new ArrayList<Alignment>();

	public Sample() {
		super();
	}
	
	public Sample(int id) {
		super();
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void addAlignment(final Alignment alignment) {
		alignments.add(alignment);
	}

	public Collection<Alignment> getAlignments() {
		return alignments;
	}

	public void setAlignments(Collection<Alignment> alignments) {
		this.alignments = alignments;
	}
   
}
