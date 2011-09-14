package org.jwall.sql.audit;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import stream.data.TreeNode;


/**
 * This class implements a stateful tree walk that simultaneously builds up a
 * tree based on TreeNode objects, while traversing the abstract syntax tree
 * created by the JSqlParser SQL parser.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class JSqlASTWalker 
    extends ExpressionTreeWalker
    implements StatementVisitor, SelectVisitor, SelectItemVisitor, FromItemVisitor, ItemsListVisitor, OrderByVisitor 
{
    TreeNode root;


    public JSqlASTWalker(){
        root = dive( "Statement" );
    }

    public JSqlASTWalker( TreeNode root ){
        this.root = root;
    }


    /*
     * @see org.jwall.sql.audit.TreeWalker#leaf(java.lang.Object)
     */
    public TreeNode leaf( Object o ){
        TreeNode node = new ASTNode( o.toString() );
        stack.peek().addChild( node );
        return node;
    }

    public TreeNode dive( String str ){
        TreeNode node = new ASTNode( str );
        if( ! stack.isEmpty() )
            stack.peek().addChild( node );
        stack.push( node );
        return node;
    }

    public void up(){
        if( ! stack.isEmpty() )
            stack.pop();
    }



    public void visit(PlainSelect plainSelect) {

        //dive( "PLAIN_SELECT" );

        dive( "ResultColList" );
        List<?> selList = plainSelect.getSelectItems();
        for( int i = 0; i < selList.size(); i++ ){
            Object o = selList.get( i );
            SelectItem sei = (SelectItem) o;
            sei.accept( this );
        }
        up();

        dive( "FromList" );
        plainSelect.getFromItem().accept(this);
        up();

        //up();

        if (plainSelect.getJoins() != null) {
            dive( "JOIN_LIST" );

            for (Iterator<?> joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
                Join join = (Join) joinsIt.next();
                dive( "JOIN_LIST_ITEM" );
                join.getRightItem().accept(this);
                up();
            }

            up();
        }

        if (plainSelect.getWhere() != null){
            dive( "WhereClause" );
            plainSelect.getWhere().accept(this);
            up();
        }

        if( plainSelect.getGroupByColumnReferences() != null ){
            dive( "GroupBy" );
            List<?> groups = plainSelect.getGroupByColumnReferences();
            for( int i = 0; i < groups.size(); i++ ){
                Object o = groups.get(i);
                if( o instanceof Column ){
                    Column col = (Column) o;
                    dive( "ColRef" );
                    leaf( col.getColumnName() );
                    up();
                }

                /*
                if( o ColumnIndex ){
                    ColumnIndex idx = (ColumnIndex) o;
                    dive( "ColIdx" );
                    leaf( idx.getIndex() + "" );
                    up();
                }
                 */

                /*
                ColumnReference colRef = (ColumnReference) groups.get( i );
                dive( "ColRef" );
                leaf( colRef.getColumnName() );
                up();
                 */
            }
            up();
        }


        if( plainSelect.getOrderByElements() != null ){

            List<?> order = plainSelect.getOrderByElements();
            if( order.size() > 0 ){
                dive( "OrderBy" );
                for( int i = 0; i < order.size(); i++ ){
                    OrderByElement orderBy = (OrderByElement) order.get(i);
                    orderBy.accept( this );
                }
                up();
            }
        }
    }

    public void visit( Select select ){
        dive( "SELECT" );
        select.getSelectBody().accept( this );
        up();
    }


    public void visit(Union union) {
        dive( "UNION" );

        for (Iterator<?> iter = union.getPlainSelects().iterator(); iter.hasNext();) {
            PlainSelect plainSelect = (PlainSelect) iter.next();
            visit(plainSelect);
        }

        up();
    }

    public void visit(Table tableName) {
        dive( "TableRef" );
        leaf( tableName.getName() );
        up();
    }

    public void visit(SubSelect subSelect) {
        dive( "SubSelect" );
        subSelect.getSelectBody().accept(this);
        up();
    }

    public void visit(ExpressionList expressionList) {
        //dive( "Expressions" );
        for (Iterator<?> iter = expressionList.getExpressions().iterator(); iter.hasNext();) {
            Expression expression = (Expression) iter.next();
            expression.accept(this);
        }
        //up();
    }

    public void visit(AllComparisonExpression allComparisonExpression) {
        allComparisonExpression.GetSubSelect().getSelectBody().accept(this);
    }

    public void visit(AnyComparisonExpression anyComparisonExpression) {
        anyComparisonExpression.GetSubSelect().getSelectBody().accept(this);
    }

    public void visit(SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        subjoin.getJoin().getRightItem().accept(this);
    }

    @Override
    public void visit(Delete arg0)
    {
        dive( "DELETE" );
        arg0.getTable().accept( this );
        dive( "WhereClause" );
        arg0.getWhere().accept( this );
        up();
        up();
    }

    @Override
    public void visit(Update arg0)
    {
        dive( "UPDATE" );
        arg0.getTable().accept( this );
        List<?> cols = arg0.getColumns();
        if( cols != null ){
            for( int i = 0; i < cols.size(); i++ ){
                Column col = (Column) cols.get(i);
                col.accept( this );
            }
        }
        
        List<?>items = arg0.getExpressions();
        if( items != null ){
            dive( "Items");
            for( int i = 0; i < items.size(); i++ ){
                Expression exp = (Expression) items.get( i );
                exp.accept( this );
            }
            up();
        }
        up();
    }

    @Override
    public void visit(Insert arg0)
    {
        dive( "INSERT" );
        arg0.getTable().accept( this );
        List<?> cols = arg0.getColumns();
        if( cols != null ){
            for( int i = 0; i < cols.size(); i++ ){
                Column col = (Column) cols.get(i);
                col.accept( this );
            }
        }
        
        ItemsList list = arg0.getItemsList();
        if( list != null ){
            dive( "Values" );
            list.accept( this );
            up();
        }
        
        up();
    }

    @Override
    public void visit(Replace arg0)
    {
        dive( "REPLACE" );
        
        arg0.getTable().accept( this );
        List<?> cols = arg0.getColumns();
        if( cols != null ){
            for( int i = 0; i < cols.size(); i++ ){
                Column col = (Column) cols.get(i);
                col.accept( this );
            }
        }
        
        ItemsList list = arg0.getItemsList();
        if( list != null ){
            dive( "Values" );
            list.accept( this );
            up();
        }
        
        up();
    }

    @Override
    public void visit(Drop arg0)
    {
        dive( "DROP" );
        List<?> params = arg0.getParameters();
        if( params != null ){
            for( int i = 0; i < params.size(); i++ ){
                leaf( params.get( i) + "" );
            }
        }
        up();
    }

    @Override
    public void visit(Truncate arg0)
    {
        dive( "TRUNCATE" );
        arg0.getTable().accept( this );
        up();
    }

    @Override
    public void visit(CreateTable arg0)
    {
        dive( "CREATE TABLE" );
        arg0.getTable().accept( this );

        List<?> colDefs = arg0.getColumnDefinitions();
        if( colDefs != null ){
            for( int i = 0; i < colDefs.size(); i++ ){
                ColumnDefinition def = (ColumnDefinition) colDefs.get( i );
                dive( "ColDef" );
                leaf( "" + def.getColumnName() );
                leaf( "" + def.getColDataType().getDataType() );
                up();
            }
        }
        up();
    }


    public TreeNode getAST(){
        return root;
    }


    @Override
    public void visit(AllColumns arg0)
    {
        dive( "ColRef" );
        leaf( "*" );
        up();
    }

    @Override
    public void visit(AllTableColumns arg0)
    {
        leaf( arg0.getTable().getName() + ".*" );
    }

    @Override
    public void visit(SelectExpressionItem arg0)
    {
        arg0.getExpression().accept( this );
    }


    public static TreeNode createAST( String sql ) throws Exception {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        Statement stmt = pm.parse( new StringReader( sql ) );

        JSqlASTWalker astWalker = new JSqlASTWalker();
        stmt.accept( astWalker );

        TreeNode ast = astWalker.getAST();
        return ast;
    }

    public static void printAST( String sql ) throws Exception {
        System.out.println( "Statement: " + sql );
        System.out.println( "  => " + ( createAST( sql ) ) );
    }


    public static void main( String[] args ) throws Exception {
        try {
            String sql = "SELECT NAME,CREDIT FROM STUDENTS WHERE NAME = 'Robert';";

            TreeNode ast = createAST( sql );
            System.out.println( ast );

            printAST( "SELECT * FROM STUDENTS WHERE CREDITS > 10 AND ID = 9423" );

            printAST( "SELECT * FROM STUDENTS WHERE CREDITS > 10 AND ID = 9423 OR ( EXP(0) + 3 > 2 * CREDITS )" );

            printAST( "SELECT SUM(CREDITS) FROM STUDENTS GROUP BY NAME, CLASS ORDER BY NAME, EXP(ID), AGE" );

            printAST( "CREATE TABLE USERS ( ID INTEGER, NAME VARCHAR(255) )" );
            
            printAST( "SELECT * FROM STUDENTS WHERE ID = ( SELECT ID FROM PROD WHERE NAME = 'Karl' )" );
            
            printAST( "SELECT profit FROM Sale2010 WHERE month = 8 UNION SELECT profit from Sale2011 WHERE month = 8" );
            
            printAST( "SELECT uid,pid,title,description FROM tx_foo_record WHERE deleted = 0 AND tstamp > 3 UNION SELECT uid,pid,username,password FROM admin_users WHERE deleted = 0 AND admin = 1" );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(OrderByElement arg0)
    {
        //dive( "OrderByElement" );
        arg0.getExpression().accept( this );
        //up();
    }
}