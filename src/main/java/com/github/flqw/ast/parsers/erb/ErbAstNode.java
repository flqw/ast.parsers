package com.github.flqw.ast.parsers.erb;

import org.jrubyparser.ast.Node;

import com.github.flqw.ast.parsers.AstNode;
import com.github.flqw.ast.parsers.ruby.RubyAstNode;

public abstract class ErbAstNode extends RubyAstNode {

	public ErbAstNode(Node node, AstNode<Node> parent) {
		super(node, parent);
	}

	@Override
	public abstract int getStartOffset();
	@Override
	public abstract int getEndOffset();

	@Override
	public Integer getLineNumber() {
		return null;
	}

}
