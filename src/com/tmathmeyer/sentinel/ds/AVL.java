package com.tmathmeyer.sentinel.ds;

import java.io.Serializable;

public interface AVL<E extends Comparable<E>> extends Iterable<E>, Serializable
{
	/**
	 * 
	 * @return the top level node
	 */
	E getNode();

	/**
	 * 
	 * @return whether the tree has the element in ANY branch
	 */
	boolean hasElement(E e);

	/**
	 * 
	 * @param i the node to be added
	 * @return the AVL tree with the added node
	 */
	AVL<E> add(E i);

	/**
	 * 
	 * @param i the node to be removed
	 * @return the AVL tree sans node
	 */
	AVL<E> remove(E i);

	/**
	 * 
	 * @return whether the tree is empty
	 */
	boolean isEmpty();

	/**
	 * 
	 * @return the left branch of this AVL
	 */
	AVL<E> getLeft();

	/**
	 * 
	 * @return the right branch of this AVL
	 */
	AVL<E> getRight();

	/**
	 * 
	 * @return the maximum depth of this tree
	 */
	int depth();

	/**
	 * 
	 * @return whether the tree is balanced
	 */
	int getBalance();

	/**
	 * this method is not recommended for use, like, ever yo
	 * 
	 * @return force the tree to balance
	 */
	AVL<E> balance();

	/**
	 * for testing purposes only
	 * 
	 * @param depth the depth of this branch in the tree
	 */
	void print(int depth);

	/**
	 * get the element all the way to the right
	 * 
	 * @return the element
	 */
	E rightmost();
	
	/**
	 * Used for finding models with a partial storage in the tree
	 * @param e the possibly unfulfilled model
	 * @return the whole model if it exists, or null otherwise
	 */
	E searchAndReturn(E e);

	int size();
	
	AVL<E> rotateRight();

	AVL<E> rotateLeft();
}