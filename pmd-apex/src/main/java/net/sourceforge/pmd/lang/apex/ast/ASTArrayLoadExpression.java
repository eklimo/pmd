/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.ArrayExpression;

public class ASTArrayLoadExpression extends AbstractApexNode.Single<ArrayExpression> {

    @Deprecated
    @InternalApi
    public ASTArrayLoadExpression(ArrayExpression arrayExpression) {
        super(arrayExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
