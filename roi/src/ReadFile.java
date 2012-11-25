import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

public class ReadFile {

	private String fileName;
	
	private Input input;
	
	private ArrayList<Row> rows;
	
	private String evpr = "Evaluation Period: \\d{4}-\\d{2}-\\d{2} to \\d{4}-\\d{2}-\\d{2}(,)*";
	
	private String colh = "Transaction Date,Market Value,Cash Flow,Agent Fees,Benchmark(,)*";

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
		
		//find evaluation period
		while (!foundEvpr && (line = br.readLine())!=null )
		{
					
			i++;
			if (line.matches(evpr))
			{
				foundEvpr = true;
				
				line= line.replaceAll(",", ""); // remove extra , at the end of line.
				
				String[] words = line.split(" ");
				String sd = words[2];
				
				String ed = words[4];
				try
				{
					Date startDate = Date.valueOf(sd);
					Date endDate = Date.valueOf(ed);
					if(!startDate.toString().equals(sd) || !endDate.toString().equals(ed))
					{
						System.out.println("Invalid Start Date or End Date input at line "+i+" !");
						System.exit(-1);
					}
					
					this.input.setStartDate(startDate);
					this.input.setEndDate(endDate);
					
				}catch (IllegalArgumentException e)
				{
					System.out.println("Illegal Start Date or End Date input at line "+i+" !");
					System.exit(-1);
				}
			}
		}
		
		if (!foundEvpr)
		{
			System.out.println("Evaluation period not found !");
			System.exit(-1);
		}
		
		//find column head line
		while (!foundCh && (line = br.readLine())!=null )
		{
			i++;
			if (line.matches(colh)) foundCh = true;
		}
		
		if (!foundCh)
		{
			System.out.println("Column head not found !");
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
				System.out.println("Invalid input at line " + i + ", please verify you input first!");
				System.exit(-2);
			}
			
			String d = words[0].trim();
			
			try
			{
				Date transDate = Date.valueOf(d);
				double mv = Double.parseDouble(words[1]);
						
				if(!transDate.toString().equals(d))
				{
					System.out.println("Invalid transaction Date input at line "+i+" !");
					System.exit(-1);
				}
				
				if (last != null && !transDate.after(last))
				{
					System.out.println("Transaction Date input at line "+i+" is not after the date in previous line!");
					System.exit(-1);
				}else
				{
					last = transDate;
				}
				

				r.setDate(transDate);
				r.setMv(mv);
				
			}catch (NumberFormatException e)
			{
				System.out.println("Invalid market value input at line "+i+" !");
				System.exit(-1);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Invalid transaction Date input at line "+i+" !");
				System.exit(-1);
			}
			
			//cash flow
			if (length == 3 )
			{
				String w = words[2].trim();
				if (w.trim().length() == 0)
				{
					r.setCf(0);
				}else
				{
					try
					{
						double cf = Double.parseDouble(w);
						r.setCf(cf);
					}catch (NumberFormatException e)
					{
						System.out.println("Invalid cach flow input at line "+i+" !");
						System.exit(-1);
					}
				}
				r.setAf(0);
				r.setBm(0);
			}
			
			//agent fee
			if (length == 4 )
			{
				String w = words[3].trim();
				if (w.trim().length() == 0)
				{
					r.setAf(0);
				}else
				{
					try
					{
						double cf = Double.parseDouble(w);
						r.setAf(cf);
					}catch (NumberFormatException e)
					{
						System.out.println("Invalid agent fee input at line "+i+" !");
						System.exit(-1);
					}
				}
				
				r.setBm(0);
			}
			
			//Bench mark
			if (length > 4 )
			{
				String wi = words[4].trim();
				
				//remove the ',' and trim the white space
				wi = wi.replaceAll(",", "").trim();
	
				if (wi.length() == 0)
				{
					r.setBm(0);
				}else
				{
					if (!wi.endsWith("%"))
					{
						System.out.println("Oops! Are you sure you have % for your benchmark input at line "+i+" ?");
						System.exit(-1);
					}
					String w = wi.substring(0, wi.length() - 1);
					
					try
					{
						double cf = Double.parseDouble(w);
						r.setBm(cf/100);
					}catch (NumberFormatException e)
					{
						System.out.println("Invalid benchmark input at line "+i+" !");
						System.exit(-1);
					}
				}
			}
			rows.add(r);
		}
		
		
		
		//test if there is empty in side file or at the end of file.
		if (line !=null && line.replaceAll(",","").trim().isEmpty())
		{
			i++;
			System.out.println("Please remove empty line at line "+i+" !");
			System.exit(-1);
		}
		
		if (!this.rows.isEmpty())
		{
			this.validateDatesOrder();
		}

		this.input.setRows(this.rows);	
	}

/**
 * 
 */
	private void validateDatesOrder() {
		
		Date dLast = this.rows.get(0).getDate();
		
		Date dCurrent;
		boolean validOrder = true;
		
		for (int i = 1; i < this.rows.size() && validOrder; i++)
		{
			dCurrent = this.rows.get(i).getDate();
			validOrder = dCurrent.after(dLast);
		}
		
	}


	/**
	 * @return the input
	 */
	public Input getInput() {
		return input;
	}
	
	

}
