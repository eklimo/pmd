/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.CompilationUnit;
import org.junit.Assert;
import org.junit.Test;

public class ASTUserEnumTest extends ApexParserTestBase {

    @Test
    public void testEnumName() {
        ApexNode<CompilationUnit> node = parse("class Foo { enum Bar { } }");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        ASTUserEnum enumNode = node.getFirstDescendantOfType(ASTUserEnum.class);
        Assert.assertNotNull(enumNode);
        Assert.assertEquals("Bar", enumNode.getImage());
    }
}
