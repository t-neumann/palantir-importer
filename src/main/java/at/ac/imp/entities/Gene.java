package at.ac.imp.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Gene
 *
 */
@Entity
@NamedQuery(name="Gene.findByName",query="SELECT g FROM Gene g WHERE g.geneSymbol = :name")
public class Gene implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String chr;
	private int start;
	private int end;
	
	private String geneSymbol;
	private boolean reverse;
	private int entrezId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Reference reference;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	protected Collection<Datapoint> datapoints;

	public Collection<Datapoint> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(Collection<Datapoint> datapoints) {
		this.datapoints = datapoints;
	}
	
	public void addDatapoint(Datapoint datapoint) {
		this.datapoints.add(datapoint);
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	public int getEntrezId() {
		return entrezId;
	}

	public void setEntrezId(int entrezId) {
		this.entrezId = entrezId;
	}
	
	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public Gene() {
		super();
	}

	public Gene(String chr, int start, int end, String geneSymbol, boolean reverse, int entrezId) {
		super();
		this.chr = chr;
		this.start = start;
		this.end = end;
		this.geneSymbol = geneSymbol;
		this.reverse = reverse;
		this.entrezId = entrezId;
	}

	@Override
	public String toString() {
		return "Gene [chr=" + chr + ", start=" + start + ", end=" + end + ", geneSymbol=" + geneSymbol + ", reverse="
				+ reverse + ", entrezId=" + entrezId + ", reference=" + reference + "]";
	}
}
