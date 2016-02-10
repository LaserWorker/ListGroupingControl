/**
*   By:Luke Burgess
*   Second Try A Beta Program
*   To Run Type: "ListGroupGenerator [File Name] [Optional Salary Limit]"
*/
import java.io.*;
import java.util.*;

public class ListGroupGenerator { 
	
	//Incase Error cannot be given or known.
	public static String ProgramError = "Unknown"; 
//One line of CSV values, Parsed into the program, named "Individual".
	public static class Individual { 	 
		private String name;	 //NAME
		private String dob;	 //DOB
		public double salary;	 //ANNUAL_SALARY
		public String role;	 //ROLE
// -------------v---------------constructors--------------v
		public Individual(String nm, String db, double slr, String rl) {
			name = nm;
			dob = db;
			salary = slr;
			role = rl;
//Exact-----------------constructor done------------------^ May not use...
		}
		public Individual() {
			name = "Unknown";
			dob = "Unknown";
			salary = 0;
			role = "ROLE";
//Default-----------------constructor done----------------^ May not use...
		}
		public Individual(String Input) {
			// Parse string into Array
			String[] SplitInput = Input.split(",");
			name = SplitInput[0];// "Private Data"; // SplitInput[0]; //Pick
			dob = SplitInput[1]; // "Private Data"; // SplitInput[1]; //One
			salary = 0;
			int c = 2;
			// Try to make SplitInput[2](the salary) into type "double".
			while(SplitInput.length > c){
				if ((c > 2) && (c < SplitInput.length)) {
				//add current SplitInput value to the salary String
					SplitInput[2] = SplitInput[2] + SplitInput[c];
				} //convert basic shorthand 
				SplitInput[2] = SplitInput[2].replace("K", "000");
				try {
				//If the current SplitInput value can be double make it salary
					salary = Double.parseDouble(SplitInput[2]);
				}catch (NumberFormatException ex) {
				//If SplitInput value was not capable of double enter it as role 
					role = SplitInput[c];
				}
				c++;
			}
//String CSV->Array input-----------------constructor done------------------^ Ready for use
		}
	}

	// List of Roles, IDs and what will be the Sum of Salaries for given role/ID
	public static List<String> RoleGroups = new ArrayList<String>();
	public static List<Double> SalarySums = new ArrayList<Double>();
	public static List<Integer> IDs = new ArrayList<Integer>();

	//List of value sets(NOT a single Individual) to be sourced from the CSV file.
	public static List<Individual> Individuals = new ArrayList<Individual>();
	
	public static void main(String[] args) {
		//Incase correct arguments are not given or can be defined.
		if(null == args || 1 > args.length){
			ProgramError = "Usage: [File Name] [Optional Salary Limit]";
			System.out.println(ProgramError);
			ProgramError = "Not enough initial input arguments";
			System.out.println(ProgramError);
			return;
		}

		int Number_Of_Arguments = (args.length);
		//Initial Arguments map is required.
		String FileName = args[0];
		//FileName will be the path to CSV file.
		String Detail = args[(Number_Of_Arguments - 1)];   
	//The final detail, error or output if left unchanged.
		String Output = "No calculation type function executed.";
		//Final Output String.
		ProgramError = "No calculation type function executed.";
	//Get Number of Rows and Read CSV.
		int Number_Of_Rows = ReadCSVFile(FileName); 
			double L = 0;
		if (1 < args.length){
	//input parameter, salary limit
			try {
				L = Integer.parseInt(args[1]);
			}catch (NumberFormatException ex) {
				ProgramError = "Fault in salary limit provided.";
				System.out.println(ProgramError);
			}
		}else{
			ProgramError = "No salary limit provided, proceeding with program.";
			System.out.println(ProgramError);
		}

	//Print Output static sring calculations
		System.out.println(calculation_a_d(Number_Of_Rows));
		System.out.println(calculation_e(Number_Of_Rows, L));
	}

//Get Number of Rows and Read CSV
	public static int ReadCSVFile(String FileName){
		int NumRows = 0;
		String newline = "abc,abc";
		Individual newIndividual = new Individual();
		
		try {   // -Start Reading and Parsing CSV.
			BufferedReader parser = new BufferedReader(new FileReader(FileName));
			//read first line outside while loop.
			newline = parser.readLine();

			while ((newline != null) && (NumRows < 1073741824)) {
				//No rows greater than 2^30, buffer from 2^32 and ~1/7th world pop.
				//More importantly a list will start taking up 8Gigs of Ram by 2^30
				NumRows++;
				//Put line data into Individuals List
				newIndividual = new Individual(newline);
				Individuals.add(newIndividual);
				//Get next line of data
				newline = parser.readLine();
				//parser.nextLine(); <-Check if this is needed
				//if you are running this and NumRows reaches 2^30
			}
			//close reader throws error on my PC, I should investigate
			//BufferedReader.close();
		}catch (IOException ex) {
			ProgramError = "Could not read from CSV Document";
			ex.printStackTrace();
			System.out.println(ProgramError);
		}
		return NumRows;
	}
	public static String SalaryToString(double Salary, double Division){
		Salary = Salary/Division;
		String Output = "0";
		int l = String.valueOf(Math.round(Salary)).length()+1;
		try {  //Put converted Salary/Division into properly formatted string
			String form = "%" + l + ".2f";
			Output = String.format(form, Salary);
		}catch (NumberFormatException ex) {	//May never run, but just incase.
			Output = String.valueOf(Salary);
		} 
		return Output;
	}

