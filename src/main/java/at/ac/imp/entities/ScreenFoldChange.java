package at.ac.imp.entities;

import java.io.Serializable;
import javax.persistence.*;

import at.ac.imp.entities.FoldChange;

/**
 * Entity implementation class for Entity: ScreenFoldChange
 *
 */
@Entity

public class ScreenFoldChange extends FoldChange implements Serializable {

	private String flag;
	private String comment;

	private static final long serialVersionUID = 1L;

	public ScreenFoldChange(double foldchange, double pvalue, String flag, String comment) {
		super(foldchange, pvalue);
		this.flag = flag;
		this.comment = comment;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ScreenFoldChange() {
		super();
	}

}
