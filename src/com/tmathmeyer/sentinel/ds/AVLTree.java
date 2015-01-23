package com.tmathmeyer.sentinel.ds;

import java.util.Iterator;

public class AVLTree<E extends Comparable<E>> implements AVL<E>
{
    private static final long serialVersionUID = -7670287066978883652L;
	private final E node;
	private final AVL<E> left;
	private final AVL<E> right;
	
	private final int balance;
	private final int depth;

	private final int size;
	
	
	@Override
    public String toString()
    {
	    return "AVLTree [(" + node + "), left=" + left + ", right=" + right + "]";
    }

	public AVLTree(E i, AVL<E> l, AVL<E> r)
	{
		left = l;
		right = r;
		node = i;
		
		int ll = left.depth();
		int rr = right.depth();
		
		depth = Math.max(ll, rr) + 1;
		
		balance = ll - rr;
		
		size = 1 + l.size() + r.size();
	}
	
	public AVLTree(E i)
	{
		this(i, new EmptyAVLTree<E>(), new EmptyAVLTree<E>());
	}

	@Override
	public E getNode()
	{
		return node;
	}

	@Override
	public AVL<E> add(E i)
	{
		int v = i.compareTo(getNode());
		if (v > 0)
		{
			return new AVLTree<E>(node, left, right.add(i));//.balance();
		}
		else if (v < 0)
		{
			return new AVLTree<E>(node, left.add(i), right);//.balance();
		}
		
		return this;
	}

	@Override
	public AVL<E> remove(E i)
	{
		int compare = i.compareTo(node);
		
		if (compare == 0)
		{
			if (left.depth() + right.depth() == 0)
			{
				return new EmptyAVLTree<E>();
			}
			
			if (left.depth() == 0)
			{
				return right;
			}
			
			if (right.depth() == 0)
			{
				return left;
			}
			
			E lrr = getLeft().rightmost();
			
			return new AVLTree<>(lrr, left.remove(lrr), right);//.balance();
			
		}
		else if(compare > 0)
		{
			return new AVLTree<E>(getNode(), getLeft(), getRight().remove(i));
		}
		return new AVLTree<E>(getNode(), getLeft().remove(i), getRight());
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public AVL<E> getLeft()
	{
		return this.left;
	}

	@Override
	public AVL<E> getRight()
	{
		return this.right;
	}

	@Override
	public int depth()
	{
		return depth;
	}

	@Override
	public int getBalance()
	{
		return balance;
	}

	@Override
	public AVL<E> balance()
	{
		int bal = getBalance();
		if (Math.abs(bal) < 2)
		{
			return this;
		}

		if (bal > 1)
		{
			AVL<E> Y = getLeft();
			if (Y.getBalance() == -1)
			{
				Y = Y.rotateRight();
			}
			return rotateLeft();
		}
		
		AVL<E> Y = getRight();
		if (Y.getBalance() == 1)
		{
			Y = Y.rotateLeft();
		}
		return rotateRight();
	}

	@Override
	public void print(int depth)
	{
		for (int i = 0; i < depth; i++)
		{
			System.out.print("  ");
		}
		System.out.println(this.getNode().toString());
		getLeft().print(depth + 1);
		getRight().print(depth + 1);
	}

	@Override
	public boolean hasElement(E e)
	{
		if (this.getNode().equals(e))
		{
			return true;
		}
		if (this.getNode().compareTo(e) > 0)
		{
			return getLeft().hasElement(e);
		}
		return getRight().hasElement(e);
	}

	@Override
    public E rightmost()
    {
	    if (getRight().depth() == 0)
	    {
	    	return node;
	    }
	    
	    return right.rightmost();
    }

	@Override
    public AVL<E> rotateRight()
    {
	    return new AVLTree<E>(getRight().getNode(),
	    		              new AVLTree<E>(getNode(), getLeft(), getRight().getLeft()),
	    		              getRight().getRight());
    }

	@Override
    public AVL<E> rotateLeft()
    {
		return new AVLTree<E>(getLeft().getNode(),
	              new AVLTree<E>(getNode(), getRight(), getLeft().getRight()),
	              getLeft().getLeft());
    }

	@Override
    public Iterator<E> iterator()
    {
	    return new Iterator<E>(){
	    	
	    	Iterator<E> l = left.iterator();
	    	Iterator<E> r = right.iterator();
	    	boolean pos = true;
	    	
			@Override
            public boolean hasNext()
            {
	            return l.hasNext() || pos || r.hasNext();
            }

			@Override
            public E next()
            {
	            if (l.hasNext())
	            {
	            	return l.next();
	            }
            	if (pos)
            	{
            		pos = !pos;
            		return node;
            	}
	            return r.next();
            }
	    	
	    };
    }

	@Override
    public E searchAndReturn(E e)
    {
	    int comp = e.compareTo(node);
	    if (comp == 0)
	    {
	    	return node;
	    }
	    return (comp<0?left:right).searchAndReturn(e);
    }

	@Override
    public int size()
    {
	    return size;
    }

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((left == null) ? 0 : left.hashCode());
	    result = prime * result + ((node == null) ? 0 : node.hashCode());
	    result = prime * result + ((right == null) ? 0 : right.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    AVLTree<?> other = (AVLTree<?>) obj;
	    if (left == null)
	    {
		    if (other.left != null)
			    return false;
	    } else if (!left.equals(other.left))
		    return false;
	    if (node == null)
	    {
		    if (other.node != null)
			    return false;
	    } else if (!node.equals(other.node))
		    return false;
	    if (right == null)
	    {
		    if (other.right != null)
			    return false;
	    } else if (!right.equals(other.right))
		    return false;
	    return true;
    }
}