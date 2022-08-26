/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.LiteralExpression;
import com.google.summit.ast.expression.VariableExpression;

public class ASTLiteralExpression extends AbstractApexNode.Single<LiteralExpression> {

    @Deprecated
    @InternalApi
    public ASTLiteralExpression(LiteralExpression literalExpression) {
        super(literalExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public enum LiteralType {
        STRING,
        INTEGER,
        LONG,
        DOUBLE,
        DECIMAL,
        TRUE,
        FALSE,
        NULL
    }

    public LiteralType getLiteralType() {
        if (node instanceof LiteralExpression.StringVal) {
            return LiteralType.STRING;
        } else if (node instanceof LiteralExpression.BooleanVal) {
            return ((LiteralExpression.BooleanVal) node).getValue() ? LiteralType.TRUE : LiteralType.FALSE;
        } else if (node instanceof LiteralExpression.IntegerVal) {
            return LiteralType.INTEGER;
        } else if (node instanceof LiteralExpression.DoubleVal) {
            return LiteralType.DOUBLE;
        } else if (node instanceof LiteralExpression.LongVal) {
            return LiteralType.LONG;
        } else if (false) { // TODO(b/239648780)
            return LiteralType.DECIMAL;
        } else if (node instanceof LiteralExpression.NullVal) {
            return LiteralType.NULL;
        } else {
            return null;
        }
    }

    public boolean isString() {
        return getLiteralType() == LiteralType.STRING;
    }

    public boolean isBoolean() {
        return getLiteralType() == LiteralType.TRUE || getLiteralType() == LiteralType.FALSE;
    }

    public boolean isInteger() {
        return getLiteralType() == LiteralType.INTEGER;
    }

    public boolean isDouble() {
        return getLiteralType() == LiteralType.DOUBLE;
    }

    public boolean isLong() {
        return getLiteralType() == LiteralType.LONG;
    }

    public boolean isDecimal() {
        return getLiteralType() == LiteralType.DECIMAL;
    }

    public boolean isNull() {
        return getLiteralType() == LiteralType.NULL;
    }

    @Override
    public String getImage() {
        return literalToString(node);
    }

    /**
     * Returns the name of this literal when it is labeled in an object initializer with named
     * arguments ({@link ASTNewKeyValueObjectExpression}).
     * <p>
     * For example, in the Apex code
     * <pre>{@code
     * new X(a = 1, b = 2)
     * }</pre>
     * , the {@link ASTLiteralExpression} corresponding to {@code 2} will have the {@code name}
     * "{@code b}".
     */
    public String getName() {
        if (getParent() instanceof ASTAssignmentExpression && getParent().getParent() instanceof ASTNewKeyValueObjectExpression) {
            ASTAssignmentExpression parent = (ASTAssignmentExpression) getParent();
            VariableExpression target = (VariableExpression) parent.node.getTarget();
            return target.getId().getString();
        }
        return null;
    }
}
