package com.github.flqw.ast.parsers.jaxen;

import java.util.Collections;
import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;

import com.github.flqw.ast.parsers.AstNode;

@SuppressWarnings({ "serial", "unchecked" })
public class DocumentNavigator<N> extends DefaultNavigator {

	@Override
	public String getAttributeName(Object arg0) {
		return ((Attribute<N>) arg0).getName();
	}

	@Override
	public String getAttributeNamespaceUri(Object arg0) {
		return "";
	}

	@Override
	public String getAttributeQName(Object arg0) {
		return ((Attribute<N>) arg0).getName();
	}

	@Override
	public String getAttributeStringValue(Object arg0) {
		return ((Attribute<N>) arg0).getStringValue();
	}

	@Override
	public String getCommentStringValue(Object arg0) {
		return "";
	}

	@Override
	public String getElementName(Object node) {
		return ((AstNode<N>) node).getType();
	}

	@Override
	public String getElementNamespaceUri(Object arg0) {
		return "";
	}

	@Override
	public String getElementQName(Object arg0) {
		return getElementName(arg0);
	}

	@Override
	public String getElementStringValue(Object arg0) {
		return "";
	}

	@Override
	public String getNamespacePrefix(Object arg0) {
		return "";
	}

	@Override
	public String getNamespaceStringValue(Object arg0) {
		return "";
	}

	@Override
	public String getTextStringValue(Object arg0) {
		return "";
	}

	@Override
	public boolean isAttribute(Object arg0) {
		return arg0 instanceof Attribute;
	}

	@Override
	public boolean isComment(Object arg0) {
		return false;
	}

	@Override
	public boolean isDocument(Object arg0) {
		if (arg0 instanceof AstNode) {
			return ((AstNode<N>) arg0).isRoot();
		}
		return false;
	}

	@Override
	public boolean isElement(Object arg0) {
		return arg0 instanceof AstNode;
	}

	@Override
	public boolean isNamespace(Object arg0) {
		return false;
	}

	@Override
	public boolean isProcessingInstruction(Object arg0) {
		return false;
	}

	@Override
	public boolean isText(Object arg0) {
		return false;
	}

	@Override
	public XPath parseXPath(String arg0) {
		return null;
	}

	@Override
	public Object getParentNode(Object arg0) {
		if (arg0 instanceof AstNode) {
			return ((AstNode<N>) arg0).getParent();
		}
		if (arg0 instanceof Attribute) {
			return ((Attribute<N>) arg0).getParent();
		}
		// can't navigate to parent node...
		return null;
	}

	@Override
	public Iterator<Attribute<N>> getAttributeAxisIterator(Object arg0) {
//		if (arg0 instanceof AttributeNode) {
//			return ((AttributeNode) arg0).getAttributeIterator();
//		} else {
			return new AttributeAxisIterator<N>((AstNode<N>) arg0);
//		}
	}

	@Override
	public Object getDocumentNode(Object contextNode) {
		if (isDocument(contextNode)) {
			return contextNode;
		}
		if (null == contextNode) {
			throw new RuntimeException("contextNode may not be null");
		}
		return getDocumentNode(getParentNode(contextNode));
	}

	/**
	 * Get an iterator over all of this node's children.
	 *
	 * @param contextNode
	 *            The context node for the child axis.
	 * @return A possibly-empty iterator (not null).
	 */
	@Override
	public Iterator<AstNode<N>> getChildAxisIterator(Object contextNode) {
		return new NodeIterator<N>((AstNode<N>) contextNode) {
			@Override
			protected AstNode<N> getFirstNode(AstNode<N> node) {
				return getFirstChild(node);
			}

			@Override
			protected AstNode<N> getNextNode(AstNode<N> node) {
				return getNextSibling(node);
			}
		};
	}

