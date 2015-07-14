package com.github.flqw.ast.parsers;

import java.util.List;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

import com.github.flqw.ast.parsers.jaxen.DocumentNavigator;

public interface AstUtility<N> {

	public static final DocumentNavigator<?> NAVIGATOR = new DocumentNavigator<>();

	/**
	 * The superclass of the nodes that the parser returns. Used
	 * to identify nodes when analyzing the node's properties.
	 * @return
	 */
	public Class<N> getNodeSuperClass();


	public AstNode<N> parse(String sourceCode) throws Exception;

	/**
	 * Perform an xPath query on the given node.
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default List<Object> findXPath(AstNode<?> rootNode, String xPath) throws JaxenException {
		return new BaseXPath(xPath, NAVIGATOR).selectNodes(rootNode);
	}

	/**
	 * Get the language the AST Utility handles. This is for displaying to the user.
	 * @return
	 */
	public String getLanguage();

}
