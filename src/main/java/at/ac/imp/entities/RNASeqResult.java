package at.ac.imp.entities;

import at.ac.imp.entities.Result;
import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: RNASeqResult
 *
 */
@Entity

public class RNASeqResult extends Result implements Serializable {
	
	@ManyToOne
	private Alignment alignment;
	
	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	private static final long serialVersionUID = 1L;

	public RNASeqResult() {
		super();
	}
   
}
