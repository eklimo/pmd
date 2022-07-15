/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTModifierNode extends AbstractApexNode<Node> implements AccessNode {

    @Deprecated
    @InternalApi
    public ASTModifierNode(Node modifierNode) {
        super(modifierNode);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public int getModifiers() {
        // return node.getModifiers().getJavaModifiers();
        // TODO
        return 0;
    }

    @Override
    public boolean isPublic() {
        // return (node.getModifiers().getJavaModifiers() & PUBLIC) == PUBLIC;
        // TODO
        return false;
    }

    @Override
    public boolean isProtected() {
        // return (node.getModifiers().getJavaModifiers() & PROTECTED) == PROTECTED;
        // TODO
        return false;
    }

    @Override
    public boolean isPrivate() {
        // return (node.getModifiers().getJavaModifiers() & PRIVATE) == PRIVATE;
        // TODO
        return false;
    }

    @Override
    public boolean isAbstract() {
        // return (node.getModifiers().getJavaModifiers() & ABSTRACT) == ABSTRACT;
        // TODO
        return false;
    }

    @Override
    public boolean isStatic() {
        // return (node.getModifiers().getJavaModifiers() & STATIC) == STATIC;
        // TODO
        return false;
    }

    @Override
    public boolean isFinal() {
        // return (node.getModifiers().getJavaModifiers() & FINAL) == FINAL;
        // TODO
        return false;
    }

    @Override
    public boolean isTransient() {
        // return (node.getModifiers().getJavaModifiers() & TRANSIENT) == TRANSIENT;
        // TODO
        return false;
    }

    /**
     * Returns true if function has `@isTest` annotation or `testmethod` modifier
     */
    public boolean isTest() {
        // return node.getModifiers().isTest();
        // TODO
        return false;
    }

    /**
     * Returns true if function has `testmethod` modifier
     */
    public boolean hasDeprecatedTestMethod() {
        // return node.getModifiers().has(TEST_METHOD);
        // TODO
        return false;
    }

    public boolean isTestOrTestSetup() {
        // return node.getModifiers().isTestOrTestSetup();
        // TODO
        return false;
    }

    public boolean isWithSharing() {
        // return node.getModifiers().has(ModifierTypeInfos.WITH_SHARING);
        // TODO
        return false;
    }

    public boolean isWithoutSharing() {
        // return node.getModifiers().has(ModifierTypeInfos.WITHOUT_SHARING);
        // TODO
        return false;
    }

    public boolean isInheritedSharing() {
        // return node.getModifiers().has(ModifierTypeInfos.INHERITED_SHARING);
        // TODO
        return false;
    }

    public boolean isWebService() {
        // return node.getModifiers().has(ModifierTypeInfos.WEB_SERVICE);
        // TODO
        return false;
    }

    public boolean isGlobal() {
        // return node.getModifiers().has(ModifierTypeInfos.GLOBAL);
        // TODO
        return false;
    }

    public boolean isOverride() {
        // return node.getModifiers().has(ModifierTypeInfos.OVERRIDE);
        // TODO
        return false;
    }

    public boolean isVirtual() {
        // return node.getModifiers().has(ModifierTypeInfos.VIRTUAL);
        // TODO
        return false;
    }
}
