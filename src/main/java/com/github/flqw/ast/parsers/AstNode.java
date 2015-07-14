package com.github.flqw.ast.parsers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for AST Nodes returned by the respective parsers.
 *
 * @author f_wagner
 *
 */
public abstract class AstNode<N> {

	private static final Logger LOG = LoggerFactory.getLogger(AstNode.class);

	protected AstNode<N> parent;
	protected N node;
	protected List<AstNode<N>> children = new ArrayList<>();

	public AstNode(N node, AstNode<N> parent) {
		this.node = node;
		this.parent = parent;
		if (parent != null) {
			parent.children.add(this);
		}
	}

	/**
	 * Get the underlying ast node returned by the parser.
	 *
	 * @return
	 */
	public Object getUnderlyingNode() {
		return node;
	}

	/**
	 * Get a list of all children of the node.
	 *
	 * @return
	 */
	public List<AstNode<N>> getChildren() {
		return children;
	}

	/**
	 * Get the parent of this node. May be null for the root.
	 *
	 * @return
	 */
	public AstNode<N> getParent() {
		return parent;
	}

	/**
	 * Get the end offset of the node. This is an absolute position.
	 *
	 * @return
	 */
	public int getEndOffset() {
		return getStartOffset() + getLength();
	}

	/**
	 * Get the type of the node.
	 *
	 * @return
	 */
	public String getType() {
		return getUnderlyingNode().getClass().getSimpleName();
	}

	/**
	 * Get the node at the specified absolute offset.
	 *
	 * @param offset
	 * @return
	 */
	public AstNode<N> getNodeAt(int offset) {
		// offset < 0 is for method chaining of methods which will
		// return -1 if an index or node is not found (baby optimization)
		if (offset < 0) {
			return null;
		}

		for (AstNode<N> child : getChildren()) { // Check children for more specific results
			AstNode<N> found = child.getNodeAt(offset);

			if (found != null && found.getStartOffset() != 0) {
				return found;
			}
		}

		return offset >= getStartOffset() && offset <= getEndOffset() ? this : null;
	}

	/**
	 * Find a node based on the underlying node.
	 * @param underlyingNode
	 * @return
	 */
	public AstNode<N> getNodeWithUnderlyingNode(Object underlyingNode) {
		if (underlyingNode.equals(node)) {
			return this;
		}

		for (AstNode<N> child : getChildren()) {
			AstNode<N> found = child.getNodeWithUnderlyingNode(underlyingNode);

			if (found != null) {
				return found;
			}
		}

		return null;
	}

	public Object getProperty(String propertyName) {
		Method method;
		try {
			// Currently only supports "get" properties.
			method = getUnderlyingNode().getClass().getMethod("get" + propertyName);
			return method.invoke(getUnderlyingNode());
		} catch (Exception e) {
			LOG.error("Error getting property '{}' from underlying node.");
			LOG.error("{}", e);
			return null;
		}

	}

	@Override
	public String toString() {
		String s = getType();
		if (getLineNumber() != null) {
			s += " (Line " + getLineNumber() + ")";
		}
		return s;
	}

	/**
	 * Get the line number of the node. Is only for informational purposes.
	 * May be null if not easily possible to aquire.
	 * @return
	 */
	public Integer getLineNumber() {
		return null;
	}

	/**
	 * Get the start position of the node. This is an absolute position.
	 *
	 * @return
	 */
	public abstract int getStartOffset();

	/**
	 * Get the length of the node.
	 *
	 * @return
	 */
	public abstract int getLength();

	/**
	 * Returns true if this node is the root node of the AST.
	 *
	 * @return
	 */
	public abstract boolean isRoot();

}
