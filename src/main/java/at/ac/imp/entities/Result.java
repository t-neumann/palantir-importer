package at.ac.imp.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Entity implementation class for Entity: Result
 *
 */
@Entity
public class Result implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	private Alignment alignment;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	private Reference reference;

	@OneToMany(cascade = CascadeType.ALL)
	private Collection<Datapoint> datapoints = new ArrayList<Datapoint>();
	
	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}
	
	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	public Collection<Datapoint> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(Collection<Datapoint> datapoints) {
		this.datapoints = datapoints;
	}
	
	public Result() {
		super();
	}
   
}
