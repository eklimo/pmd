/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.EventStreams;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.fxdesigner.model.ASTManager;
import net.sourceforge.pmd.util.fxdesigner.model.ParseAbortedException;
import net.sourceforge.pmd.util.fxdesigner.popups.AuxclasspathSetupController;
import net.sourceforge.pmd.util.fxdesigner.util.ConvenienceNodeWrapper;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.AvailableSyntaxHighlighters;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea.LayerId;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ASTTreeCell;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ASTTreeItem;
import net.sourceforge.pmd.util.fxdesigner.util.controls.TreeViewWrapper;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;


/**
 * One editor, i.e. source editor and ast tree view.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SourceEditorController implements Initializable, SettingsOwner {

    private static final Duration AST_REFRESH_DELAY = Duration.ofMillis(100);

    @FXML
    private Label astTitleLabel;
    @FXML
    private TreeView<Node> astTreeView;
    @FXML
    private CustomCodeArea codeEditorArea;

    private ASTManager astManager;
    private TreeViewWrapper<Node> treeViewWrapper;

    private final MainDesignerController parent;

    private Var<Node> currentFocusNode = Var.newSimpleVar(null);
    private ASTTreeItem selectedTreeItem;

    private Var<List<File>> auxclasspathFiles = Var.newSimpleVar(emptyList());
    private final Val<ClassLoader> auxclasspathClassLoader = auxclasspathFiles.map(fileList -> {
        try {
            return new ClasspathClassLoader(fileList, SourceEditorController.class.getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
            return SourceEditorController.class.getClassLoader();
        }
    });

    public SourceEditorController(DesignerRoot owner, MainDesignerController mainController) {
        parent = mainController;
        astManager = new ASTManager(owner);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        treeViewWrapper = new TreeViewWrapper<>(astTreeView);
        astTreeView.setCellFactory(treeView -> new ASTTreeCell(parent));

        languageVersionProperty().values()
                                 .filterMap(Objects::nonNull, LanguageVersion::getLanguage)
                                 .distinct()
                                 .subscribe(this::updateSyntaxHighlighter);

        EventStreams.valuesOf(astTreeView.getSelectionModel().selectedItemProperty())
                    .filterMap(Objects::nonNull, TreeItem::getValue)
                    .subscribe(parent::onNodeItemSelected);

        codeEditorArea.plainTextChanges()
                      .filter(t -> !t.isIdentity())
                      .successionEnds(AST_REFRESH_DELAY)
                      // Refresh the AST anytime the text, classloader, or language version changes
                      .or(auxclasspathClassLoader.changes())
                      .or(languageVersionProperty().changes())
                      .subscribe(tick -> {
                          // Discard the AST if the language version has changed
                          tick.ifRight(c -> astTreeView.setRoot(null));
                          parent.refreshAST();
                      });

        codeEditorArea.setParagraphGraphicFactory(lineNumberFactory());
    }


    private IntFunction<javafx.scene.Node> lineNumberFactory() {
        IntFunction<javafx.scene.Node> base = LineNumberFactory.get(codeEditorArea);
        Val<Integer> activePar = Val.wrap(codeEditorArea.currentParagraphProperty());

        return idx -> {

            javafx.scene.Node label = base.apply(idx);

            activePar.conditionOnShowing(label)
                     .values()
                     .subscribe(p -> label.pseudoClassStateChanged(PseudoClass.getPseudoClass("has-caret"), idx == p));

            // adds a pseudo class if part of the focus node appears on this line
            currentFocusNode.conditionOnShowing(label)
                            .values()
                            .subscribe(n -> label.pseudoClassStateChanged(PseudoClass.getPseudoClass("is-focus-node"),
                                                                          n != null && idx <= n.getEndLine() - 1 && idx >= n.getBeginLine() - 1));

            return label;
        };
    }


    /**
     * Refreshes the AST and returns the new compilation unit if the parse didn't fail.
     */
    public Optional<Node> refreshAST() {
        String source = getText();
        Node previous = getCompilationUnit();
        Node current;

        if (StringUtils.isBlank(source)) {
            astTreeView.setRoot(null);
            return Optional.empty();
        }

        try {
            current = astManager.updateCompilationUnit(source, auxclasspathClassLoader.getValue());
        } catch (ParseAbortedException e) {
            invalidateAST(true);
            return Optional.empty();
        }
        if (!Objects.equals(previous, current)) {
            parent.invalidateAst();
            setUpToDateCompilationUnit(current);
        }
        return Optional.of(current);
    }


    public void showAuxclasspathSetupPopup(DesignerRoot root) {
        new AuxclasspathSetupController(root).show(root.getMainStage(),
                                                   auxclasspathFiles.getValue(),
                                                   auxclasspathFiles::setValue);
    }



    private void setUpToDateCompilationUnit(Node node) {
        astTitleLabel.setText("Abstract Syntax Tree");
        ASTTreeItem root = ASTTreeItem.getRoot(node);
        astTreeView.setRoot(root);
    }


    private void updateSyntaxHighlighter(Language language) {
        codeEditorArea.setSyntaxHighlighter(AvailableSyntaxHighlighters.getHighlighterForLanguage(language)
                                                                       .orElse(null));
    }


    /** Clears the secondary highlight. Doesn't clear the primary focus.. */
    public void clearSecondaryHighlight() {
        clearLayer(LayerId.SECONDARY);
    }


    /** Clears the highlighting of XPath results. */
    public void clearXPathHighlight() {
        clearLayer(LayerId.XPATH_RESULTS);
    }


    private void clearLayer(LayerId id) {
        codeEditorArea.clearStyleLayer(id);
    }


    /**
     * Highlights the given node (or nothing if null).
     * Removes highlighting on the previously highlighted node.
     */
    public void setFocusNode(Node node) {
        highlightNodes(node == null ? emptyList() : singleton(node), LayerId.FOCUS, true, true);
        currentFocusNode.setValue(node);
    }

    /** Highlights xpath results (xpath highlight). */
    public void highlightXPathResults(Collection<? extends Node> nodes) {
        highlightNodes(nodes, LayerId.XPATH_RESULTS, true, false);
    }


    /** Highlights name occurrences (secondary highlight). */
    public void highlightNameOccurrences(Collection<? extends NameOccurrence> occs) {
        highlightNodes(occs.stream().map(NameOccurrence::getLocation).collect(Collectors.toList()), LayerId.SECONDARY, true, false, "name-occurence");
    }


    /** Highlights nodes that are in error (secondary highlight). */
    public void highlightErrorNodes(Collection<? extends Node> nodes) {
        highlightNodes(nodes, LayerId.SECONDARY, true, true, "error-highlight");
    }


    private void highlightNodes(Collection<? extends Node> nodes, LayerId layer, boolean resetLayer, boolean autoScroll, String... cssClasses) {
        codeEditorArea.styleCss(nodes, layer, resetLayer, cssClasses);

        if (autoScroll && !nodes.isEmpty()) {
            scrollEditorToNode(nodes.iterator().next());
        }
    }


    private void scrollEditorToNode(Node node) {

        codeEditorArea.moveTo(node.getBeginLine() - 1, 0);

        if (node.getBeginLine() < codeEditorArea.firstVisibleParToAllParIndex()) {
            codeEditorArea.showParagraphAtTop(Math.max(node.getBeginLine() - 2, 0));
        } else if (node.getEndLine() > codeEditorArea.lastVisibleParToAllParIndex()) {
            codeEditorArea.showParagraphAtBottom(Math.min(node.getEndLine(), codeEditorArea.getParagraphs().size()));
        }
    }


    public void clearStyleLayers() {
        codeEditorArea.clearStyleLayers();
    }


    public void focusNodeInTreeView(Node node) {
        SelectionModel<TreeItem<Node>> selectionModel = astTreeView.getSelectionModel();

        // node is different from the old one
        if (selectedTreeItem == null && node != null
            || selectedTreeItem != null && !Objects.equals(node, selectedTreeItem.getValue())) {
            ASTTreeItem found = ((ASTTreeItem) astTreeView.getRoot()).findItem(node);
            if (found != null) {
                selectionModel.select(found);
            }
            selectedTreeItem = found;

            astTreeView.getFocusModel().focus(selectionModel.getSelectedIndex());
            if (!treeViewWrapper.isIndexVisible(selectionModel.getSelectedIndex())) {
                astTreeView.scrollTo(selectionModel.getSelectedIndex());
            }
        }
    }

    private void invalidateAST(boolean error) {
        astTitleLabel.setText("Abstract syntax tree (" + (error ? "error" : "outdated") + ")");
    }


    /** Moves the caret to a position and makes the view follow it. */
    public void moveCaret(int line, int column) {
        codeEditorArea.moveTo(line, column);
        codeEditorArea.requestFollowCaret();
    }


    public ConvenienceNodeWrapper wrapNode(Node node) {
        return codeEditorArea.wrapNode(node);
    }

    @PersistentProperty
    public LanguageVersion getLanguageVersion() {
        return astManager.getLanguageVersion();
    }


    public void setLanguageVersion(LanguageVersion version) {
        astManager.setLanguageVersion(version);
    }


    public Var<LanguageVersion> languageVersionProperty() {
        return astManager.languageVersionProperty();
    }


    public Node getCompilationUnit() {
        return astManager.getCompilationUnit();
    }


    public Val<Node> compilationUnitProperty() {
        return astManager.compilationUnitProperty();
    }


    @PersistentProperty
    public String getText() {
        return codeEditorArea.getText();
    }


    public void setText(String expression) {
        codeEditorArea.replaceText(expression);
    }


    public Val<String> textProperty() {
        return Val.wrap(codeEditorArea.textProperty());
    }


    @PersistentProperty
    public String getAuxclasspathFiles() {
        return auxclasspathFiles.getValue().stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));
    }


    public void setAuxclasspathFiles(String files) {
        List<File> newVal = Arrays.stream(files.split(File.pathSeparator)).map(File::new).collect(Collectors.toList());
        auxclasspathFiles.setValue(newVal);
    }


}
