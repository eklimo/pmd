/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTPostfixExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTPostfixExpression(Node postfixExpression) {
        super(postfixExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /*
    public PostfixOp getOperator() {
        return node.getOp();
    }
     */
    // TODO(b/239648780)
}
