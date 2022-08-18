/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.Identifier;
import com.google.summit.ast.expression.CallExpression;

public class ASTMethodCallExpression extends AbstractApexNode.Single<CallExpression> {

    /** The {@link Identifier}s that constitute the {@link #getFullMethodName() full method name}. */
    private final List<Identifier> components;

    @Deprecated
    @InternalApi
    public ASTMethodCallExpression(CallExpression callExpression, List<Identifier> components) {
        super(callExpression);
        this.components = components;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getMethodName() {
        return node.getId().getString();
    }

    public String getFullMethodName() {
        return components.stream().map(Identifier::getString).collect(Collectors.joining("."));
    }

    public int getInputParametersSize() {
        return node.getArgs().size();
    }
}
