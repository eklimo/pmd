/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.DmlStatement;

public class ASTDmlInsertStatement extends AbstractApexNode.Single<DmlStatement.Insert> {

    @Deprecated
    @InternalApi
    public ASTDmlInsertStatement(DmlStatement.Insert dmlInsertStatement) {
        super(dmlInsertStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
