/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBooleanExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTBooleanExpression(Node booleanExpression) {
        super(booleanExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    // public BooleanOp getOperator() {
    //     return this.node.getOp();
    // }
    // TODO
}
