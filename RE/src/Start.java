import java.io.File;
import java.io.IOException;
import java.sql.Date;


public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 1)
		{
			System.out.println("Invalid number of commandline arguments!\n" + 
					"Usage: java -jar roi.jar csvinput\n");
			System.exit(-1);
		}
		
		ReadFile rf = new ReadFile(args[0]);
		
		try {
			rf.readFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error opening file, please verify your file name and location, thanks.!");
			System.exit(-2);
			
		}
		
		Input in = rf.getInput();
		
		Roi roi = new Roi(in);
		roi.generateReport();
		
		System.out.println(roi.getReport());
		
		System.out.printf("Evaluation period: start date %s end date %s%n",in.getStartDate(), in.getEndDate() );
		for (Row r: in.getRows())
		{
			System.out.printf("%s %.2f %.2f %.2f %.3f %n", r.getDate(), r.getMv(), r.getCf(), r.getAf(), r.getBm());
		}
	}

}
