/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast

import net.sourceforge.pmd.annotation.InternalApi
import net.sourceforge.pmd.lang.apex.ApexParserOptions
import net.sourceforge.pmd.lang.ast.ParseException
import net.sourceforge.pmd.lang.ast.SourceCodePositioner

import com.google.summit.ast.CompilationUnit
import com.google.summit.ast.Identifier
import com.google.summit.ast.Node
import com.google.summit.ast.TypeRef
import com.google.summit.ast.declaration.ClassDeclaration
import com.google.summit.ast.declaration.EnumDeclaration
import com.google.summit.ast.declaration.FieldDeclaration
import com.google.summit.ast.declaration.FieldDeclarationGroup
import com.google.summit.ast.declaration.InterfaceDeclaration
import com.google.summit.ast.declaration.MethodDeclaration
import com.google.summit.ast.declaration.PropertyDeclaration
import com.google.summit.ast.declaration.TriggerDeclaration
import com.google.summit.ast.declaration.TypeDeclaration
import com.google.summit.ast.expression.ArrayExpression
import com.google.summit.ast.expression.AssignExpression
import com.google.summit.ast.expression.BinaryExpression
import com.google.summit.ast.expression.CastExpression
import com.google.summit.ast.expression.LiteralExpression
import com.google.summit.ast.expression.SuperExpression
import com.google.summit.ast.expression.ThisExpression
import com.google.summit.ast.expression.UnaryExpression
import com.google.summit.ast.modifier.KeywordModifier
import com.google.summit.ast.modifier.KeywordModifier.Keyword
import com.google.summit.ast.modifier.Modifier
import com.google.summit.ast.statement.CompoundStatement
import com.google.summit.ast.statement.ExpressionStatement

@Deprecated("internal")
@InternalApi
@Suppress("DEPRECATION")
class ApexTreeBuilder(val sourceCode: String, val parserOptions: ApexParserOptions) {
    private val sourceCodePositioner = SourceCodePositioner(sourceCode)

    /** Builds and returns an [ApexNode] AST corresponding to the given [root] node. */
    fun buildTree(root: CompilationUnit): ApexRootNode<TypeDeclaration> {
        // Build tree
        val result =
            build(root, parent = null) as? ApexRootNode<TypeDeclaration>
                ?: throw ParseException("Unable to build tree")

        // Call additional methods
        callAdditional(result)

        return result
    }

    /** Calls additional methods for each node in [root] using a post-order traversal. */
    private fun callAdditional(root: ApexNode<*>) =
        root.jjtAccept(
            object : ApexParserVisitorAdapter() {
                override fun visit(node: ApexNode<*>?, data: Any?): Any? =
                    super.visit(node, data).also {
                        if (node is AbstractApexNode) {
                            node.handleSourceCode(sourceCode)
                            node.calculateLineNumbers(sourceCodePositioner)
                        }
                    }
            },
            null
        )

    /** Builds an [ApexNode] wrapper for [node]. */
    private fun build(node: Node?, parent: ApexNode<*>?): ApexNode<*>? =
        when (node) {
            null -> null
            is CompilationUnit -> build(node.typeDeclaration, parent)
            is TypeDeclaration -> buildTypeDeclaration(node)
            is MethodDeclaration -> buildMethodDeclaration(node, parent)
            is PropertyDeclaration -> buildPropertyDeclaration(node)
            is FieldDeclarationGroup -> buildFieldDeclarationGroup(node)
            is FieldDeclaration -> ASTFieldDeclaration(node).apply { buildChildren(node, parent = this) }
            is CompoundStatement -> ASTBlockStatement(node).apply { buildChildren(node, parent = this) }
            is ExpressionStatement ->
                ASTExpressionStatement(node).apply { buildChildren(node, parent = this) }
            is AssignExpression ->
                ASTAssignmentExpression(node).apply { buildChildren(node, parent = this) }
            is ArrayExpression -> buildArrayExpression(node)
            is LiteralExpression ->
                ASTLiteralExpression(node).apply { buildChildren(node, parent = this) }
            is CastExpression -> ASTCastExpression(node).apply { buildChildren(node, parent = this) }
            is BinaryExpression -> buildBinaryExpression(node)
            is UnaryExpression -> buildUnaryExpression(node)
            is SuperExpression ->
                ASTSuperVariableExpression(node).apply { buildChildren(node, parent = this) }
            is ThisExpression ->
                ASTThisVariableExpression(node).apply { buildChildren(node, parent = this) }
            is Identifier,
            is KeywordModifier,
            is TypeRef -> null
            else -> {
                println("No adapter exists for type ${node::class.qualifiedName}")
                // TODO(b/239648780): temporary print
                null
            }
        }

    /** Builds an [ApexNode] wrapper for [node] and sets its parent to [parent]. */
    private fun buildAndSetParent(node: Node, parent: ApexNode<*>) =
        build(node, parent)?.also { it.setParent(parent) }

    /**
     * Builds an [ApexNode] wrapper for each [child][Node.getChildren] of [node] and sets its parent
     * to [parent].
     *
     * If [exclude] is provided, child nodes matching this predicate are not visited.
     */
    private fun buildChildren(
        node: Node,
        parent: ApexNode<*>,
        exclude: (Node) -> Boolean = { false } // exclude none by default
    ) = node.getChildren().filterNot(exclude).forEach { buildAndSetParent(it, parent) }

