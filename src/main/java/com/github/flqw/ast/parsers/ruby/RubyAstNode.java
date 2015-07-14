package com.github.flqw.ast.parsers.ruby;

import org.jrubyparser.ast.Node;
import org.jrubyparser.ast.RootNode;

import com.github.flqw.ast.parsers.AstNode;

public class RubyAstNode extends AstNode<Node> {

	public RubyAstNode(Node node, AstNode<Node> parent) {
		super(node, parent);
	}

	@Override
	public Integer getLineNumber() {
		return node.getPosition().getStartLine();
	}

	@Override
	public int getStartOffset() {
		return node.getPosition().getStartOffset();
	}

	@Override
	public int getLength() {
		return getEndOffset() - getStartOffset();
	}

	@Override
	public int getEndOffset() {
		return node.getPosition().getEndOffset();
	}

	@Override
	public boolean isRoot() {
		return getUnderlyingNode() instanceof RootNode;
	}

}
