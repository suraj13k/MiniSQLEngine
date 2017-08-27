import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class MySQLParser {
	
	private Statement parsedStatement; 
	
	public MySQLParser(String query) throws JSQLParserException
	{
		parsedStatement = CCJSqlParserUtil.parse(query);
	}
	
	public Statement getParsedStatement() {
		return parsedStatement;
	}
	
	public List<String> getTableNames()
	{
		Select selectStatement = (Select) parsedStatement;
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
		return tableList;
	}
	
	
}
