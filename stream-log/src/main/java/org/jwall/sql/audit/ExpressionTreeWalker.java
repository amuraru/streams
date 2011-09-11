package org.jwall.sql.audit;

import java.util.List;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import org.jwall.sql.parser.SQLTreeNode;

import stream.data.TreeNode;

public class ExpressionTreeWalker
    extends TreeWalker
    implements ExpressionVisitor, ItemsListVisitor
{

    /**
     * @see org.jwall.sql.audit.TreeWalker#create(java.lang.String)
     */
    @Override
    public TreeNode create(String label)
    {
        return new SQLTreeNode( label );
    }


    
    protected void visitBinaryExpression(BinaryExpression binaryExpression) {
        dive( binaryExpression.getStringExpression() + "" );
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
        up();
    }

    

    @Override
    public void visit(NullValue arg0)
    {
        leaf( "NULL" );
    }

    
    @Override
    public void visit(Function arg0)
    {
        String fn = "fn:" + arg0.getName();
        dive( fn );
        if( arg0.getParameters() != null ){
            arg0.getParameters().accept( this );
        }
        up();
    }

    @Override
    public void visit(InverseExpression arg0)
    {
        dive( "inverse" );
        arg0.getExpression().accept( this );
        up();
    }

    @Override
    public void visit(JdbcParameter arg0)
    {
        leaf( "jdbcParameter" );
    }

    @Override
    public void visit(DoubleValue arg0)
    {
        dive( "Double" );
        leaf( arg0.getValue() + "" );
        up();
    }

    @Override
    public void visit(LongValue arg0)
    {
        dive( "Long" );
        leaf( arg0.getValue() + "" );
        up();
    }

    @Override
    public void visit(DateValue arg0)
    {
        dive( "Date" );
        leaf( arg0.getValue() + "" );
        up();
    }

    @Override
    public void visit(TimeValue arg0)
    {
        dive( "Time" );
        leaf( arg0.getValue() + "" );
        up();
    }

    @Override
    public void visit(TimestampValue arg0)
    {
        dive( "Timestamp" );
        leaf( arg0.getValue() + "" );
        up();
    }

    @Override
    public void visit(Parenthesis arg0)
    {
        dive( "NestedExpression" );
        arg0.getExpression().accept( this );
        up();
    }

    @Override
    public void visit(StringValue arg0)
    {
        dive( "String" );
        leaf( arg0.getNotExcapedValue() + "" );
        up();
    }

    @Override
    public void visit(Addition arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(Division arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(Multiplication arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(Subtraction arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(AndExpression arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(OrExpression arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(Between arg0)
    {
        throw new RuntimeException( "Currently not supported!" );
    }

    @Override
    public void visit(EqualsTo arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(GreaterThan arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(GreaterThanEquals arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(InExpression arg0)
    {
        dive( "In" );
        arg0.getLeftExpression().accept( this );
        up();
    }

    @Override
    public void visit(IsNullExpression arg0)
    {
        dive( "isNull" );
        arg0.getLeftExpression().accept( this );
        up();
    }

    @Override
    public void visit(LikeExpression arg0)
    {
        dive( "Like" );
        arg0.getLeftExpression().accept( this );
        arg0.getRightExpression().accept( this );
        up();
    }

    @Override
    public void visit(MinorThan arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(MinorThanEquals arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(NotEqualsTo arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(Column arg0)
    {
        dive( "ColRef" );
        if( arg0.getTable() != null && arg0.getTable().getName() != null )
            leaf( arg0.getTable().getName() + "." + arg0.getColumnName() );
        else
            leaf( arg0.getColumnName() );
        up();
    }

    @Override
    public void visit(SubSelect arg0)
    {
        throw new RuntimeException( "SubSelect is currently not supported!" );
    }

    @Override
    public void visit(CaseExpression arg0)
    {
        throw new RuntimeException( "CaseExpression is currently not supported!" );
    }

    @Override
    public void visit(WhenClause arg0)
    {
        throw new RuntimeException( "WhenClause is currently not supported!" );
    }

    @Override
    public void visit(ExistsExpression arg0)
    {
        throw new RuntimeException( "ExistsExpression is currently not supported!" );
    }

    @Override
    public void visit(AllComparisonExpression arg0)
    {
        throw new RuntimeException( "AllComparisonExpression is currently not supported!" );
    }

    @Override
    public void visit(AnyComparisonExpression arg0)
    {
        throw new RuntimeException( "AnyComparisonExpression is currently not supported!" );
    }

    @Override
    public void visit(Concat arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(Matches arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(BitwiseAnd arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(BitwiseOr arg0)
    {
        visitBinaryExpression( arg0 );
    }

    @Override
    public void visit(BitwiseXor arg0)
    {
        visitBinaryExpression( arg0 );
    }


    /**
     * @see net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor#visit(net.sf.jsqlparser.expression.operators.relational.ExpressionList)
     */
    @Override
    public void visit(ExpressionList arg0)
    {
        List<?> list = arg0.getExpressions();
        if( list == null )
            return;
        
        for( int i = 0; i < list.size(); i++ ){
            Expression e = (Expression) list.get(i);
            e.accept( this );
        }
    }
}