    /** Builds an [ApexRootNode] wrapper for the [TypeDeclaration] node. */
    private fun buildTypeDeclaration(node: TypeDeclaration) =
        when (node) {
            is ClassDeclaration ->
                ASTUserClass(node).apply {
                    buildModifiers(node.modifiers).also { it.setParent(this) }
                    buildChildren(node, parent = this, exclude = { it in node.modifiers })
                }
            is InterfaceDeclaration ->
                ASTUserInterface(node).apply {
                    buildModifiers(node.modifiers).also { it.setParent(this) }
                    buildChildren(node, parent = this, exclude = { it in node.modifiers })
                }
            is EnumDeclaration -> ASTUserEnum(node) // TODO(b/239648780): enum body is untranslated
            is TriggerDeclaration -> ASTUserTrigger(node) // TODO(b/239648780): visit children
        }

    /** Builds an [ASTMethod] wrapper for the [MethodDeclaration] node. */
    private fun buildMethodDeclaration(node: MethodDeclaration, parent: ApexNode<*>?) =
        when {
            node.isAnonymousInitializationCode() && !node.hasKeyword(Keyword.STATIC) ->
                build(node.body, parent)
            else -> {
                ASTMethod(node).apply {
                    buildModifiers(node.modifiers).also { it.setParent(this) }
                    buildChildren(node, parent = this, exclude = { it in node.modifiers })
                }
            }
        }

    /** Builds an [ASTProperty] wrapper for the [PropertyDeclaration] node. */
    private fun buildPropertyDeclaration(node: PropertyDeclaration) =
        ASTProperty(node).apply {
            buildModifiers(node.modifiers).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it in node.modifiers })
        }

    /** Builds an [ASTFieldDeclarationStatements] wrapper for the [FieldDeclarationGroup] node. */
    private fun buildFieldDeclarationGroup(node: FieldDeclarationGroup) =
        ASTFieldDeclarationStatements(node).apply {
            buildModifiers(node.modifiers).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it in node.modifiers })
        }

    /**
     * Builds an [ASTArrayStoreExpression] or [ASTArrayLoadExpression] wrapper for the
     * [ArrayExpression] node.
     */
    private fun buildArrayExpression(node: ArrayExpression) =
        if ((node.parent as? AssignExpression)?.target == node) {
            ASTArrayStoreExpression(node)
        } else {
            ASTArrayLoadExpression(node)
        }
            .apply { buildChildren(node, parent = this) }

    /**
     * Builds an [ASTBinaryExpression], [ASTBooleanExpression], or [ASTInstanceOfExpression] wrapper
     * for the [BinaryExpression] node.
     */
    private fun buildBinaryExpression(node: BinaryExpression) =
        when (node.op) {
            BinaryExpression.Operator.INSTANCEOF -> ASTInstanceOfExpression(node)
            BinaryExpression.Operator.GREATER_THAN_OR_EQUAL,
            BinaryExpression.Operator.GREATER_THAN,
            BinaryExpression.Operator.LESS_THAN,
            BinaryExpression.Operator.LESS_THAN_OR_EQUAL,
            BinaryExpression.Operator.EQUAL,
            BinaryExpression.Operator.NOT_EQUAL,
            BinaryExpression.Operator.ALTERNATIVE_NOT_EQUAL,
            BinaryExpression.Operator.EXACTLY_EQUAL,
            BinaryExpression.Operator.EXACTLY_NOT_EQUAL,
            BinaryExpression.Operator.BITWISE_XOR,
            BinaryExpression.Operator.LOGICAL_AND,
            BinaryExpression.Operator.LOGICAL_OR,
            -> ASTBooleanExpression(node)
            BinaryExpression.Operator.ADDITION,
            BinaryExpression.Operator.SUBTRACTION,
            BinaryExpression.Operator.MULTIPLICATION,
            BinaryExpression.Operator.DIVISION,
            BinaryExpression.Operator.MODULO,
            BinaryExpression.Operator.LEFT_SHIFT,
            BinaryExpression.Operator.RIGHT_SHIFT_SIGNED,
            BinaryExpression.Operator.RIGHT_SHIFT_UNSIGNED,
            BinaryExpression.Operator.BITWISE_AND,
            BinaryExpression.Operator.BITWISE_OR,
            -> ASTBinaryExpression(node)
        }.apply { buildChildren(node, parent = this) }

    /**
     * Builds an [ASTPrefixExpression] or [ASTPostfixExpression] wrapper for the [UnaryExpression]
     * node.
     */
    private fun buildUnaryExpression(node: UnaryExpression) =
        when (node.op) {
            UnaryExpression.Operator.PLUS,
            UnaryExpression.Operator.NEGATION,
            UnaryExpression.Operator.LOGICAL_COMPLEMENT,
            UnaryExpression.Operator.BITWISE_NOT,
            UnaryExpression.Operator.PRE_INCREMENT,
            UnaryExpression.Operator.PRE_DECREMENT,
            -> ASTPrefixExpression(node)
            UnaryExpression.Operator.POST_INCREMENT,
            UnaryExpression.Operator.POST_DECREMENT,
            -> ASTPostfixExpression(node)
        }.apply { buildChildren(node, parent = this) }

    /** Builds an [ASTModifierNode] wrapper for the list of [Modifier]s. */
    private fun buildModifiers(modifiers: List<Modifier>) =
        ASTModifierNode(modifiers).apply { modifiers.forEach { buildAndSetParent(it, parent = this) } }

    /**
     * If [parent] is not null, adds this [ApexNode] as a [child][ApexNode.jjtAddChild] and sets
     * [parent] as the [parent][ApexNode.jjtSetParent].
     */
    private fun ApexNode<*>.setParent(parent: ApexNode<*>?) {
        if (parent != null) {
            parent.jjtAddChild(this, parent.numChildren)
            this.jjtSetParent(parent)
        }
    }

    val suppressMap
        get() = emptyMap<Int, String>()
    // TODO(b/239648780)
}
