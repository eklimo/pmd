/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

// TODO(b/243906255)
public class ASTSoqlExpression extends AbstractApexNode.Single<Node> {

    @Deprecated
    @InternalApi
    public ASTSoqlExpression(Node soqlExpression) {
        super(soqlExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getQuery() {
        // return node.getRawQuery();
        return null;
    }

    public String getCanonicalQuery() {
        // return node.getCanonicalQuery();
        return null;
    }
}
