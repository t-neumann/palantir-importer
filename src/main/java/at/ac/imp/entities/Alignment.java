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
	
	private String name;
	
	private String build;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	private Sample sample;

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

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

	public Alignment() {
		super();
	}
	public Alignment(String name, String build) {
		super();
		this.name = name;
		this.build = build;
	}
   
}
