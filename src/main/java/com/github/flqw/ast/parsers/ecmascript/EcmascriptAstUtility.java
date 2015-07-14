package com.github.flqw.ast.parsers.ecmascript;

import java.util.Stack;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;

import com.github.flqw.ast.parsers.AstNode;
import com.github.flqw.ast.parsers.AstUtility;

public class EcmascriptAstUtility implements AstUtility<org.mozilla.javascript.ast.AstNode> {

	public static final EcmascriptAstUtility INSTANCE = new EcmascriptAstUtility();

	@Override
	public String getLanguage() {
		return "Ecmascript 3";
	}

	@Override
	public Class<org.mozilla.javascript.ast.AstNode> getNodeSuperClass() {
		return org.mozilla.javascript.ast.AstNode.class;
	}

	@Override
	public AstNode<org.mozilla.javascript.ast.AstNode> parse(String sourceCode) throws Exception {
		return new InternalParser().parse(sourceCode);
	}

	// We have to use a inner class here, because we need a new instance to clear
	// the stacks and a parser might only be used once.
	public static class InternalParser {

		private static CompilerEnvirons config = new CompilerEnvirons();

		static {
			// Be as forgiving as possible.
			config.setRecordingComments(false);
			config.setRecordingLocalJsDocComments(false);
			config.setWarnTrailingComma(false);
			config.setLanguageVersion(Context.VERSION_1_8);
			// IDE Mode is memory-heavy since it reads the whole code into memory first.
			// config.setIdeMode(true); // Scope's don't appear to get set right without this
			config.setReservedKeywordAsIdentifier(true);
			config.setOptimizationLevel(0);
			config.setRecoverFromErrors(false);
		}

		private Parser parser = new Parser(config);

		public AstNode<org.mozilla.javascript.ast.AstNode> parse(String sourceCode) throws Exception {
			return buildInternal(parser.parse(sourceCode, "", 1));
		}

		/**
		 * The code below originated in the PMD source code. Thanks for that. The visitor pattern in the parser is hard to use.
		 */

		// The nodes having children built.
		private Stack<EcmascriptAstNode> nodes = new Stack<>();

		// The Rhino nodes with children to build.
		private Stack<org.mozilla.javascript.ast.AstNode> parents = new Stack<>();

		/**
		 * Build a wrapping AST tree.
		 *
		 * @param astNode
		 * @return The RootNode
		 */
		private AstNode<org.mozilla.javascript.ast.AstNode> buildInternal(org.mozilla.javascript.ast.AstNode astNode) {

			EcmascriptAstNode parent = nodes.isEmpty() ? null : nodes.peek();
			// Create a Node
			EcmascriptAstNode node = new EcmascriptAstNode(astNode, parent);

			// Build the children...
			nodes.push(node);
			parents.push(astNode);
			astNode.visit(this::visit);
			nodes.pop();
			parents.pop();

			return node;
		}

		private boolean visit(org.mozilla.javascript.ast.AstNode node) {
			if (parents.peek() == node) {
				return true;
			} else {
				buildInternal(node);
				return false;
			}
		}

	}

}
