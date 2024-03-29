import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

public class ReadFile {

	private String fileName;
	
	private Input input;
	
	private ArrayList<Row> rows;
	
	//private String evpr = "\\s{0,}Evaluation\\s{1,}Period:\\s{0,}\\d{4}-\\d{2}-\\d{2}\\s{1,}to\\s{1,}\\d{4}-\\d{2}-\\d{2}\\s{0,},*\\s{0,}";
	
	private String evpr = "\\s{0,}Evaluation\\s{1,}Period:.*";
	
	private String colh = "(\\s)*Transaction(\\s)+Date\\s*,\\s*Market Value\\s*,\\s*Cash Flow\\s*,\\s*Agent Fees\\s*,\\s*Benchmark\\s*,*\\s*";

	private String name = "\\s*Name:\\s*\\w+[\\s\\w]*\\s*,*\\s*";
	
	private BufferedReader br;
	
	/**
	 * 
	 * @param fileName
	 */
	public ReadFile(String fileName) {
		this.fileName = fileName;
		this.input = new Input();
		this.rows = new ArrayList<Row>();
	}

	
	public void readFile() throws IOException
	{
		br = new BufferedReader(new FileReader(fileName));
		String line = null ;
		
		int i = 0; //line number
		
		boolean foundEvpr = false;
		boolean foundCh = false;
		
		Date last = null; // for date comparison
		
		
		//while (!foundEvpr && (line = br.readLine())!=null )
		
		while (!foundCh && (line = br.readLine())!=null )
		{

				
			i++;
			
			//find account number
			if(line.matches(name))
			{
				this.input.setAccountNumber(line.replaceAll(",", ""));
			}
			
			//find evaluation period
			if (line.matches(evpr))
			{
				foundEvpr = true;
				
				line= line.replaceAll(",", ""); // remove extra , at the end of line.
				
				String[] words = line.split(" ");
				String sd = words[2].trim();
				
				String ed = words[4].trim();
				try
				{
					Date startDate = Date.valueOf(sd);
					Date endDate = Date.valueOf(ed);
					
								
					if(startDate.toString().equals(sd) && endDate.toString().equals(ed))
					{
						if (startDate.before(endDate))
						{
							this.input.setStartDate(startDate);
							this.input.setEndDate(endDate);
						}else
						{
							this.input.seteWarning("The endDate of evaluation period is not after StartDate!");
						}
					}else
					{
						this.input.seteWarning("The date of evaluation period is invalid!");
					}
					
					/*if(!startDate.toString().equals(sd) || !endDate.toString().equals(ed))
					{
						System.out.println("Invalid Start Date or End Date input at line "+i+" !");
						System.exit(-1);
					}else
					
					this.input.setStartDate(startDate);
					this.input.setEndDate(endDate);
					*/
				}catch (IllegalArgumentException e)
				{
					//startDate and enddate remain null
					this.input.seteWarning("The date of evaluation period may conatin illegal chars!");
				}
			}
			
			//find column head line
			if (line.matches(colh)) foundCh = true;	
		}
		
		if (!foundEvpr)
		{
			this.input.seteWarning("The evaluation period not found!");
		}
		
		if (!foundCh)
		{
			System.out.println("Column head not found or invalid!");
			System.exit(-1);
		}
		
		
		while ((line = br.readLine())!=null && !line.replaceAll(",","").trim().isEmpty())
		{
			i++;
			
			Row r = new Row();
			
			String[] words = line.split(",");
			int length = words.length;
			if (length < 2) 
			{
				System.out.println("Invalid input at line " + i + ", please verify your input first!");
				System.exit(-2);
			}
			
			String d = words[0].trim();
			
			try
			{
				Date transactionDate = Date.valueOf(d);
				double mv = Double.parseDouble(words[1].trim());
				
				//market value cannot less than zero
				if (mv < 0)
				{
					System.out.println("Market value cannot be negative at line "+i+" !");
					System.exit(-1);
				}
						
				if(!transactionDate.toString().equals(d))
				{
					System.out.println("Invalid transaction Date input at line "+i+" !");
					System.exit(-1);
				}
				
				/* check the order of the dates */
				if (last != null && !transactionDate.after(last)) 
				{
					System.out.println("Transaction Date input at line "+i+" is not after the date in previous line!");
					System.exit(-1);
				}else
				{
					last = transactionDate;
				}
				

				r.setDate(transactionDate);
				r.setMv(mv);
				
				if(this.rows.size()>0 )
				{
					Row p = this.rows.get(this.rows.size()-1);
					if ((p.getCf()+p.getMv())==0 && mv > 0)
					{
						System.out.println("Impossible market value input at line "+i+" !");
						System.exit(-1);
					}
				}
				
			}catch (NumberFormatException e)
			{
				System.out.println("Invalid market value input at line "+i+" !");
				System.exit(-1);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Invalid transaction Date input at line "+i+" !");
				System.exit(-1);
			}
			
			r.setAf(0);
			r.setBm(Double.NaN);
			r.setCf(0);
			
			if (r.getCf() + r.getMv() < 0)
			{
				System.out.println("Cannot withdraw more than you have!");
				System.exit(-1);
			}
			
			//cash flow
			if (length > 2 )
			{
				String w = words[2].trim();
				if (w.length() > 0)
				{
					try
					{
						double cf = Double.parseDouble(w);
						r.setCf(cf);
					}catch (NumberFormatException e)
					{
						System.out.println("Invalid cash flow input at line "+i+" !");
						System.exit(-1);
					}
				}
			}
			
			//agent fee
			if (length > 3 )
			{
				String w = words[3].trim();
				if (w.length() > 0)
				{
					try
					{
						double af = Double.parseDouble(w);
						if (af < 0)
						{
							System.out.println("Agent fee cannot be negative at line "+i+" !");
							System.exit(-1);
						}
						r.setAf(af);
					}catch (NumberFormatException e)
					{
						System.out.println("Invalid agent fee input at line "+i+" !");
						System.exit(-1);
					}
				}
			}
			
			//Bench mark
			if (length > 4 )
			{
				String wi = words[4].trim();
				
				//remove the ',' and trim the white space
				wi = wi.replaceAll(",", "").trim();
	
				if (wi.length() > 0)
				{
					if (!wi.endsWith("%"))
					{
						System.out.println("Oops! Are you sure you have % for your benchmark input at line "+i+" ?");
						System.exit(-1);
					}
					String w = wi.substring(0, wi.length() - 1);
					
					try
					{
						double bm = Double.parseDouble(w);
						r.setBm(bm/100);
					}catch (NumberFormatException e)
					{
						System.out.println("Invalid benchmark input at line "+i+" !");
						System.exit(-1);
					}
				}
			}
			rows.add(r);
			
			// get index of rows for evaluation period start and end date
			if(this.input.getStartDate()!=null && r.getDate().compareTo(this.input.getStartDate()) == 0)
			{
				this.input.setStart(rows.indexOf(r));
			}
			
			if(this.input.getEndDate()!=null && r.getDate().compareTo(this.input.getEndDate()) == 0)
			{
				//System.out.println(this.input.getEndDate());
				this.input.setEnd(rows.indexOf(r));
			}
		}
		
		if (this.input.getStartDate()!=null && this.input.getEndDate()!=null && (this.input.getStart() < 0 || this.input.getEnd() < 0))
		{
			this.input.seteWarning("The evaluation period out of scope!");
		}
		
		//test if there is empty line inside file or at the end of file.
		//assume end of input, so empty line acceptable at the end of input
		while (line !=null)
		{
			i++;
			if (!line.replaceAll(",","").trim().isEmpty())
			{
				System.out.println("Please check line "+i+" !");
				System.exit(-1);
			}
			line = br.readLine();
		}
		
		if (this.rows.size()<2)
		{
			System.out.println("Sorry, cannot compute any information for you with only one transaction");
			System.exit(-1);
		}

		this.input.setRows(this.rows);	
	}

	/**
	 * @return the input
	 */
	public Input getInput() {
		return input;
	}
	
	

}
