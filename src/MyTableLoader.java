import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class MyTableLoader {

	public static HashMap<String, HashMap<String, ArrayList<Integer>>> tablesHashMap = new HashMap<>(); // table names
																										// in keys and
																										// table values
																										// in
																										// values(Column
																										// names(keys)
																										// and its
																										// values(values))
	public static Map<String, ArrayList<String>> tableMetaHM = new HashMap<String, ArrayList<String>>();

	public static void loadTablesStructure() throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "/src/files/metadata.txt"));
		String curr = br.readLine();
		String table_name, next;
		while (curr != null) {
			if (curr.equals("<begin_table>")) {
				table_name = br.readLine();
				next = br.readLine();
				ArrayList<String> cols = new ArrayList<String>();
				while (!next.equals("<end_table>")) {
					cols.add(next);
					// value for tablesHashMap
					HashMap<String, ArrayList<Integer>> tableDataHM = new HashMap<String, ArrayList<Integer>>();
					ArrayList<Integer> colValues = new ArrayList<Integer>();

					tableDataHM.put(next, colValues);// placing empty values in cols
					if (tablesHashMap.get(table_name) == null) {
						tablesHashMap.put(table_name, tableDataHM);
					}
					if (!tablesHashMap.get(table_name).containsKey(next)) {
						tablesHashMap.get(table_name).put(next, colValues);
					}

					// System.out.println(data);
					next = br.readLine();

				}
				tableMetaHM.put(table_name, cols);
			}
			curr = br.readLine();
		}
		br.close();
	}

	public static void loadDataToStructure() throws IOException {
		// TODO Auto-generated method stub
		BufferedReader bufferedReader;
		String curr;
		int count=0;
		
		for(Entry<String, ArrayList<String>> entry:tableMetaHM.entrySet())
      	{
			
           ArrayList<Integer> dataArray;   
           bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/files/"+entry.getKey()+".csv"));
           List<String> colList = entry.getValue();	//list of cols of table
           Map<String,ArrayList<Integer>> tempColDataHM=new HashMap<String,ArrayList<Integer>>();
           tempColDataHM=tablesHashMap.get(entry.getKey());
           
           curr=bufferedReader.readLine();
           while(curr!=null)
            {
            	StringTokenizer st = new StringTokenizer(curr,",");
            	count=0;
            	while(st.hasMoreElements())
				{
					String token = st.nextToken();
					dataArray=tempColDataHM.get(colList.get(count));
					if(dataArray==null)
						dataArray=new ArrayList<Integer>();
					
					if(token.startsWith("\""))
					{
						token = token.substring(1);
						token=token.substring(0, token.length()-1);
						int num = Integer.parseInt(token);
						dataArray.add(num);
						tablesHashMap.get(entry.getKey()).put(colList.get(count),dataArray);
					}
					else
					{
						int num = Integer.parseInt(token);
						dataArray.add(num);
						tablesHashMap.get(entry.getKey()).put(colList.get(count),dataArray);	
					}
					count++;
				}
				 curr=bufferedReader.readLine();
            }
            bufferedReader.close();
      	}
		for(Object tableName : tablesHashMap.keySet().toArray())
		{
			//printTable(tablesHashMap.get(tableName));
		}
	}

	static void printTable(HashMap<String, ArrayList<Integer>> tableToPrintHM) {
		// TODO Auto-generated method stub
		System.out.println("------------Result-----------");
		System.out.println(" ");
		
		int noOfRows =0;
		//printing col names
		for(Object colName : tableToPrintHM.keySet().toArray())
		{
			if(noOfRows==0)
			{
				noOfRows = tableToPrintHM.get(colName).size();
			}
			System.out.print(colName+"\t");
		}
		System.out.print("\n");
		
		for(int i=0;i<noOfRows;i++)
		{
			for(Object colName : tableToPrintHM.keySet().toArray())
			{
				System.out.print(tableToPrintHM.get(colName).get(i)+"\t");
			}
			System.out.print("\n");
		}
		
	}

}
