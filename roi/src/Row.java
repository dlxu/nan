import java.sql.Date;

/**
 * 
 */

/**
 * @author 
 *
 */
public class Row {

	private Date date;
	private double mv;
	private double cf;
	private double af;
	private double bm;
	
	/**
	 * @param date
	 * @param mv
	 * @param cf
	 * @param af
	 * @param bm
	 */
	public Row() {

	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getMv() {
		return mv;
	}

	public void setMv(double mv) {
		this.mv = mv;
	}

	public double getCf() {
		return cf;
	}

	public void setCf(double cf) {
		this.cf = cf;
	}

	public double getAf() {
		return af;
	}

	public void setAf(double af) {
		this.af = af;
	}

	public double getBm() {
		return bm;
	}

	public void setBm(double bm) {
		this.bm = bm;
	}
	
	
}
