import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.Days;

import de.jtem.numericalMethods.calculus.rootFinding.NewtonRaphson;

/**
 * 
 */

/**
 * @author derek
 *
 */
public class Roi {

	private static final double daysOfYear = 365;
	
	private Date startDate;
	
	private Date endDate;
	
	private int eStart;
	
	private int eEnd;
	
	private ArrayList<Row> rows;
	
	private StringBuffer report;

	/**
	 * @param input
	 */
	public Roi(Input input) 
	{

		this.eEnd = input.getEnd();
		this.eStart = input.getStart();
		this.startDate = input.getStartDate();
		this.endDate = input.getEndDate();
		this.rows = input.getRows();
		this.report = new StringBuffer();
	}
	
	public void GenerateReport()
	{
		int length = this.rows.size();
		double twrOverall = 0;
		double twrEvaluation = 0;
		
		//days over all period
		double oDuration = this.Duration(this.rows.get(0).getDate(), this.rows.get(length-1).getDate());
		//days evaluation period
		double eDuration = this.Duration(this.rows.get(this.eStart).getDate(), this.rows.get(this.eEnd).getDate());
		//=========over all period=================//
		this.report.append("Overall period: ");
		this.report.append(this.rows.get(0).getDate());
		this.report.append(" to ");
		this.report.append(this.rows.get(length-1).getDate());
		this.report.append("\n========================\nTWR: ");
	
		
		
		
		
		//TWR calculating.
		try
		{
			twrOverall = this.Twr(0, length - 1);

			this.report.append(String.format("%.2f%%%n", twrOverall*100));
			this.report.append("Annualized TWR: ");
			
			double an_twrOverall = twrOverall;
			if (oDuration > 1)
			{

				an_twrOverall = Math.pow((1 + twrOverall), (1/oDuration)) - 1;

			}
			this.report.append(String.format("%.2f%%%n", an_twrOverall*100));
		}catch (RoiException e)
		{
			this.report.append(e.getMessage());
			this.report.append("\n");
		}
		
		//benchmark 
		this.report.append("Benchmark: ");
		try
		{
			double bm = this.BenchMark(0, length-1);
			
			double an_bm = bm;
			if (oDuration > 1)
			{

				an_bm = Math.pow((1 + bm), (1/oDuration)) - 1;

			}
			
			this.report.append(String.format("%.2f%%%n", an_bm*100));
			
		}catch(RoiException e)
		{
			this.report.append(e.getMessage());
			this.report.append("\n");
		}
		
		//roi
		
		this.report.append("ROI: ");		
		try {
			this.report.append(this.generateRoi(0, length - 1, twrOverall));
		} catch (RoiException e) {
			// TODO Auto-generated catch block
			this.report.append(e.getMessage());
			this.report.append("\n");
		}		
		
		
		
		
		
		//===========Evaluation period=======================//

		this.report.append("\nEvaluation period: ");
		this.report.append(this.startDate);
		this.report.append(" to ");
		this.report.append(this.endDate);
		this.report.append("\n========================\nTWR: ");
		
		
	
		//TWR calculating.
		try
		{
			twrEvaluation = this.Twr(this.eStart, this.eEnd);
			this.report.append(String.format("%.2f%%%n", twrEvaluation*100));
			this.report.append("Annualized TWR: ");
			
			double an_twrEvaluation = twrEvaluation;
			if (eDuration > 1)
			{
				an_twrEvaluation = Math.pow((1 + twrEvaluation), 1/eDuration) - 1;
			}
			this.report.append(String.format("%.2f%%%n", an_twrEvaluation*100));
		}catch (RoiException e)
		{
			this.report.append(e.getMessage());
			this.report.append("\n");
		}
		
		//benchmark 
		this.report.append("Benchmark: ");
		try
		{
			double bm = this.BenchMark(this.eStart, this.eEnd);
			
			double an_bm = bm;
			if (eDuration > 1)
			{

				an_bm = Math.pow((1 + bm), (1/eDuration)) - 1;

			}
			
			this.report.append(String.format("%.2f%%%n", an_bm*100));
			//this.report.append(String.format("%.2f%%%n", bm*100));
			
		}catch(RoiException e)
		{
			this.report.append(e.getMessage());
			this.report.append("\n");
		}
		
		//roi
		
		this.report.append("ROI: ");		
		try {
			this.report.append(this.generateRoi(eStart, eEnd, twrEvaluation));
		} catch (RoiException e) {
			// TODO Auto-generated catch block
			this.report.append(e.getMessage());
			this.report.append("\n");
		}
	}
	