	/**
	 * Get a (single-member) iterator over this node's parent.
	 *
	 * @param contextNode
	 *            the context node for the parent axis.
	 * @return A possibly-empty iterator (not null).
	 */
	@Override
	public Iterator<AstNode<N>> getParentAxisIterator(Object contextNode) {
		if (isAttribute(contextNode)) {
			return new SingleObjectIterator(((Attribute<N>) contextNode).getParent());
		}
		AstNode<N> parent = ((AstNode<N>) contextNode).getParent();
		if (parent != null) {
			return new SingleObjectIterator(parent);
		} else {
			return Collections.emptyIterator();
		}
	}

	/**
	 * Get an iterator over all following siblings.
	 *
	 * @param contextNode
	 *            the context node for the sibling iterator.
	 * @return A possibly-empty iterator (not null).
	 */
	@Override
	public Iterator<AstNode<N>> getFollowingSiblingAxisIterator(Object contextNode) {
		return new NodeIterator<N>((AstNode<N>) contextNode) {
			@Override
			protected AstNode<N> getFirstNode(AstNode<N> node) {
				return getNextNode(node);
			}

			@Override
			protected AstNode<N> getNextNode(AstNode<N> node) {
				return getNextSibling(node);
			}
		};
	}

	/**
	 * Get an iterator over all preceding siblings.
	 *
	 * @param contextNode
	 *            The context node for the preceding sibling axis.
	 * @return A possibly-empty iterator (not null).
	 */
	@Override
	public Iterator<AstNode<N>> getPrecedingSiblingAxisIterator(Object contextNode) {
		return new NodeIterator<N>((AstNode<N>) contextNode) {
			@Override
			protected AstNode<N> getFirstNode(AstNode<N> node) {
				return getNextNode(node);
			}

			@Override
			protected AstNode<N> getNextNode(AstNode<N> node) {
				return getPreviousSibling(node);
			}
		};
	}

	/**
	 * Get an iterator over all following nodes, depth-first.
	 *
	 * @param contextNode
	 *            The context node for the following axis.
	 * @return A possibly-empty iterator (not null).
	 */
	@Override
	public Iterator<AstNode<N>> getFollowingAxisIterator(Object contextNode) {
		return new NodeIterator<N>((AstNode<N>) contextNode) {
			@Override
			protected AstNode<N> getFirstNode(AstNode<N> node) {
				if (node == null) {
					return null;
				} else {
					AstNode<N> sibling = getNextSibling(node);
					if (sibling == null) {
						return getFirstNode(node.getParent());
					} else {
						return sibling;
					}
				}
			}

			@Override
			protected AstNode<N> getNextNode(AstNode<N> node) {
				if (node == null) {
					return null;
				} else {
					AstNode<N> n = getFirstChild(node);
					if (n == null) {
						n = getNextSibling(node);
					}
					if (n == null) {
						return getFirstNode(node.getParent());
					} else {
						return n;
					}
				}
			}
		};
	}

	/**
	 * Get an iterator over all preceding nodes, depth-first.
	 *
	 * @param contextNode
	 *            The context node for the preceding axis.
	 * @return A possibly-empty iterator (not null).
	 */
	@Override
	public Iterator<AstNode<N>> getPrecedingAxisIterator(Object contextNode) {
		return new NodeIterator<N>((AstNode<N>) contextNode) {
			@Override
			protected AstNode<N> getFirstNode(AstNode<N> node) {
				if (node == null) {
					return null;
				} else {
					AstNode<N> sibling = getPreviousSibling(node);
					if (sibling == null) {
						return getFirstNode(node.getParent());
					} else {
						return sibling;
					}
				}
			}

			@Override
			protected AstNode<N> getNextNode(AstNode<N> node) {
				if (node == null) {
					return null;
				} else {
					AstNode<N> n = getLastChild(node);
					if (n == null) {
						n = getPreviousSibling(node);
					}
					if (n == null) {
						return getFirstNode(node.getParent());
					} else {
						return n;
					}
				}
			}
		};
	}

}
