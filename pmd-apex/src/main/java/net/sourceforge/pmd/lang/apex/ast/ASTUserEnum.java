/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTUserEnum extends ApexRootNode<Node> {

    @Deprecated
    @InternalApi
    public ASTUserEnum(Node userEnum) {
        super(userEnum);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        String apexName = getDefiningType();
        return apexName.substring(apexName.lastIndexOf('.') + 1);
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }
}
