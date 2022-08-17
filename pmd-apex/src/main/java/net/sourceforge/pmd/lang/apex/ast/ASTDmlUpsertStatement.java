/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.DmlStatement;

public class ASTDmlUpsertStatement extends AbstractApexNode.Single<DmlStatement.Upsert> {

    @Deprecated
    @InternalApi
    public ASTDmlUpsertStatement(DmlStatement.Upsert dmlUpsertStatement) {
        super(dmlUpsertStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