	/**
	 * 
	 * @param s -start index to calculate benchmark 
	 * @param e -end index to calculate benchmark
	 * @return
	 */
	private double BenchMark(int s, int e) throws RoiException
	{
		double result = 1;
		
		Row r = this.rows.get(s);
		
		Date temp = r.getDate();
		
		Date nextNewYearDate = this.NextNewYearDay(temp);

		for (int i = s; i < e; i++ )
		{
			
			r = this.rows.get(i);
			if (r.getDate().compareTo(nextNewYearDate) == 0)
			{
				double bm = r.getBm();

				if (Double.isNaN(bm))
				{
					throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
				}
				result = result * (1 + bm);
				nextNewYearDate = this.NextNewYearDay(r.getDate());
			}else if (r.getDate().compareTo(nextNewYearDate) > 0)
			{
				throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
			}
		}
		
		//the end date of required calculation period
		r = this.rows.get(e);
		if (r.getDate().compareTo(nextNewYearDate) > 0)
		{
			throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
		}else if(r.getDate().compareTo(nextNewYearDate) == 0 || this.rows.size() == (e + 1)) // e is year end or year to date
		{
			double bm = r.getBm();
			if (Double.isNaN(bm))
			{
				throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + r.getDate());
			}
			result = result * (1 + bm);
		}else
		{
			boolean found = false;
			for (int i = e + 1; i < this.rows.size() && !found; i++ )
			{
				r = this.rows.get(i);
				if (r.getDate().compareTo(nextNewYearDate) == 0)
				{
					double bm = r.getBm();

					if (Double.isNaN(bm))
					{
						throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
					}
					result = result * (1 + bm);
					found = true;
				}else if (r.getDate().compareTo(nextNewYearDate) > 0)
				{
					throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
				}
			}
			
			if (!found) // check the last row in data, which is before nextnewyear date 
			{
				double bm = r.getBm();

				if (Double.isNaN(bm))
				{
					throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + r.getDate());
				}
				result = result * (1 + bm);
			}
		}
		
		
		return result - 1;
	}

	/**
	 * 
	 * @param temp
	 * @return
	 */
	private Date NextNewYearDay(Date temp) {
		
		DateTime std = new DateTime(temp.getTime());
		int nextyear = std.getYear() + 1;
		
		return Date.valueOf(nextyear + "-01-01");
	}


	/**
	 * 
	 * @param first -the first row of data to be in calculation
	 * @param last -the last row of data to be in calculation
	 * @return -the TWR for the portfolio
	 * @throws RoiException
	 */
	private double Twr(int first, int last) throws RoiException
	{
		double result = 1;
		Row r;
		
		for (int i = first + 1; i <= last; i++)
		{
			r = this.rows.get(i-1);
			double denominator = r.getAf() + r.getMv() + r.getCf();
			if (denominator <= 0)
			{
				throw new RoiException("The sum of market value, cash flow and agent is not positive at date " + r.getDate());
			}
			result = result * this.rows.get(i).getMv()/denominator;
		}
		return (result - 1);
	}

	/**
	 * 
	 * @param first -the first row of data to be in calculation
	 * @param last -the last row of data to be in calculation
	 * @param twr 
	 * @return -the ROI for the portfolio
	 * @throws RoiException
	 */
	private double generateRoi(int first, int last, double twr) throws RoiException
	{
		double result = 1;
		Date theLastDate = this.rows.get(last).getDate();
		Row r = this.rows.get(first);
		
		final double pv = r.getMv() + r.getAf() + r.getCf();
		final double fv = this.rows.get(last).getMv();
		
		double overall = this.Duration(r.getDate(), theLastDate);
		HashMap<Double,Double> map = new HashMap<Double,Double>();

		for (int i = first + 1; i < last; i++)
		{
			r = this.rows.get(i);
			double v = r.getAf() + r.getCf();
			if (v != 0)
			{
				map.put(this.Duration(r.getDate(), theLastDate)/overall, v);
			}
		}
		
		final HashMap<Double,Double> map2 = map;
		
		NewtonRaphson.RealFunctionWithDerivative rf = new NewtonRaphson.RealFunctionWithDerivative() {
			public void eval(double x, double[] f, int offsetF, double[] df,
					int offsetDF) 
			{

				/*double[] m = null ; 
				double[] n = null;
				m[offsetF] = pv * x - fv;
				n[offsetDF] = pv;*/
				f[offsetF] = pv * x - fv;
				df[offsetDF] = pv;
				for (double d : map2.keySet()) 
				{
					f[offsetF] = f[offsetF] + map2.get(d) * Math.pow(x, d);
					df[offsetDF] = df[offsetDF] + d*map2.get(d) * Math.pow(x, d-1);;
				}
/*				f[offsetF] = m[offsetF];
				df[offsetDF] = n[offsetDF];*/

			}
		};

           double[] rootValues = new double[3];
           try
           {
           NewtonRaphson.search(rf, twr, rootValues ); // start our serch in 0.6
           }catch(RuntimeException e)
           {
        	   throw new RoiException("The roi cannot be found");
           }
		/*for (double d: rootValues)
		{
			System.out.println(d + " roi ");
		}
		*/
		result = rootValues[0];
		
		
		return result;
	}
	
	/**
	 * 
	 * @param sd -start date
	 * @param ed -end date
	 * @return
	 */
	private double Duration(Date sd, Date ed) {
		// TODO Auto-generated method stub
		DateTime startDate = new DateTime(sd.getTime()); 
		DateTime endDate = new DateTime(ed.getTime());
		Days d = Days.daysBetween(startDate, endDate);
		return d.getDays()/Roi.daysOfYear;
	}
		
	
	/**
	 * @return the report
	 */
	public StringBuffer getReport() {
		return report;
	}
	

}
