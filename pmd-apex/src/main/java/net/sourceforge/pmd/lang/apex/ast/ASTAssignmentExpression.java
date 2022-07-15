/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTAssignmentExpression extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTAssignmentExpression(Node assignmentExpression) {
        super(assignmentExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    // public AssignmentOp getOperator() {
    //     return node.getOp();
    // }
    // TODO
}
