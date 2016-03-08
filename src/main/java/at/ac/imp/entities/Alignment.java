package at.ac.imp.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Alignment
 *
 */
@Entity
public class Alignment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne
	private Reference reference;

	public Alignment() {
		super();
	}
   
}
