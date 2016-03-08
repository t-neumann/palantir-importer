package at.ac.imp.entities;

import java.io.Serializable;
import javax.persistence.*;

import at.ac.imp.entities.Datapoint;

/**
 * Entity implementation class for Entity: FoldChange
 *
 */
@Entity

public class FoldChange extends Datapoint implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private double foldchange;
	private double pvalue;

	public double getFoldchange() {
		return foldchange;
	}

	public void setFoldchange(double foldchange) {
		this.foldchange = foldchange;
	}

	public double getPvalue() {
		return pvalue;
	}

	public void setPvalue(double pvalue) {
		this.pvalue = pvalue;
	}

	public FoldChange(double foldchange, double pvalue) {
		super();
		this.foldchange = foldchange;
		this.pvalue = pvalue;
	}

	public FoldChange() {
		super();
	}
   
}
