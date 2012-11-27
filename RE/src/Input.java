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
	private int start; //start index of start date
	private int end; //end index of end date
	private ArrayList<Row> rows;
	/**
	 * 
	 */
	public Input() {
		this.start = -1;
		this.end = -1;
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
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	
	
}
