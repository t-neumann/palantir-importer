package at.ac.imp.entities;

import at.ac.imp.entities.Result;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: PairwiseResult
 *
 */
@Entity

public class PairwiseResult extends Result implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public PairwiseResult() {
		super();
	}

	@ManyToMany
	private Collection<Alignment> cases = new ArrayList<Alignment>();
	@ManyToMany
	private Collection<Alignment> controls = new ArrayList<Alignment>();
	
	public Collection<Alignment> getCases() {
		return cases;
	}

	public void setCases(Collection<Alignment> cases) {
		this.cases = cases;
	}

	public Collection<Alignment> getControls() {
		return controls;
	}

	public void setControls(Collection<Alignment> controls) {
		this.controls = controls;
	}
   
}
