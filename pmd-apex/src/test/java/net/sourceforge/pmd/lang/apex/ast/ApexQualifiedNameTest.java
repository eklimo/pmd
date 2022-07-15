/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.summit.ast.CompilationUnit;
import java.util.List;

import org.junit.Test;

/**
 * @author Clément Fournier
 */
public class ApexQualifiedNameTest extends ApexParserTestBase {

    @Test
    public void testClass() {
        ApexNode<CompilationUnit> root = parse("public class Foo {}");

        ApexQualifiedName qname = ASTUserClass.class.cast(root).getQualifiedName();
        assertEquals("c__Foo", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertNull(qname.getOperation());
    }


    @Test
    public void testNestedClass() {
        ApexNode<CompilationUnit> root = parse("public class Foo { class Bar {}}");

        ApexQualifiedName qname = root.getFirstDescendantOfType(ASTUserClass.class).getQualifiedName();
        assertEquals("c__Foo.Bar", qname.toString());
        assertEquals(2, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertNull(qname.getOperation());
    }


    @Test
    public void testSimpleMethod() {
        ApexNode<CompilationUnit> root = parse("public class Foo { String foo() {}}");
        ApexQualifiedName qname = root.getFirstDescendantOfType(ASTMethod.class).getQualifiedName();
        assertEquals("c__Foo#foo()", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertEquals("foo()", qname.getOperation());
    }


    @Test
    public void testMethodWithArguments() {
        ApexNode<CompilationUnit> root = parse("public class Foo { String foo(String h, Foo g) {}}");
        ApexQualifiedName qname = root.getFirstDescendantOfType(ASTMethod.class).getQualifiedName();
        assertEquals("c__Foo#foo(String, Foo)", qname.toString());
        assertEquals(1, qname.getClasses().length);
        assertNotNull(qname.getNameSpace());
        assertEquals("foo(String, Foo)", qname.getOperation());
    }


    @Test
    public void testOverLoads() {
        ApexNode<CompilationUnit> root = parse("public class Foo { "
                                                                 + "String foo(String h) {} "
                                                                 + "String foo(int c) {}"
                                                                 + "String foo(Foo c) {}}");

        List<ASTMethod> methods = root.findDescendantsOfType(ASTMethod.class);

        for (ASTMethod m1 : methods) {
            for (ASTMethod m2 : methods) {
                if (m1 != m2) {
                    assertNotEquals(m1.getQualifiedName(), m2.getQualifiedName());
                }
            }
        }
    }


    @Test
    public void testTrigger() {
        ApexNode<CompilationUnit> root = parse("trigger myAccountTrigger on Account (before insert, before update) {}");


        List<ASTMethod> methods = root.findDescendantsOfType(ASTMethod.class);

        for (ASTMethod m : methods) {
            assertEquals("c__trigger.Account#myAccountTrigger", m.getQualifiedName().toString());
        }
    }
}
