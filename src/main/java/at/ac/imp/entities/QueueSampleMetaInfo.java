package at.ac.imp.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class QueueSampleMetaInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private int sampleId;
	private String sequencer = "";
	private String vendor = "";
	private String flowcellId = "";
	private String readType = "";
	private int lane;
	private String user = "";
	private String organism = "";
	private String celltype = "";
	private String experimentType = "";
	private String genotype = "";
	private String antibody = "";
	private String primer = "";
	
	public int getSampleId() {
		return sampleId;
	}
	public void setSampleId(int sampleId) {
		this.sampleId = sampleId;
	}
	public String getSequencer() {
		return sequencer;
	}
	public void setSequencer(String sequencer) {
		this.sequencer = sequencer;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getFlowcellId() {
		return flowcellId;
	}
	public void setFlowcellId(String flowcellId) {
		this.flowcellId = flowcellId;
	}
	public String getReadType() {
		return readType;
	}
	public void setReadType(String readType) {
		this.readType = readType;
	}
	public int getLane() {
		return lane;
	}
	public void setLane(int lane) {
		this.lane = lane;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	public String getCelltype() {
		return celltype;
	}
	public void setCelltype(String celltype) {
		this.celltype = celltype;
	}
	public String getExperimentType() {
		return experimentType;
	}
	public void setExperimentType(String experimentType) {
		this.experimentType = experimentType;
	}
	public String getGenotype() {
		return genotype;
	}
	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}
	public String getAntibody() {
		return antibody;
	}
	public void setAntibody(String antibody) {
		this.antibody = antibody;
	}
	public String getPrimer() {
		return primer;
	}
	public void setPrimer(String primer) {
		this.primer = primer;
	}
	public QueueSampleMetaInfo(int sampleId, String sequencer, String vendor, String flowcellId, String readType,
			int lane, String user, String organism, String celltype, String experimentType, String genotype,
			String antibody, String primer) {
		super();
		this.sampleId = sampleId;
		this.sequencer = sequencer;
		this.vendor = vendor;
		this.flowcellId = flowcellId;
		this.readType = readType;
		this.lane = lane;
		this.user = user;
		this.organism = organism;
		this.celltype = celltype;
		this.experimentType = experimentType;
		this.genotype = genotype;
		this.antibody = antibody;
		this.primer = primer;
	}
	
	public QueueSampleMetaInfo() {
		super();
	}
	
	@Override
	public String toString() {
		return "QueueSampleMetaInfo [sampleId=" + sampleId + ", sequencer=" + sequencer + ", vendor=" + vendor
				+ ", flowcellId=" + flowcellId + ", readType=" + readType + ", lane=" + lane + ", user=" + user
				+ ", organism=" + organism + ", celltype=" + celltype + ", experimentType=" + experimentType
				+ ", genotype=" + genotype + ", antibody=" + antibody + ", primer=" + primer + "]";
	}
}
