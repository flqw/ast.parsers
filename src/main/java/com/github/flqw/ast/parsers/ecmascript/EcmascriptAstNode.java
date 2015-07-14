package com.github.flqw.ast.parsers.ecmascript;

import org.mozilla.javascript.ast.AstRoot;

import com.github.flqw.ast.parsers.AstNode;

public class EcmascriptAstNode extends AstNode<org.mozilla.javascript.ast.AstNode> {

	public EcmascriptAstNode(org.mozilla.javascript.ast.AstNode node, AstNode<org.mozilla.javascript.ast.AstNode> parent) {
		super(node, parent);
	}

	@Override
	public Integer getLineNumber() {
		return node.getLineno();
	}

	@Override
	public int getStartOffset() {
		return node.getAbsolutePosition();
	}

	@Override
	public int getLength() {
		return node.getLength();
	}

	@Override
	public boolean isRoot() {
		return getUnderlyingNode() instanceof AstRoot;
	}

}
