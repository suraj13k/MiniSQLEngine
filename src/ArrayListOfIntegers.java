import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayListOfIntegers {

	ArrayList<Integer> colValues;
	String selectedFunction;
	public ArrayListOfIntegers(ArrayList<Integer> colValues,String function)
	{
		this.colValues = colValues;
		this.selectedFunction = function;
	}
	
	private int getSum()
	{
		int sum=0;
		for(Integer integer : colValues)
		{
			sum=+integer;
		}	
		return sum;
	}
	
	private int getAverage()
	{
		int sum = getSum();
		int noOfRows = colValues.size();
		int average = sum/noOfRows;
		return average;
	}
	
	private int getMax() {
		Collections.sort(colValues, new Comparator<Integer>() {
		    public int compare(Integer o1, Integer o2) {
		        return o2.compareTo(o1);
		    }
		});
		return colValues.get(0);
	}
	
	private int getMin() {
		Collections.sort(colValues, new Comparator<Integer>() {
		    public int compare(Integer o1, Integer o2) {
		        return o1.compareTo(o2);
		    }
		});
		return colValues.get(0);
	}
	
	
	private ArrayList<Integer> removeDuplicates()
	{
		// add elements to al, including duplicates
		Set<Integer> hs = new HashSet();
		hs.addAll(colValues);
		colValues.clear();
		colValues.addAll(hs);
		return colValues;
	}
	
	public ArrayList<Integer> getAggregateValue()
	{
		ArrayList<Integer> result = new ArrayList<>();
		if(selectedFunction.equalsIgnoreCase("sum"))
		{
			result.add(getSum());
		}
		else if(selectedFunction.equalsIgnoreCase("average"))
		{
			result.add(getAverage());
		}
		else if(selectedFunction.equalsIgnoreCase("min"))
		{
			result.add(getMin());
		}
		else if(selectedFunction.equalsIgnoreCase("max"))
		{
			result.add(getMax());
		}
		else if(selectedFunction.equalsIgnoreCase("distinct"))
		{
			result.addAll(removeDuplicates());
		}
		return result;
	}
	
}
