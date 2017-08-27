import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.jsqlparser.JSQLParserException;

public class SQLEngine {
	
	String[] DMLArray = {"select","update","delete"};
	
	
	public static void main(String args[]){ 
		
		//load tables to datastructure from csv files
		try {
			MyTableLoader.loadTablesStructure();
			MyTableLoader.loadDataToStructure();
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("Tables loaded to data structure successfully");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		String query = args[0];
		if(query.length()>0)
		{
			try {
				MySQLParser mySQLParser = new MySQLParser(query);
				System.out.println("query being executed : "+ query);
				
				MySQLExecutor.executeStatement(mySQLParser.getParsedStatement());

			} catch (JSQLParserException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Invalid query.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("This is the only functionality left. Please ignore");
			}
		}
		else
		{
			System.out.println("please enter query.");
		}
		
	}  

}
