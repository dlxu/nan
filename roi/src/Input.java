import java.util.ArrayList;
import java.sql.Date;

/**
 * 
 */

/**
 * @author derek
 *
 */
public class Input {

	private Date startDate;
	private Date endDate;
	private ArrayList<Row> rows;
	/**
	 * 
	 */
	public Input() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the rows
	 */
	public ArrayList<Row> getRows() {
		return rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(ArrayList<Row> rows) {
		this.rows = rows;
	}
	
	
}
