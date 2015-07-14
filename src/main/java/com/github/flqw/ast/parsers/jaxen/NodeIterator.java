/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.github.flqw.ast.parsers.jaxen;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.flqw.ast.parsers.AstNode;

public abstract class NodeIterator<N> implements Iterator<AstNode<N>> {

    private AstNode<N> node;

    public NodeIterator(AstNode<N> contextNode) {
        node = getFirstNode(contextNode);
    }

    @Override
	public boolean hasNext() {
        return node != null;
    }

    @Override
	public AstNode<N> next() {
        if (node == null) {
            throw new NoSuchElementException();
        }
        AstNode<N> ret = node;
        node = getNextNode(node);
        return ret;
    }

    @Override
	public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract AstNode<N> getFirstNode(AstNode<N> contextNode);

    protected abstract AstNode<N> getNextNode(AstNode<N> contextNode);

    public AstNode<N> getPreviousSibling(AstNode<N> contextNode) {
        AstNode<N> parentNode = contextNode.getParent();
        if (parentNode != null) {
            int prevPosition = getPositionFromParent(contextNode) - 1;
            if (prevPosition >= 0) {
                return parentNode.getChildren().get(prevPosition);
            }
        }
        return null;
    }

    private int getPositionFromParent(AstNode<N> contextNode) {
        AstNode<N> parentNode = contextNode.getParent();

        for (int i = 0; i < parentNode.getChildren().size(); i++) {
            if (parentNode.getChildren().get(i) == contextNode) {
                return i;
            }
        }
        throw new RuntimeException("Node was not a child of it's parent ???");
    }

    public AstNode<N> getNextSibling(AstNode<N> contextNode) {
        AstNode<N> parentNode = contextNode.getParent();
        if (parentNode != null) {
            int nextPosition = getPositionFromParent(contextNode) + 1;
            if (nextPosition < parentNode.getChildren().size()) {
                return parentNode.getChildren().get(nextPosition);
            }
        }
        return null;
    }

    public AstNode<N> getFirstChild(AstNode<N> contextNode) {
        if (contextNode.getChildren().size() > 0) {
            return contextNode.getChildren().get(0);
        } else {
            return null;
        }
    }

    public AstNode<N> getLastChild(AstNode<N> contextNode) {
        if (contextNode.getChildren().size() > 0) {
            return contextNode.getChildren().get(contextNode.getChildren().size() - 1);
        } else {
            return null;
        }
    }
}