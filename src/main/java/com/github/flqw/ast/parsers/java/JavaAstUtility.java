package com.github.flqw.ast.parsers.java;

import java.io.StringReader;

import com.github.flqw.ast.parsers.AstNode;
import com.github.flqw.ast.parsers.AstUtility;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;

public class JavaAstUtility implements AstUtility<Node> {

	public static final JavaAstUtility INSTANCE = new JavaAstUtility();

	@Override
	public Class<Node> getNodeSuperClass() {
		return Node.class;
	}

	@Override
	public AstNode<Node> parse(String sourceCode) throws Exception {
		return buildInternal(JavaParser.parse(new StringReader(sourceCode), false), null, sourceCode);
	}

	private AstNode<Node> buildInternal(Node node, AstNode<Node> parent, String sourceCode) {

		// The column seems to describe the character positions, but we need caret positions. So
		// for the start indices we have to subtract 1.
		int startOffset = lineAndColumnToOffset(sourceCode, node.getBeginLine(), node.getBeginColumn() - 1);
		int endOffset = lineAndColumnToOffset(sourceCode, node.getEndLine(), node.getEndColumn());

		JavaAstNode astNode = new JavaAstNode(node, parent) {

			@Override
			public int getStartOffset() {
				return startOffset;
			}

			@Override
			public int getLength() {
				return getEndOffset() - getStartOffset();
			}

			@Override
			public int getEndOffset() {
				return endOffset;
			}

		};

		for (Node child : node.getChildrenNodes()) {
			buildInternal(child, astNode, sourceCode);
		}

		return astNode;
	}

	private int lineAndColumnToOffset(String sourceCode, int line, int col) {

		// Line must be 1-based.

		String[] lines = sourceCode.split("(?<=\\n)"); // Split at \n, but keep it.

		int offset = 0;
		for (int i = 0; i < line - 1; i++) {
			offset += lines[i].length();
		}

		// Now the target line.
		offset += col;
		// We have to substract every tab before the column.
		String l = lines[line - 1];
		// Replace the tabs with something we know won't be in the line so we
		// can count it afterwards.
		String partialLine = replaceTabs(l).substring(0, col);
		int tabs = countReplacedTabs(partialLine);
		offset -= tabs * TAB_WIDTH;
		offset += tabs;

		if (sourceCode.length() < offset) {
			System.out.println("Something went wrong here (result " + offset + ")");
			return 0;
		}

		return offset;
	}

	private static final int TAB_WIDTH = 8;
	// Tabs are replaced by TAB_WIDTH \0 characters.
	private static final String TAB_REPLACEMENT = new String(new char[TAB_WIDTH]);

	private int countReplacedTabs(String l) {
		return (l.length() - l.replace(TAB_REPLACEMENT, "").length()) / TAB_WIDTH;
	}

	private String replaceTabs(String l) {
		return l.replace("\t", TAB_REPLACEMENT);
	}

	@Override
	public String getLanguage() {
		return "Java 8";
	}

}
