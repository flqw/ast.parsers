package com.github.flqw.ast.parsers.java;

import com.github.flqw.ast.parsers.AstNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public abstract class JavaAstNode extends AstNode<Node> {

	public JavaAstNode(Node node, AstNode<Node> parent) {
		super(node, parent);
	}

	@Override
	public Integer getLineNumber() {
		return node.getBeginLine();
	}

	@Override
	public boolean isRoot() {
		return getUnderlyingNode() instanceof CompilationUnit;
	}

}
