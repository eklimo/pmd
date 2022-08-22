/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.DmlStatement;

public class ASTDmlUndeleteStatement extends AbstractApexNode.Single<DmlStatement.Undelete> {

    @Deprecated
    @InternalApi
    public ASTDmlUndeleteStatement(DmlStatement.Undelete dmlUndeleteStatement) {
        super(dmlUndeleteStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
