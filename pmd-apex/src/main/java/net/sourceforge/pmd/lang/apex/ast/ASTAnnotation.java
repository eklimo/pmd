/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.common.collect.ImmutableSet;
import com.google.summit.ast.modifier.AnnotationModifier;

public class ASTAnnotation extends AbstractApexNode.Single<AnnotationModifier> {

    /**
     * Valid annotations in the Apex language.
     * <p>
     * Includes all annotations from the <a href="https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_annotation.htm">official
     * documentation</a>, plus
     * <ul>
     *     <li>{@code AllowCertifiedApex}</li>
     *     <li>{@code HiddenFromDoc}</li>
     *     <li>{@code NamespaceGuard}</li>
     *     <li>{@code PermGuard}</li>
     *     <li>{@code PrivateApi}</li>
     *     <li>{@code SfdcOnly}</li>
     *     <li>{@code UseConnectDeserializer}</li>
     *     <li>{@code UseConnectSerializer}</li>
     *     <li>{@code VisibleApiVersion}</li>
     * </ul>
     * for backward compatibility.
     */
    private static final ImmutableSet<String> VALID_ANNOTATION_NAMES = ImmutableSet.of(
            "allowcertifiedapex",
            "auraenabled",
            "deprecated",
            "future",
            "hiddenfromdoc",
            "httpdelete",
            "httpget",
            "httppatch",
            "httppost",
            "httpput",
            "invocablemethod",
            "invocablevariable",
            "istest",
            "jsonaccess",
            "namespaceaccessible",
            "namespaceguard",
            "permguard",
            "privateapi",
            "readonly",
            "remoteaction",
            "restresource",
            "sfdconly",
            "suppresswarnings",
            "testsetup",
            "testvisible",
            "useconnectdeserializer",
            "useconnectserializer",
            "visibleapiversion"
    );

    @Deprecated
    @InternalApi
    public ASTAnnotation(AnnotationModifier annotationModifier) {
        super(annotationModifier);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getName().getString();
    }

    /**
     * @deprecated Will be removed in 7.0, the AST shouldn't know about rules
     */
    @Deprecated
    public boolean suppresses(Rule rule) {
        final String ruleAnno = "PMD." + rule.getName();

        if (hasImageEqualTo("SuppressWarnings")) {
            for (ASTAnnotationParameter param : findChildrenOfType(ASTAnnotationParameter.class)) {
                String image = param.getImage();

                if (image != null) {
                    Set<String> paramValues = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                    paramValues.addAll(Arrays.asList(image.replaceAll("\\s+", "").split(",")));
                    if (paramValues.contains("PMD") || paramValues.contains(ruleAnno) || paramValues.contains("all")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isResolved() {
        return VALID_ANNOTATION_NAMES.contains(node.getName().getString().toLowerCase());
    }
}
