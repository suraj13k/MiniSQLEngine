import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class MySQLExecutor {

	static HashMap<Table, List<SelectItem>> selectedTablesAndCols = new HashMap<>();

	public MySQLExecutor() {

	}

	public static void executeStatement(Statement statement) throws Exception
	{
		boolean joinsPresent = false;
		if(statement instanceof Select)
		{
			Select selectStatement = (Select)statement;
			PlainSelect plainSelect = (PlainSelect)selectStatement.getSelectBody();
			Table fromTable = (Table)plainSelect.getFromItem();
			List<Join> joins = plainSelect.getJoins();
			
			List<SelectItem> selectedColumns = plainSelect.getSelectItems();
			System.out.println("Selected table name :" + fromTable.getName() +" with alias "+fromTable.getAlias() );
			
			
			
			if(joins!=null)
			{
				joinsPresent = true;
				for (Join join : joins) {
					//System.out.println("Joined table: "+ ((Table)join.getRightItem()).getName() +" with alias "+((Table)join.getRightItem()).getAlias());
				}
			}
			
			if(!joinsPresent)
			{
				selectedTablesAndCols.put(fromTable, selectedColumns);
				getFinalTable(plainSelect,null);
			}
			else
			{
				selectedTablesAndCols.put(fromTable, selectedColumns);
				try
				{
					selectedTablesAndCols.put(((Table)joins.get(0).getRightItem()), selectedColumns);
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				for (SelectItem selectItem : selectedColumns) {
						//System.out.print(((Column)((SelectExpressionItem)selectItem).getExpression()).getColumnName() + " from "+((Column)((SelectExpressionItem)selectItem).getExpression()).getTable().getName()+", ");
				}
				
				HashMap<String, ArrayList<Integer>> joinedTable = new HashMap<>();
				Table table1 = fromTable;
				Table table2 = ((Table)joins.get(0).getRightItem());
				joinedTable.putAll(MyTableLoader.tablesHashMap.get(table1.getName()));
				
				for(Object col: MyTableLoader.tablesHashMap.get(table2.getName()).keySet().toArray())
				{
					joinedTable.put((String) col , MyTableLoader.tablesHashMap.get(table2.getName()).get(col));
				}
				
				
				getFinalTable(plainSelect,joinedTable);
				
			}
			
			//select * from table1 
			
			
			
		}
		else
		{
			System.out.println("Cannot support other queries other than select");
		}
	}

	

	private static void getFinalTable(PlainSelect plainSelect, HashMap<String, ArrayList<Integer>> joinedTable) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<Integer>> finalResultTable = new HashMap<>();
		
		if(joinedTable==null)
		{
			for(Object table : selectedTablesAndCols.keySet().toArray())
			{
				if(MyTableLoader.tablesHashMap.containsKey(((Table) table).getName()))
				{
					finalResultTable =  new HashMapOfArrayList((HashMap<String, ArrayList<Integer>>) MyTableLoader.tablesHashMap.get(((Table)table).getName())).getGraph();
					finalResultTable = new HashMapOfArrayList((HashMap<String, ArrayList<Integer>>) applyWhere(plainSelect.getWhere(),finalResultTable)).getGraph();
							
					//remove non selected cols if any
					for(Object colName : finalResultTable.keySet().toArray())
					{
						boolean isColSelected = false;
						for(Object colNameSelected : selectedTablesAndCols.get(table))
						{
							try
							{
								Column selectedCol = getColumnSelectedFromExpression( ((SelectExpressionItem)colNameSelected).getExpression());
								if((((String)colName).equalsIgnoreCase(selectedCol.getColumnName())))
								{
									isColSelected = true;
									break;
								}
							}
							catch(Exception e)
							{
								isColSelected = true;
								break;
							}
						}
						
						if(!isColSelected)
						{
							finalResultTable.remove(colName);
						}
						
					}
				}
				else
				{
					System.out.println("selected table does not exists");
				}
				
				
			}
		}
		else
		{
			finalResultTable =  new HashMapOfArrayList(joinedTable).getGraph();
			finalResultTable = new HashMapOfArrayList((HashMap<String, ArrayList<Integer>>) applyWhere(plainSelect.getWhere(),finalResultTable)).getGraph();
					
			//remove non selected cols if any
			for(Object colName : finalResultTable.keySet().toArray())
			{
				boolean isColSelected = false;
				for(Object colNameSelected : selectedTablesAndCols.get(selectedTablesAndCols.keySet().toArray()[0]))
				{
					try
					{
						Column selectedCol = getColumnSelectedFromExpression( ((SelectExpressionItem)colNameSelected).getExpression());
						if((((String)colName).equalsIgnoreCase(selectedCol.getColumnName())))
						{
							isColSelected = true;
							break;
						}
					}
					catch(Exception e)
					{
						isColSelected = true;
						break;
					}
				}
				
				if(!isColSelected)
				{
					finalResultTable.remove(colName);
				}
				
			}
		}
		
		
		finalResultTable = applyAggregateFunctionOnSelectedColIfAny(selectedTablesAndCols.get(selectedTablesAndCols.keySet().toArray()[0]),finalResultTable);
		MyTableLoader.printTable(finalResultTable);
		
	}
	
	private static HashMap<String, ArrayList<Integer>> applyAggregateFunctionOnSelectedColIfAny(List<SelectItem> list,
			HashMap<String, ArrayList<Integer>> finalResultTable) {
		// TODO Auto-generated method stub
		SelectItem item = list.get(0);
		if(item instanceof SelectExpressionItem)
		{
			SelectExpressionItem selectExpressionItem = (SelectExpressionItem)item;
			Expression expression = selectExpressionItem.getExpression();
			if(expression instanceof Function)
			{
				Function selectedFunction = (Function)expression;
				finalResultTable =applyFunctionOnCol(finalResultTable,selectedFunction);
				
			}
			else if(expression instanceof Parenthesis)
			{
				Function fn =new Function();
				fn.setName("Distinct");
				finalResultTable =applyFunctionOnCol(finalResultTable,fn);
			}
		}
		return finalResultTable;
	}

	private static HashMap<String, ArrayList<Integer>> applyFunctionOnCol(
			HashMap<String, ArrayList<Integer>> finalResultTable, Function selectedFunction) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<Integer>> resultTableAfterApplyingFunction = new HashMap<>();
		
		resultTableAfterApplyingFunction.put(selectedFunction.getName(), (new ArrayListOfIntegers(finalResultTable.get(finalResultTable.keySet().toArray()[0]), selectedFunction.getName())).getAggregateValue());
		
		return resultTableAfterApplyingFunction;
	}

	private static Column getColumnSelectedFromExpression(Expression colNameSelected) {
		// TODO Auto-generated method stub
		//Column colObj=null;
		if(colNameSelected instanceof Column)
		   return (Column) colNameSelected;
		else if(colNameSelected instanceof Function)
			return getColumnSelectedFromExpression(((Function)colNameSelected).getParameters().getExpressions().get(0));
		else
		{
			return getColumnSelectedFromExpression(((Parenthesis)colNameSelected).getExpression());
		}
		
	}

	private static HashMap<String, ArrayList<Integer>> applyWhere(Expression exp, HashMap<String,ArrayList<Integer>> finalResultTableOriginal) throws Exception {
		
		HashMap<String,ArrayList<Integer>> finalResultTable = new HashMap<>();
		finalResultTable = new HashMapOfArrayList(finalResultTableOriginal).getGraph();
		
		ArrayList<Integer> indexesToRemove = new ArrayList<>();
		indexesToRemove.clear();
		if(exp instanceof EqualsTo)
		{
			
			EqualsTo equalTo = (EqualsTo)exp;
			String colName = ((Column)equalTo.getLeftExpression()).getColumnName();
			
			Integer comparedValue = (int)((LongValue)equalTo.getRightExpression()).getValue();
			
			for(int i=0;i < finalResultTable.get(colName).size();i++ )
			{
				Integer intValue = finalResultTable.get(colName).get(i);
				if(!intValue.equals(comparedValue))
				{
					indexesToRemove.add(i);
				}
			}
			
			
			
		}
		else if(exp instanceof GreaterThanEquals)
		{
			GreaterThanEquals greaterThanEquals = (GreaterThanEquals)exp;
			String colName = ((Column)greaterThanEquals.getLeftExpression()).getColumnName();
			Integer comparedValue = (int)((LongValue)greaterThanEquals.getRightExpression()).getValue();
			
			for(int i=0;i < finalResultTable.get(colName).size();i++ )
			{
				Integer intValue = finalResultTable.get(colName).get(i);
				if(!(intValue >= comparedValue))
				{
					indexesToRemove.add(i);
				}
			}
		}
		else if(exp instanceof GreaterThan)
		{
			GreaterThan greaterThan = (GreaterThan)exp;
			String colName = ((Column)greaterThan.getLeftExpression()).getColumnName();
			Integer comparedValue = (int)((LongValue)greaterThan.getRightExpression()).getValue();
			
			for(int i=0;i < finalResultTable.get(colName).size();i++ )
			{
				Integer intValue = finalResultTable.get(colName).get(i);
				if(!(intValue > comparedValue))
				{
					indexesToRemove.add(i);
				}
			}
		}
		else if(exp instanceof NotEqualsTo)
		{
			NotEqualsTo notEqualTo = (NotEqualsTo)exp;
			String colName = ((Column)notEqualTo.getLeftExpression()).getColumnName();
			Integer comparedValue = (int)((LongValue)notEqualTo.getRightExpression()).getValue();
			
			for(int i=0;i < finalResultTable.get(colName).size();i++ )
			{
				Integer intValue = finalResultTable.get(colName).get(i);
				if(intValue.equals(comparedValue))
				{
					indexesToRemove.add(i);
				}
			}
		}
		else if(exp instanceof MinorThan)
		{
			MinorThan minorThan = (MinorThan)exp;
			String colName = ((Column)minorThan.getLeftExpression()).getColumnName();
			Integer comparedValue = (int)((LongValue)minorThan.getRightExpression()).getValue();
			
			for(int i=0;i < finalResultTable.get(colName).size();i++ )
			{
				Integer intValue = finalResultTable.get(colName).get(i);
				if(!(intValue < comparedValue))
				{
					indexesToRemove.add(i);
				}
			}
		}
		else if(exp instanceof MinorThanEquals)
		{
			MinorThanEquals minorThanEquals = (MinorThanEquals)exp;
			String colName = ((Column)minorThanEquals.getLeftExpression()).getColumnName();
			Integer comparedValue = (int)((LongValue)minorThanEquals.getRightExpression()).getValue();
			
			for(int i=0;i < finalResultTable.get(colName).size();i++ )
			{
				Integer intValue = finalResultTable.get(colName).get(i);
				if(!(intValue <= comparedValue))
				{
					indexesToRemove.add(i);
				}
			}
		}
		else if(exp instanceof AndExpression)
		{
			AndExpression andExpression = (AndExpression)exp;
			finalResultTable = applyWhere(andExpression.getLeftExpression(), finalResultTable);
			finalResultTable = applyWhere(andExpression.getRightExpression(), finalResultTable);
		}
		else if(exp instanceof OrExpression)
		{
			OrExpression orExpression = (OrExpression)exp;
			HashMap<String,ArrayList<Integer>> finalResultTable1 = new HashMap<>();
			HashMap<String,ArrayList<Integer>> finalResultTable2 = new HashMap<>();
			HashMap<String,ArrayList<Integer>> finalResultTableCopy = new HashMap<>();
			finalResultTableCopy= new HashMapOfArrayList(finalResultTable).getGraph();
			
			finalResultTable1 = new HashMapOfArrayList(applyWhere(orExpression.getLeftExpression(), finalResultTableCopy)).getGraph();
			finalResultTable2 = new HashMapOfArrayList(applyWhere(orExpression.getRightExpression(), finalResultTableCopy)).getGraph();
			
			finalResultTable= new HashMapOfArrayList(mergeTwoTables(finalResultTable1,finalResultTable2)).getGraph();
		}
		else 
		{
			//System.out.println("Selected where not supported yet!! try simpler ones ;)");
		}
		
		Collections.sort(indexesToRemove, Collections.reverseOrder());

		for(Object colObj : finalResultTable.keySet().toArray())
		{
			for(int indexToBeRemoved : indexesToRemove)
			{
				finalResultTable.get((String)colObj).remove(indexToBeRemoved);
			}
		}
		
		
		return finalResultTable;
		// TODO Auto-generated method stub
		
	}

	private static HashMap<String, ArrayList<Integer>> mergeTwoTables(
			HashMap<String, ArrayList<Integer>> finalResultTable1,
			HashMap<String, ArrayList<Integer>> finalResultTable2) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<Integer>> finalResultTable = new HashMap<>();
		finalResultTable = new HashMapOfArrayList(finalResultTable1).getGraph();
		
		for(Object colObj : (new HashMapOfArrayList(finalResultTable2).getGraph()).keySet().toArray())
		{
			if(finalResultTable.containsKey(colObj))
			{
				finalResultTable.get(colObj).addAll(finalResultTable2.get(colObj));
			}
		}
		//finalResultTable.putAll();
		
		
		return removeDuplicates(finalResultTable);
	}

	private static HashMap<String, ArrayList<Integer>> removeDuplicates(
			HashMap<String, ArrayList<Integer>> finalResultTable) {
		// TODO Auto-generated method stub
		ArrayList<String> touplesArrayList = getTouplesForTable(finalResultTable);
		ArrayList<Integer> indexesToRemove = new ArrayList<>();
		for(int i=0;i<touplesArrayList.size();i++)
		{
			for(int j=i+1;j<touplesArrayList.size();j++)
			{
				if(touplesArrayList.get(i).equals(touplesArrayList.get(j)))
				{
					if(!indexesToRemove.contains(j))
						indexesToRemove.add(j);
				}
			}
		}
		
		
		Collections.sort(indexesToRemove, Collections.reverseOrder());

		for(Object colObj : finalResultTable.keySet().toArray())
		{
			for(int indexToBeRemoved : indexesToRemove)
			{
				finalResultTable.get((String)colObj).remove(indexToBeRemoved);
			}
		}
		
		return finalResultTable;
	}

	private static ArrayList<String> getTouplesForTable(HashMap<String, ArrayList<Integer>> finalResultTable) {
		// TODO Auto-generated method stub
		ArrayList<String> touplesArrayList = new ArrayList<>();
		int noOfRows = 0;
		try
		{
			noOfRows = finalResultTable.get(finalResultTable.keySet().toArray()[0]).size();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		for(int i=0;i<noOfRows;i++)
		{
			String touple = "";
			for(Object colObj : finalResultTable.keySet().toArray())
			{
				touple = touple.concat(finalResultTable.get(colObj).get(i)+"");
			}
			touplesArrayList.add(touple);
		}
		return touplesArrayList;
	}
	
	

}
