/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.EnumDeclaration;

public class ASTUserEnum extends ApexRootNode<EnumDeclaration> {

    @Deprecated
    @InternalApi
    public ASTUserEnum(EnumDeclaration enumDeclaration) {
        super(enumDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getId().getString();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }
}
