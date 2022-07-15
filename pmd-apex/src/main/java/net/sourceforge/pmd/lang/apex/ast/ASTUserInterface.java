/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import java.util.stream.Collectors;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;





public class ASTUserInterface extends ApexRootNode<Node> implements ASTUserClassOrInterface<Node>,
       CanSuppressWarnings {

    private ApexQualifiedName qname;

    @Deprecated
    @InternalApi
    public ASTUserInterface(Node userInterface) {
        super(userInterface);
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

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.INTERFACE;
    }

    @Override
    public ApexQualifiedName getQualifiedName() {
        if (qname == null) {

            ASTUserClass parent = this.getFirstParentOfType(ASTUserClass.class);

            if (parent != null) {
                qname = ApexQualifiedName.ofNestedClass(parent.getQualifiedName(), this);
            } else {
                qname = ApexQualifiedName.ofOuterClass(this);
            }
        }

        return qname;
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (ASTModifierNode modifier : findChildrenOfType(ASTModifierNode.class)) {
            for (ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getSuperInterfaceName() {
        /*
        return node.getDefiningType().getCodeUnitDetails().getInterfaceTypeRefs().stream().map(TypeRef::getNames)
                .map(it -> it.stream().map(Identifier::getValue).collect(Collectors.joining(".")))
                .findFirst().orElse("");
         */
        // TODO
        return null;
    }
}
