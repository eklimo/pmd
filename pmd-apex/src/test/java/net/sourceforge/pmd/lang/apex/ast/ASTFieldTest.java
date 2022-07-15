/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.CompilationUnit;
import org.junit.Assert;
import org.junit.Test;

public class ASTFieldTest extends ApexParserTestBase {

    @Test
    public void testGetType() {
        ApexNode<CompilationUnit> node = parse("public class Foo { private String myField = 'a'; }");
        ASTField field = node.getFirstDescendantOfType(ASTField.class);

        Assert.assertEquals("myField", field.getImage());
        Assert.assertEquals("String", field.getType());
        Assert.assertEquals("a", field.getValue());
    }

    @Test
    public void testGetValue() {
        ApexNode<CompilationUnit> node = parse("public class Foo { private String myField = 'a'; }");
        ASTField field = node.getFirstDescendantOfType(ASTField.class);

        Assert.assertEquals("a", field.getValue());
    }

    @Test
    public void testGetNoValue() {
        ApexNode<CompilationUnit> node = parse("public class Foo { private String myField; }");
        ASTField field = node.getFirstDescendantOfType(ASTField.class);

        Assert.assertNull(field.getValue());
    }
}
