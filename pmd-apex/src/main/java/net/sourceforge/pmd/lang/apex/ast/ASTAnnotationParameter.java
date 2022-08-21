/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.LiteralExpression;
import com.google.summit.ast.modifier.ElementArgument;
import com.google.summit.ast.modifier.ElementValue;

public class ASTAnnotationParameter extends AbstractApexNode.Single<ElementArgument> {
    public static final String SEE_ALL_DATA = "seeAllData";

    @Deprecated
    @InternalApi
    public ASTAnnotationParameter(ElementArgument elementArgument) {
        super(elementArgument);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        return node.getName().getString();
    }

    public String getValue() {
        if (node.getValue() instanceof ElementValue.ExpressionValue) {
            return expressionToString(((ElementValue.ExpressionValue) node.getValue()).getValue());
        }
        return null;
    }

     public Boolean getBooleanValue() {
         if (node.getValue() instanceof ElementValue.ExpressionValue) {
             ElementValue.ExpressionValue value = (ElementValue.ExpressionValue) node.getValue();
             if (value.getValue() instanceof LiteralExpression.BooleanVal) {
                 return ((LiteralExpression.BooleanVal) value.getValue()).getValue();
             }
         }
         return false;
     }

    @Override
    public String getImage() {
        return getValue();
    }
}