	public static void CreateTimeTableRow(String GroupName, double TotalSalary){
		String tm[] = new String[7]; 
	//An Array of Time types and correct conversion value
		tm[6] = SalaryToString(TotalSalary, 31536000);
		tm[5] = SalaryToString(TotalSalary, 525600);
		tm[4] = SalaryToString(TotalSalary, 8760);
		tm[3] = SalaryToString(TotalSalary, 365);
		tm[2] = SalaryToString(TotalSalary, 52);
		tm[1] = SalaryToString(TotalSalary, 12);
		tm[0] = SalaryToString(TotalSalary, 1);
		String form = "%-14s %1s %7s %7s %9s %10s %11s %15s %n";
		System.out.format(form, GroupName, tm[6], tm[5], tm[4], tm[3], tm[2], tm[1], tm[0]);
	}

	// ----Total salary in dollars by time unit grouped by role
	public static String calculation_a_d(int N){
		int c = 0;
		int d = 0;
		double e = 0;
		double total = 0;
		Individual current = new Individual();
		while(c < N){
		//Get the individual corisponding to current count position "c".
			current = Individuals.get(c);
		//get and add role to list
			if (!RoleGroups.contains(current.role)) {
				RoleGroups.add(current.role);
				SalarySums.add(current.salary);
			}else{ //If Role Record already exists add salary value to it
				d = RoleGroups.indexOf(current.role);
				e = SalarySums.get(d) + current.salary;
				SalarySums.set(d, e); 
			}
			total = total + current.salary;
			c++;
		}
		RoleGroups.add("Total");
		SalarySums.add(total);

		String form = "%-14s %6s %6s %6s %9s %10s %11s %15s %n";
		String m[] = {"Earnings Per>","Second","Minute","Hour","Day","Week","Month","Year"};
		System.out.format(form, m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7]);

		for(d = 0; d < (RoleGroups.size()); d++){
			CreateTimeTableRow(RoleGroups.get(d), SalarySums.get(d));
		}
		return "End of List for parts 'a' through 'd' ...";
	}
