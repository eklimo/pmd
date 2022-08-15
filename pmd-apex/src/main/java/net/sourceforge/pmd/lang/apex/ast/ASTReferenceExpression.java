/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.Identifier;

public class ASTReferenceExpression extends AbstractApexNode.Many<Identifier> {

    private final boolean isSafe;

    @Deprecated
    @InternalApi
    public ASTReferenceExpression(List<Identifier> identifiers, boolean isSafe) {
        super(identifiers);
        this.isSafe = isSafe;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /*
    public IdentifierContext getContext() {
        return node.getContext();
    }
     */
    // TODO(b/239648780)


    /*
    public ReferenceType getReferenceType() {
        return node.getReferenceType();
    }
     */
    // TODO(b/239648780)

    @Override
    public String getImage() {
        if (!nodes.isEmpty()) {
            return nodes.get(0).asCodeString();
        }
        return "";
    }

    public List<String> getNames() {
        return nodes.stream().map(Identifier::asCodeString).collect(Collectors.toList());
    }

    public boolean isSafeNav() {
        return this.isSafe;
    }

    public boolean isSObjectType() {
        /*
        List<Identifier> identifiers = node.getNames();
        if (identifiers != null) {
            return identifiers.stream().anyMatch(id -> "sobjecttype".equalsIgnoreCase(id.getValue()));
        }
         */
        // TODO(b/239648780)
        return false;
    }
}
