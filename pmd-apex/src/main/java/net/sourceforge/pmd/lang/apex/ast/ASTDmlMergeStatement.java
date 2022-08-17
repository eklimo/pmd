/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.DmlStatement;

public class ASTDmlMergeStatement extends AbstractApexNode.Single<DmlStatement.Merge> {

    @Deprecated
    @InternalApi
    public ASTDmlMergeStatement(DmlStatement.Merge dmlMergeStatement) {
        super(dmlMergeStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