/**
*
*Groupings of people whose sum of annual salary are as close to the input number (without going over)
*What kind of Groupings number of possible groups may exceed 2^(N-1). Use structured possibilities?
*A direct list will be out of range of a common PC. 1073741824 
*/
	public static String calculation_e(int N, double L){
		System.out.println( "Starting List for part 'e' ...");
		String form = "%-14s %6s %6s %6s %9s %10s %11s %15s %n";
		String m[] = {"Earnings Per>","Second","Minute","Hour","Day","Week","Month","Year"};

		SalarySums = new ArrayList<Double>();
		double SalaryTest = 0.0;
		double SalaryC = 0.0;
		double SalaryD = 0.0;
		List<Integer> BestGroupIDs = new ArrayList<Integer>();
		IDs.add(0);
		SalarySums.add(0.0); 

		if (0.01 > L){
			return "None or incorrect data given.";
		}

		for(int c = 0; c < N; c++){
			if(Individuals.get(c).salary < L){
				IDs.add(c);
				SalarySums.add(Individuals.get(c).salary);
	//Keep track of who is closest
				if(Individuals.get(c).salary > SalarySums.get(0)){
	//Get first closest known ID
					IDs.set(0, c);
					SalarySums.set(0, Individuals.get(c).salary);
					}
			}
		}//New value for N reflects new number of options.
		N = IDs.size();
		BestGroupIDs.add(IDs.get(0));

		if ((IDs.size() == SalarySums.size()) && (N < 32768)){
	//If N is bigger than 32768, the sulution to N*(N-1) is bigger than 2^30 
			int Number_Testable_Possibilities = N * (N-1);
		}else{
			return "Calculation 'e' function not resolvable";
		}
//This would best be done recursivly but I'm concerned about size of "IDs"
		for(int c = 1; c < IDs.size(); c++){
			for(int d = 1; d < IDs.size(); d++){
				SalaryC = Individuals.get(IDs.get(c)).salary;
				SalaryD = Individuals.get(IDs.get(d)).salary;
				SalaryTest = SalaryC + SalaryD;
				if(L < SalaryTest &&  SalaryC > SalarySums.get(0)){
					IDs.remove(c);
					SalarySums.remove(c);
				}
				if(L < SalaryTest &&  SalaryD > SalarySums.get(0)){
					IDs.remove(d);
					SalarySums.remove(d);
				}
				if((SalaryTest < L) && (SalarySums.get(0) < SalaryTest)){
					SalarySums.set(0, SalaryTest);
					BestGroupIDs.set(0, IDs.get(c));
					BestGroupIDs.add(IDs.get(d));
				}
			}
		}

/** Now that the lists have been mostly reduced in size a recursive operation...
*   Is a less scarry idea to me, Not that I understand why it should be. */
		int[] Groups = rec(IDs, SalarySums.get(0), L);
		SalaryD = 0.0; 
		for (int c = 0; c < Groups.length; c++){
			SalaryC = Individuals.get(Groups[c]).salary;
			CreateTimeTableRow(Integer.toString(Groups[c]), SalaryC);
			SalaryD = SalaryD + SalaryC;
		}
		CreateTimeTableRow("Total", SalaryD);
		return "End of Table for part 'e' ...";
	}

	public static int[] rec(List<Integer> Group_of_IDs, double closest, double L){
		double CurrentTotal = sum(Group_of_IDs);
		int[] end = IntListToArray(Group_of_IDs);
		List<Integer> G_IDs = Group_of_IDs;
		for (int c = 0; c < G_IDs.size(); c++){
			CurrentTotal = sum(G_IDs);
			if (CurrentTotal > L){
				G_IDs.remove(c);
				end = rec(G_IDs, closest, L);
			}else if ((CurrentTotal < L) && (closest < CurrentTotal)){
				closest = CurrentTotal;
				Group_of_IDs = G_IDs;
				end = IntListToArray(G_IDs);
			}else{
				G_IDs = Group_of_IDs;
			}
		}
		end = IntListToArray(G_IDs);
		CurrentTotal = sum(G_IDs);
		if (CurrentTotal > L){
			end = rec(Group_of_IDs, closest, L);
		}else{
			end = IntListToArray(Group_of_IDs);
		}
		
		return end;
	}
	public static int[] IntListToArray(List<Integer> list) {
		int[] out = new int[list.size()];
		for (int c = 0; c < list.size(); c++){
			out[c] = list.get(c);
		}
		return out;
	}


	public static double sum(List<Integer> list) {
		double sum= 0.0;
		for (int c = 0; c < list.size(); c++){
			sum = sum + Individuals.get(list.get(c)).salary;
		}
		return sum;
	}
}

