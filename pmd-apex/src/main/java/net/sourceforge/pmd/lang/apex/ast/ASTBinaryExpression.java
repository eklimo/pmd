/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBinaryExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTBinaryExpression(Node binaryExpression) {
        super(binaryExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    // public BinaryOp getOperator() {
    //     return node.getOp();
    // }
    // TODO(b/239648780)
}
