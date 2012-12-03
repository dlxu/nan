import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.Days;

import de.jtem.numericalMethods.calculus.function.RealFunctionOfOneVariable;
import de.jtem.numericalMethods.calculus.rootFinding.Brent;
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
	
	private Input input;

	/**
	 * @param input
	 */
	public Roi(Input input) 
	{
		this.input = input;
		this.eEnd = input.getEnd();
		this.eStart = input.getStart();
		this.startDate = input.getStartDate();
		this.endDate = input.getEndDate();
		this.rows = input.getRows();
		this.report = new StringBuffer();
	}
	
	public void generateReport()
	{
		int length = this.rows.size();
		double twrOverall = 0;
		double twrEvaluation = 0;
		
		//days over all period
		double oDuration = this.duration(this.rows.get(0).getDate(), this.rows.get(length-1).getDate());
		
		this.report.append("\n===========================================\n");
		this.report.append(this.input.getAccountNumber()  + "\n===========================================\n\n");
		//=========over all period=================//
		//=========over all period=================//
		this.report.append("Overall period: ");
		this.report.append(this.rows.get(0).getDate());
		this.report.append(" to ");
		this.report.append(this.rows.get(length-1).getDate());
		this.report.append("\n===========================================\nTWR: ");
	
		
		
		
		
		//TWR calculating.
		try
		{
			twrOverall = this.twr(0, length - 1);
/*
			this.report.append(String.format("%.2f%%%n", twrOverall*100));
			this.report.append("Annualized TWR: ");
			
			double an_twrOverall = twrOverall;
			if (oDuration > 1)
			{

				an_twrOverall = Math.pow((1 + twrOverall), (1/oDuration)) - 1;

			}*/
			
			this.report.append(String.format("%.2f%%%n", this.annualize(twrOverall, oDuration)*100));
		}catch (RoiException e)
		{
			this.report.append(e.getMessage());
			this.report.append("\n");
		}
		
		//benchmark 
		this.report.append("Benchmark: ");
		try
		{
			//System.out.println(this.annualize(this.benchMarkRate(0, length - 1)-1, oDuration));
			/*	double bm = this.benchMarkRate(0, length-1);
			
			double an_bm = bm;
			if (oDuration > 1)
			{
				an_bm = Math.pow((1 + bm), (1/oDuration)) - 1;
			}*/
			
			this.report.append(String.format("%.2f%%%n", this.annualize(this.benchMark(0, length-1), oDuration)*100));
			
		}catch(RoiException e)
		{
			this.report.append(e.getMessage());
			this.report.append("\n");
		}
		
		//=========roi========
		
		this.report.append("ROI: ");		
		try {
			double fv = this.rows.get(length - 1).getMv();
			this.report.append(String.format("%.2f%%%n", this.annualize(this.generateRoi(0, length - 1, twrOverall+1, fv),oDuration)*100));
		} catch (RoiException e) {
			// TODO Auto-generated catch block
			this.report.append(e.getMessage());
			this.report.append("\n");
		}		
		
		
		
		
		
		//===========Evaluation period=======================//
		//===========Evaluation period=======================//

		this.report.append("\nEvaluation period: ");
		
		
		if (this.input.getEnd() >= 0 && this.input.getStart() >= 0 )
		{
			//days evaluation period
			double eDuration = this.duration(this.rows.get(this.eStart).getDate(), this.rows.get(this.eEnd).getDate());
			
			this.report.append(this.startDate);
			this.report.append(" to ");
			this.report.append(this.endDate);
			this.report.append("\n===========================================\nTWR: ");
			
			
		
			//TWR calculating.
			try
			{
				twrEvaluation = this.twr(this.eStart, this.eEnd);
	/*			this.report.append(String.format("%.2f%%%n", twrEvaluation*100));
				this.report.append("Annualized TWR: ");*/
	/*			
				double an_twrEvaluation = twrEvaluation;
				if (eDuration > 1)
				{
					an_twrEvaluation = Math.pow((1 + twrEvaluation), 1/eDuration) - 1;
				}
				this.report.append(String.format("%.2f%%%n", an_twrEvaluation*100));*/
				this.report.append(String.format("%.2f%%%n", this.annualize(twrEvaluation, eDuration)*100));
			}catch (RoiException e)
			{
				this.report.append(e.getMessage());
				this.report.append("\n");
			}
			
			//benchmark 
			this.report.append("Benchmark: ");
			try
			{
				//System.out.println(this.annualize(this.benchMarkRate(this.eStart, this.eEnd)-1, eDuration));
				/*double bm = this.benchMarkRate(this.eStart, this.eEnd);
				
				double an_bm = bm;
				if (eDuration > 1)
				{
	
					an_bm = Math.pow((1 + bm), (1/eDuration)) - 1;
	
				}*/
				
				this.report.append(String.format("%.2f%%%n", this.annualize(this.benchMark(this.eStart, this.eEnd), eDuration)*100));
				//this.report.append(String.format("%.2f%%%n", bm*100));
				
			}catch(RoiException e)
			{
				this.report.append(e.getMessage());
				this.report.append("\n");
			}
			
			//roi
			
			this.report.append("ROI: ");		
			try {
				//this.report.append(this.Annualize(this.generateRoi(eStart, eEnd, twrEvaluation), eDuration));
				double fv = this.rows.get(eEnd).getMv();
				this.report.append(String.format("%.2f%%%n", this.annualize(this.generateRoi(eStart, eEnd, twrEvaluation, fv), eDuration)*100));
			} catch (RoiException e) {
				// TODO Auto-generated catch block
				this.report.append(e.getMessage());
				this.report.append("\n");
			}
		}else
		{
			//System.out.println("we are here" + this.input.geteWarning());
			this.report.append(this.input.geteWarning());
			this.report.append("\n");
		}
	}
	
	
	
	private double benchMark(int sd, int ed) throws RoiException
	{
		Row sdr = this.rows.get(sd);
		double fv = (sdr.getAf() + sdr.getCf() + sdr.getMv())*this.benchMarkRate(sd, ed);
		
		
		//double pv = sdr.getAf() + sdr.getCf() + sdr.getMv();
		
		for (int i = sd + 1; i < ed; i++)
		{
			Row r = this.rows.get(i);
			double cf = r.getAf() + r.getCf();
			
			if(cf != 0 )
			{
				fv = fv + cf*this.benchMarkRate(i, ed);
			}
		}
		//System.out.println(fv);
		if (fv < 0)
		{
			throw new RoiException("The benchmark will give you a negative return on your investiment!");
		}
		return this.generateRoi(sd, ed, this.benchMarkRate(sd, ed), fv);
		
	}
	
	/**
	 * 
	 * @param s -start index to calculate benchmark 
	 * @param e -end index to calculate benchmark
	 * @return
	 */
	private double benchMarkRate(int s, int e) throws RoiException
	{
		double result = 1;
		
		//fraction part of year for start date, 
		//if it happen to be new year day, the value is 1
		double sfractionYear = 1;
		
		//fraction part of year for end date
		//if it happen to be new year day, the value is 1
		double efractionYear = 1;
		
		Row r = this.rows.get(s);
		Row re = this.rows.get(e);
		
		Date begin = r.getDate();
		Date end = re.getDate();
		
		DateTime beginTime = new DateTime(begin);
		DateTime endTime = new DateTime(end);
		
		Date nextNewYearDate = this.nextNewYearDay(begin);
		Date firstNewYearDay = nextNewYearDate;
		/*DateTime std = new DateTime(nextNewYearDate.getTime());
		System.out.println (std.getDayOfYear() + " " + std.getYear());*/
		
		
		if (!this.isNewYearDay(begin))
		{
			sfractionYear = this.duration(begin, nextNewYearDate);
		}

		
		for (int i = s + 1; i <= e; i++ )
		{
			
			r = this.rows.get(i);
			if (r.getDate().compareTo(nextNewYearDate) == 0)
			{
				double bm = r.getBm();

				if (Double.isNaN(bm))
				{
					throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
				}
				
				//check if it is first new year day after the start date
				if (r.getDate().compareTo(firstNewYearDay)==0)
				{
					result = result * Math.pow(1+bm, sfractionYear);
					//System.out.printf("benchmark %f value %f %n", bm, Math.pow(1+bm, sfractionYear));
				}else 
				{
					result = result * (1 + bm);
					//System.out.println("benchmark "+ bm);
				}
				
				
				nextNewYearDate = this.nextNewYearDay(r.getDate());
				
			}else if (r.getDate().compareTo(nextNewYearDate) > 0)
			{
				throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
			}
		}
		
	/*	//reach the end date of required calculation period after for loop
		r = this.rows.get(e);
		
		//did not find the benchmark for next new year day, current day is after new year day.
		if (r.getDate().compareTo(nextNewYearDate) > 0) 
		{
			throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
		}
		// e(index of end of period) is new year day or (last row over all period)year to date, next new year day is not in scope
		else if(r.getDate().compareTo(nextNewYearDate) == 0 || this.rows.size() == (e + 1)) */
		
		//e is not new year day.
		r = this.rows.get(e);
		nextNewYearDate = this.nextNewYearDay(r.getDate());
		if (!this.isNewYearDay(r.getDate())){
			
			//(end of period e is last row over all period)year to date, next new year day is not in scope
			
			int deltaDays = endTime.getDayOfYear() - 1;
			if (beginTime.getYear() == endTime.getYear())//what if start date and end in same year
			{
				deltaDays = endTime.getDayOfYear() -beginTime.getDayOfYear();
			}
		/*	if(this.rows.size() == (e + 1))
			{
				double bm = r.getBm();
				if (Double.isNaN(bm))
				{
					throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + r.getDate());
				}
				if (beginTime.getYear() == endTime.getYear())//what if start date and end in same year
				{
					efractionYear = 1.0*(endTime.getDayOfYear() -beginTime.getDayOfYear())/(endTime.getDayOfYear() - 1);
				}else
				{
					efractionYear = 1;
				}
					
				result = result * Math.pow(1 + bm,efractionYear);
			} 
			//e is before new year day or year to date
			//end of list of rows is before new year day, next new year day is out of scope, check end of rows for bm;
			else*/
			if (this.rows.get(this.rows.size()-1).getDate().compareTo(nextNewYearDate) < 0)
			{
				Row lastRow = this.rows.get(this.rows.size()-1);
/*				
				if (beginTime.getYear() == endTime.getYear())
				{*/
					efractionYear = 1.0*deltaDays/(new DateTime(lastRow.getDate()).getDayOfYear() - 1);
	/*			}else
				{
					efractionYear = 1.0*(endTime.getDayOfYear() - 1)/(new DateTime(lastRow.getDate()).getDayOfYear() - 1);
				}*/
				
				double bm = lastRow.getBm();
				if (Double.isNaN(bm))
				{
					throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + r.getDate());
				}
				result = result * Math.pow(1 + bm,efractionYear);
			}
			else
			{
				boolean found = false;
				
				// try to find next new year
				for (int i = e + 1; i < this.rows.size() && !found; i++ )
				{
					r = this.rows.get(i);
/*					
					if (beginTime.getYear() == endTime.getYear())
					{
						efractionYear = 1.0*(endTime.getDayOfYear() -beginTime.getDayOfYear())/Roi.daysOfYear;
					}else
					{
						efractionYear = (endTime.getDayOfYear() - 1)/Roi.daysOfYear;
					}*/
				
					//System.out.println(new DateTime(this.rows.get(e).getDate()).getDayOfYear());
					
					if (r.getDate().compareTo(nextNewYearDate) == 0)
					{
						double bm = r.getBm();
						efractionYear = 1.0*deltaDays/Roi.daysOfYear;
	
						if (Double.isNaN(bm))
						{
							throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
						}
						result = result * Math.pow(1 + bm, efractionYear);
						found = true;
					}else if (r.getDate().compareTo(nextNewYearDate) > 0)
					{
						throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + nextNewYearDate);
					}
				}
				

				
		/*		//if 
				if (!found) // if not found, check the last row in data, which is before nextnewyear date 
				{
					double bm = r.getBm();
	
					if (Double.isNaN(bm))
					{
						throw new RoiException("The benchmark cannot be calculated, missing benchmark of date " + r.getDate());
					}
					result = result * (1 + bm);
				}*/
			}
		}
		
		//System.out.println(result);
		return result;
	}

	/**
	 * 
	 * @param first -the first row of data to be in calculation
	 * @param last -the last row of data to be in calculation
	 * @return -the TWR for the portfolio
	 * @throws RoiException
	 */
	private double twr(int first, int last) throws RoiException
	{
		double result = 1;
		Row r;
		
		for (int i = first + 1; i <= last; i++)
		{
			r = this.rows.get(i-1);
			double denominator = r.getAf() + r.getMv() + r.getCf();
			if (denominator <= 0)
			{
				throw new RoiException("The sum of market value, cash flow is not positive on date " + r.getDate());
			}
			result = result * this.rows.get(i).getMv()/denominator;
		}
		return (result - 1);
	}

	/**
	 * 
	 * @param first -the first row of data to be in calculation
	 * @param last -the last row of data to be in calculation
	 * @param twr -the initial guess twr + 1
	 * @param fv2 
	 * @return -the ROI for the portfolio
	 * @throws RoiException
	 */
	private double generateRoi(int first, int last, double twr, double fv) throws RoiException
	{
		
		if (!this.checkRoi(first, last))
		{
			throw new RoiException("Market value and cash flow are all zero in the period, nothing can be done!");
		}
		/*if (fv == 0)
		{
			return -1;
		}
		*/
		double result = 1;
		Date theLastDay = this.rows.get(last).getDate();
		Date theFirstDay = this.rows.get(first).getDate();
		Row r = this.rows.get(first);
		
		final double pv = r.getMv() + r.getAf() + r.getCf();
		final double fvalue = fv;
		//System.out.println("fv "+fvalue);
		double overall = this.duration(theFirstDay, theLastDay);
		HashMap<Double,Double> map = new HashMap<Double,Double>();

		for (int i = first + 1; i < last; i++)
		{
			r = this.rows.get(i);
			double v = r.getAf() + r.getCf();
			if (v != 0)
			{
				map.put(this.duration(r.getDate(), theLastDay)/overall, v);
			}
		}
		
		final HashMap<Double,Double> map2 = map;
		
		RealFunctionOfOneVariable rfo = new RealFunctionOfOneVariable(){
			public double eval(double x){
				double f = pv * x - fvalue;
				
				
				for (double d : map2.keySet()) 
				{
					f = f + map2.get(d) * Math.pow(x, d);
				}
				
				return f;
			}
		};
		
		NewtonRaphson.RealFunctionWithDerivative rf = new NewtonRaphson.RealFunctionWithDerivative() {
			public void eval(double x, double[] f, int offsetF, double[] df,
					int offsetDF) 
			{

				/*double[] m = null ; 
				double[] n = null;
				m[offsetF] = pv * x - fv;
				n[offsetDF] = pv;*/
				f[offsetF] = pv * x - fvalue;
				
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
          boolean success = false;
          for (int i = 1; i < 15 && !success ; i++)
          {
	           success = true;
	           double guess = Math.pow(i, 2);
        	  try
	           {
        		  NewtonRaphson.search(rf, guess, rootValues ); // start our serch in 0.6
	           }catch(RuntimeException e)
	           {

	        	   success = false;
	        	   //throw new RoiException("cannot be calculated, check your input!");
	           }
        	  
          }
          
          if (!success)
          {
        	try{
        		result = Brent.search(rfo, 0, 100, 0.000001) - 1; // start our serch in 0.6
        	}catch (Exception e)
        	{
        		throw new RoiException("cannot be calculated, check your input!");
        	}
	           
          }
          else{
        	  result = rootValues[0] - 1;
          }
		/*for (double d: rootValues)
		{
			System.out.println(d + " roi ");
		}
		*/
		
		
		
		return result;
	}
	
	/**
	 * 
	 * @param sd -start date
	 * @param ed -end date
	 * @return
	 */
	private double duration(Date sd, Date ed) {
		// TODO Auto-generated method stub
		DateTime startDate = new DateTime(sd.getTime()); 
		DateTime endDate = new DateTime(ed.getTime());
		Days d = Days.daysBetween(startDate, endDate);
		return d.getDays()/Roi.daysOfYear;
	}


	/**
	 * 
	 * @param temp
	 * @return
	 */
	private Date nextNewYearDay(Date temp) {
		
		DateTime std = new DateTime(temp.getTime());
		int nextyear = std.getYear() + 1;
		
		return Date.valueOf(nextyear + "-01-01");
	}
	
	
	private boolean isNewYearDay(Date temp)
	{
		DateTime std = new DateTime(temp.getTime());
		return (std.getDayOfYear()==1);
	}

	

	/**
	 * 
	 * @param input
	 * @param duration
	 * @return
	 */
	private double annualize(double input, double duration)
	{
		return 	(duration > 1)? Math.pow((1 + input), (1/duration)) - 1 : input;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 */
	private boolean checkRoi(int a, int b)
	{
		boolean good = false;
		for (int i = a; i < this.rows.size() && !good; i++)
		{
			Row rtemp = this.rows.get(i);
			if (rtemp.getMv() !=0 || rtemp.getCf() != 0) good = true;
		}
		return good;
	}
	
	/**
	 * @return the report
	 */
	public StringBuffer getReport() {
		return report;
	}
	
	
}
