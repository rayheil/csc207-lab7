package lab7;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * The Language INtegrated Query (LINQ) library.
 * It allows users to perform common queries and transformations on Iterable objects.
 * @author Dawn Nye
 * @author Ethan Wills
 * @author Ray Heil
 */
public final class LINQ
{
	/**
	 * Determines if each element of {@code source} satisfies {@code predicate}.
	 * @param <T> The iterable type.
	 * @param source The source iterable object.
	 * @param predicate The predicate all objects in {@code source} must satisfy.
	 * @return Returns true if each element of {@code source} satisfies {@code predicate} and false otherwise.
	 * @throws NullPointerException Thrown if {@code source} or {@code predicate} is null.
	 */
	public static <T> boolean All(Iterable<? extends T> source, SingleInputPredicate<T> predicate)
	{
		if(source == null || predicate == null)
			throw new NullPointerException();
		
		for(T t : source)
		{
			if(!predicate.Evaluate(t))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns a new iterable object with {@code obj} appended to the end of {@code source}.
	 * <br><br>
	 * For example, given the sequence {1,2,3} and the element 0, we produce the sequence {1,2,3,0}.
	 * @param <T> The iterable type.
	 * @param source The source iterable object to occur first.
	 * @param obj The item to append to the end of the sequence.
	 * @return Returns a new iterable object with {@code obj} following the elements of {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 * @author Dawn Nye
	 */
	public static <T> Iterable<T> Append(Iterable<? extends T> source, T obj)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					public boolean hasNext()
					{return obj_unused;}
					
					public T next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						// Return the next item of the source iterator if it exists
						if(iter.hasNext())
							return iter.next();
						
						// If it does not exist, return the appended item and exhaust this iterator
						obj_unused = false;
						return obj;
					}
			
					/**
					 * Iterator over the source.
					 */
					protected Iterator<? extends T> iter = source.iterator();
					
					/**
					 * Whether the appended object is still unused (has not been returned).
					 */
					protected boolean obj_unused = true;
				};
			}
		};
	}
	
	/**
	 * Creates a new iterable object which concatenates the sequence {@code source_b} to the sequence {@code source_a}.
	 * <br><br>
	 * For example, given the sequences {1,2,3} and {4,5,6}, we produce the sequence {1,2,3,4,5,6}.
	 * @param <T> The iterable type.
	 * @param source_a The squence which occurs first.
	 * @param source_b The sequence which occurs second.
	 * @return Returns a new iterable object which concatenates the sequence {@code source_b} to the sequence {@code source_a}.
	 * @throws NullPointerException Thrown if {@code source_a} or {@code source_b} is null.
	 */
	public static <T> Iterable<T> Concatenate(Iterable<? extends T> source_a, Iterable<? extends T> source_b)
	{
		if (source_a == null || source_b == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			@Override
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					@Override
					public boolean hasNext()
					{
						return (iter_a.hasNext() || iter_b.hasNext());
					}

					@Override
					public T next()
					{
						if (!hasNext())
							throw new NoSuchElementException();
						
						// First return any items in iter_a, only then begin to use items from iter_b
						if (iter_a.hasNext())
							return iter_a.next();
						return iter_b.next();
					}
					
					/**
					 * Iterator for the first source.
					 */
					protected Iterator<? extends T> iter_a = source_a.iterator();
					
					/**
					 * Iterator for the second source.
					 */
					protected Iterator<? extends T> iter_b = source_b.iterator();
				};
			}			
		};
	}
	
	/**
	 * Determines if {@code source} contains an instance of {@code obj}.
	 * @param <T> The iterable type.
	 * @param source The source iterable object.
	 * @param obj The object to look for in {@code source}.
	 * @return Returns true if {@code source} contains {@code obj} and false otherwise.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> boolean Contains(Iterable<? extends T> source, T obj)
	{
		if(source == null)
			throw new NullPointerException();
		
		for(T t : source)
		{
			if(t.equals(obj))
				return true;
		}
		return false;
	}
	
	/**
	 * Counts the number of elements of an iterable object.
	 * @param <T> The type of object to iterate.
	 * @param source The iterable object to count.
	 * @return Returns the number of elements in {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> int Count(Iterable<? extends T> source)
	{
		if (source == null)
			throw new NullPointerException();
		
		int count = 0;
		for (@SuppressWarnings("unused") T t : source)
			count++;
		return count;
	}
	
	/**
	 * Filters an iterable object so that each element iterated appears exactly once.
	 * The order of the new sequence will be such that if a and b appear in the new sequence, then a first occurs in {@code source} before b first occurs.
	 * <br><br>
	 * For example, given the sequence {1,2,3,4,3,3,1}, we produce the sequence {1,2,3,4}.
	 * @param <T> The type of object to iterate.
	 * @param source The iterable object to filter.
	 * @return Returns a new iterable object which iterates each value exactly once without duplicates.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> Iterable<T> Distinct(Iterable<? extends T> source)
	{
		if (source == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					@Override
					public boolean hasNext()
					{
						// If a nextItem already exists, no problem!
						if(nextItem != null)
							return true;
					
						/* If it does not exist, search for one in the source iterator.
						 * If we find a unique element that's great!
						 * If not, we really are out of items to iterate over.
						 */
						while(it.hasNext())
						{
							T temp = it.next();
							
							if(!seenItems.contains(temp))
							{
								nextItem = temp;
								seenItems.add(temp);
								return true;
							}
						}
						return false;
					}
					
					@Override
					public T next()
					{
						// Since we always check hasNext, it will continue to cycle nextItem as needed
						if(!hasNext())
							throw new NoSuchElementException();
						
						T ret = nextItem;
						nextItem = null; // set nextItem to null so a new one will be found
						return ret;
					}
					
					/**
					 * Iterator for the source.
					 */
					protected Iterator<? extends T> it = source.iterator();
					
					/**
					 * Items that have already been iterated through.
					 * It will likely not be noticeable in our use case,
					 * but HashSet.contains is O(1) as opposed to O(n).
					 */
					protected HashSet<T> seenItems = new HashSet<T>();
					
					/**
					 * The next item to be returned. Calculated as needed.
					 */
					protected T nextItem = null;
				};
			}
		};
	}
	
	/**
	 * Obtains the element at index {@code index} of the sequence {@code source}.
	 * @param <T> The iterable type.
	 * @param source The sequence to sample from.
	 * @param index The index of the sequence to query.
	 * @return Returns the element at {@code index} of the sequence {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or at least {@code Count(source)}.
	 */
	public static <T> T ElementAt(Iterable<T> source, int index)
	{
		if (source == null)
			throw new NullPointerException();
		
		if (index < 0 || index >= Count(source))
			throw new IndexOutOfBoundsException();
		
		T currentItem = null;
		Iterator<T> it = source.iterator();
		for (int i = 0; i < index; i++)
		{
			// Since we already checked bounds, there's no need to check in the loop
			currentItem = it.next();
		}
		return currentItem;
	}
	
	/**
	 * Creates an empty iterable object with type {@code T}.
	 * @param <T> The type to iterate.
	 * @return Returns an empty iterable object with type {@code T}.
	 */
	public static <T> Iterable<T> Empty()
	{return new LinkedList<T>();}
	
	/**
	 * Determines if {@code source} is empty.
	 * @param <T> The iterable type.
	 * @param source The source sequence.
	 * @return Returns true if the sequence is empty and false otherwise.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> boolean Empty(Iterable<? extends T> source)
	{
		if (source == null)
			throw new NullPointerException();
		
		return (!source.iterator().hasNext());
	}
	
	/**
	 * Computes the set difference of {@code source} minus {@code except}.
	 * The order of the output elements is the same as the order they appear in {@code source}.
	 * <br><br>
	 * For example, given the sequences {1,2,3} and {2,4}, we produce the sequence {1,3}.
	 * @param <T> The iterable type.
	 * @param source The source set.
	 * @param except The set to exclude.
	 * @return Returns a new iterable object which contains all elements of {@code source} not in {@code except}.
	 * @throws NullPointerException Thrown if {@code source} or {@code except} is null.
	 */
	public static <T> Iterable<T> Except(Iterable<? extends T> source, Iterable<? extends T> except)
	{
		if (source == null || except == null)
			throw new NullPointerException();
		
		// We want all elements of source that are NOT in except 
		return Where(source, t -> !Contains(except, t));
	}
	
	/**
	 * Iterates over the first items of a list of pairs.
	 * <br><br>
	 * For example, given the sequence {(1,2),(3,4),(5,6)}, we produce the sequence {1,3,5}.
	 * @param <A> The first type of values to iterate.
	 * @param <B> The second type of values to iterate.
	 * @param source The iterable object to provide items of type {@code A}.
	 * @return Returns a new iterable object which iterates the first elements of {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <A,B> Iterable<A> First(Iterable<Pair<A,B>> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<A>()
		{
			public Iterator<A> iterator()
			{
				return new Iterator<A>()
				{
					@Override
					public boolean hasNext() 
					{return it.hasNext();}

					@Override
					public A next()
					{
						if (!hasNext())
							throw new NoSuchElementException();
						return it.next().Item1;
					}
					
					Iterator<Pair<A,B>> it = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Computes the intersection of {@code source_b} minus {@code source_b}.
	 * The order of the output sequence is the same order that the elements appear in {@code source_a}
	 * <br><br>
	 * For example, given the sequences {1,2,3,4} and {2,5,4}, we produce the sequence {2,4}.
	 * @param <T> The iterable type.
	 * @param source_a The first set.
	 * @param source_b The second set.
	 * @return Returns a new iterable object which contains all elements in both {@code source_a} and {@code source_b}.
	 * @throws NullPointerException Thrown if {@code source_a} or {@code source_b} is null.
	 */
	public static <T> Iterable<T> Intersect(Iterable<? extends T> source_a, Iterable<? extends T> source_b)
	{
		if (source_a == null || source_b == null)
			throw new NullPointerException();
		
		// We want all elements of source_a that are ALSO in source_b
		return Where(source_a, t -> Contains(source_b, t));
	}
	
	/**
	 * Determines the maximum element of {@code source}.
	 * <br><br>
	 * For example, given the sequence {1,2,3} and ordinary integer comparison, we produce the value 3.
	 * @param <T> The iterable type.
	 * @param source The source to sample from.
	 * @param cmp The means by which we compare {@code T} types.
	 * @return Returns the maximum element of {@code source}. If there is more than one, it returns the first encountered in the sequence.
	 * @throws NullPointerException Thrown if {@code source} or {@code cmp} is null.
	 * @throws NoSuchElementException Thrown if {@code source} is empty.
	 */
	public static <T> T Max(Iterable<? extends T> source, Comparator<T> cmp)
	{
		if (source == null)
			throw new NullPointerException();
		
		if (Empty(source))
			throw new NoSuchElementException();
		
		Iterator<? extends T> it = source.iterator();
		T maxItem = it.next();
		T currentItem = null;
		while (it.hasNext()) 
		{
			currentItem = it.next();
			if (cmp.compare(maxItem, currentItem) < 0)
				maxItem = currentItem;
		}
		
		return maxItem;
	}
	
	/**
	 * Determines the minimum element of {@code source}.
	 * <br><br>
	 * For example, given the sequence {1,2,3} and ordinary integer comparison, we produce the value 1.
	 * @param <T> The iterable type.
	 * @param source The source to sample from.
	 * @param cmp The means by which we compare {@code T} types.
	 * @return Returns the minimum element of {@code source}. If there is more than one, it returns the first encountered in the sequence.
	 * @throws NullPointerException Thrown if {@code source} or {@code cmp} is null.
	 * @throws NoSuchElementException Thrown if {@code source} is empty.
	 */
	public static <T> T Min(Iterable<? extends T> source, Comparator<T> cmp)
	{
		if (source == null)
			throw new NullPointerException();
		
		if (Empty(source))
			throw new NoSuchElementException();
		
		Iterator<? extends T> it = source.iterator();
		T minItem = it.next();
		T currentItem = null;
		while (it.hasNext()) 
		{
			currentItem = it.next();
			if (cmp.compare(minItem, currentItem) > 0)
				minItem = currentItem;
		}
		
		return minItem;
	}
	
	/**
	 * Determines if no element of {@code source} satisfies {@code predicate}.
	 * @param <T> The iterable type.
	 * @param source The source iterable object.
	 * @param predicate The predicate no objects in {@code source} can satisfy.
	 * @return Returns true if no element of {@code source} satisfies {@code predicate} and false otherwise.
	 * @throws NullPointerException Thrown if {@code source} or {@code predicate} is null.
	 */
	public static <T> boolean None(Iterable<? extends T> source, SingleInputPredicate<T> predicate)
	{
		if (source == null)
			throw new NullPointerException();
		
		// None passing is the same as all not passing the predicate.
		// Also, we need another little lambda expression since it's predicate.Evaluate
		return All(source, t -> !predicate.Evaluate(t));
	}
	
	/**
	 * Pairs two iterable objects together.
	 * The new iterator will proceed until one source runs out of items.
	 * <br><br>
	 * For example, given the sequences {1,2,3} and {4,5,6,7}, we produce the sequence {(1,4),(2,5),(3,6)}.
	 * @param <A> The first type of values to iterate.
	 * @param <B> The second type of values to iterate.
	 * @param a_source The iterable object to provide items of type {@code A}.
	 * @param b_source The iterable object to provide items of type {@code B}.
	 * @return Returns a new iterable object which pairs the elements of {@code a_source} and {@code b_source} together.
	 * @throws NullPointerException Thrown if {@code a_source} or {@code b_source} is null.
	 */
	public static <A,B> Iterable<Pair<A,B>> Pair(Iterable<? extends A> a_source, Iterable<? extends B> b_source)
	{
		return new Iterable<Pair<A,B>>()
		{
			@Override
			public Iterator<Pair<A, B>> iterator()
			{
				return new Iterator<Pair<A,B>>()
				{
					@Override
					public boolean hasNext()
					{
						// There is only a next pair if both source iterators have a next item
						return (a_it.hasNext() && b_it.hasNext());
					}

					@Override
					public Pair<A, B> next()
					{
						if (!hasNext())
							throw new NoSuchElementException();
						
						return new Pair<A,B>(a_it.next(), b_it.next());
					}
					
					Iterator<? extends A> a_it = a_source.iterator();
					Iterator<? extends B> b_it = b_source.iterator();
				};
			}
		};
	}
	
	/**
	 * Returns a new iterable object with {@code obj} prepended to the beginning of {@code source}.
	 * <br><br>
	 * For example, given the sequence {1,2,3} and the object 0, we produce the sequence {0,1,2,3}.
	 * @param <T> The iterable type.
	 * @param source The source iterable object to occur last.
	 * @param obj The item to prepend to the front of the sequence.
	 * @return Returns a new iterable object with {@code obj} appearing before the elements of {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> Iterable<T> Prepend(Iterable<? extends T> source, T obj)
	{
		if (source == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			@Override
			public Iterator<T> iterator() 
			{
				return new Iterator<T>()
				{
					@Override
					public boolean hasNext()
					{
						// This or is necessary because the source iterator may start with no items
						return (obj_unused || it.hasNext());
					}

					@Override
					public T next()
					{
						if (!hasNext())
							throw new NoSuchElementException();
						
						// Return the appended object first, then move onto the source iterator
						if (obj_unused)
						{
							obj_unused = false;
							return obj;
						}
						return it.next();
					}
					
					boolean obj_unused = true;
					Iterator<? extends T> it = source.iterator();
				};
			}
		};
	}
	
	/*==========================================================================
	 * 
	 * MIDDLE OF THE DOCUMENT
	 * There are 13 unfinished functions after this point, I believe. This should be a
	 * little bit less than half.
	 * 
	 *==========================================================================*/
	
	/**
	 * Makes an iterable object read only.
	 * In other words, if an Iterator of {@code source} could use is {@code remove} method, the wrapper around it will not support that operation.
	 * @param <T> The type of values to iterate.
	 * @param source The iterable object to make read only.
	 * @return Returns a new iterable object which is read only.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	//TODO implement
	public static <T> Iterable<T> ReadOnly(Iterable<? extends T> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			@Override
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public T next() {
						if (!hasNext())
							throw new NoSuchElementException();
						return it.next();
					}
					
					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
					
					/**
					 * Source iterator
					 */
					Iterator<? extends T> it = source.iterator();
				};
			}	
		};
	}
	
	/**
	 * Reverses the elements of {@code source}.
	 * <br><br>
	 * For example, given the sequence {1,2,3}, we produce the sequence {3,2,1}.
	 * @param <T> The iterable type.
	 * @param source The source sequence.
	 * @return Returns a new iterable object which produces the elements of {@code source} in reverse order.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> Iterable<T> Reverse(Iterable<? extends T> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<T>(){
			
			//make this able to be used by prepend
			Iterable<T> returnIterable = this;
			
			public Iterator<T> iterator(){
				
				return new Iterator<T>(){
					//standard hasNext
					public boolean hasNext()
					{return unfinished;}
					
					public T next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						//If we have a next in source, we prepend it to new iterator
						if(iter.hasNext())
						{
							Prepend(returnIterable, iter.next());
							return iter.next();
						}
						
						unfinished = false;
						return null;
					}
					protected Iterator<? extends T> iter = source.iterator();
					protected boolean unfinished = true;
				};
			}
		};
	}
	
	/**
	 * Iterates over the second items of a list of pairs.
	 * <br><br>
	 * For example, given the sequence {(1,2),(3,4),(5,6)}, we produce the sequence {2,4,6}.
	 * @param <A> The first type of values to iterate.
	 * @param <B> The second type of values to iterate.
	 * @param source The iterable object to provide items of type {@code B}.
	 * @return Returns a new iterable object which iterates the second elements of {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <A,B> Iterable<B> Second(Iterable<Pair<A,B>> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<B>(){
			
			public Iterator<B> iterator(){
				
				return new Iterator<B>(){
					
					//We have a next if source has a next
					public boolean hasNext()
					{return iter.hasNext();}
					
					public B next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						//if source has a next, add the B element of the pair to return Iterable
						if(iter.hasNext())
							return iter.next().Item2;
						
						return null;
					}
					protected Iterator<Pair<A,B>> iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Projects each element of a sequence into a new form.
	 * <br><br>
	 * For example, given the Integer sequence {1,2,3} and the transformation of [input] * 2.5, we produce the Double sequence {2.5,5.0,7.5}.
	 * @param <I> The input type.
	 * @param <O> The output type.
	 * @param source The iterable object to project into new forms.
	 * @param transformation The function transforming input objects into a new form.
	 * @return Returns a new iterable object which transforms the original values into a new form according to {@code transformation}.
	 * @throws NullPointerException Thrown if {@code source} or {@code transformation} is null.
	 */
	public static <I,O> Iterable<O> Select(Iterable<? extends I> source, SingleInputTransformation<I,O> transformation)
	{
		if(source == null || transformation == null)
			throw new NullPointerException();
		
		return new Iterable<O>(){
			
			public Iterator<O> iterator(){
				
				return new Iterator<O>(){
					
					//if source has a next, we have a next
					public boolean hasNext()
					{return iter.hasNext();}
					
					public O next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						//if source has a next, we transform and return it
						if(iter.hasNext())
							return transformation.Evaluate(iter.next());
						
						return null;
					}
					protected Iterator<? extends I> iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Projects each element of a sequence into a new form.
	 * <br><br>
	 * For example, given the Integer sequence {1,2,3} and the transformation of [previous] + [input] * 2.5, we produce the Double sequence {2.5,7.5,15.0}.
	 * @param <I> The input type.
	 * @param <O> The output type.
	 * @param source The iterable object to project into new forms.
	 * @param transformation The function transforming input objects into a new form.
	 * @return Returns a new iterable object which transforms the original values into a new form according to {@code transformation}.
	 * @throws NullPointerException Thrown if {@code source} or {@code transformation} is null.
	 */
	public static <I,O> Iterable<O> Select(Iterable<? extends I> source, DoubleInputTransformation<I,O> transformation)
	{
		if(source == null || transformation == null)
			throw new NullPointerException();
		
		return new Iterable<O>(){
			
			public Iterator<O> iterator(){
				
				return new Iterator<O>(){
					
					//if iter has a next, we have a next
					public boolean hasNext()
					{return iter.hasNext();}
					
					public O next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						//if iter has a next, we load the transformed prev into prev and return it
						if(iter.hasNext())
						{
							prev = transformation.Evaluate(iter.next(), prev);
							return prev;
						}
						
						return null;
					}
					protected O prev = null;
					protected Iterator<? extends I> iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Checks if two sequences are equal.
	 * @param <T> The sequence type.
	 * @param s1 The first sequence.
	 * @param s2 The second sequence.
	 * @return Returns true if {@code s1} and {@code s2} are the same sequence and false otherwise.
	 * @throws NullPointerException Thrown if {@code s1} or {@code s2} is null.
	 */
	public static <T> boolean SequenceEqual(Iterable<? extends T> s1, Iterable<? extends T> s2)
	{
		if(s1 == null || s2 == null)
			throw new NullPointerException();
		
		Iterator<? extends T> iter1 = s1.iterator();
		Iterator<? extends T> iter2 = s2.iterator();
		
		//while there's a next in both sources
		while(iter1.hasNext() &&  iter2.hasNext())
		{
			//if they are not the same return false
			if(!(iter1.next().equals(iter2.next())))
				return false;
		}
		
		//if one source does not have a next but the other does they are not equal
		if(iter1.hasNext() || iter2.hasNext())
			return false;
		
		//if they are the same length and every element is the same in the same order
		//the sequences are equal
		return true;
	}
	
	/**
	 * Sorts the elements of {@code source} according to {@code cmp} using {@code Arrays.sort(T[],Comparator<? super T>)}.
	 * <br><br>
	 * For example, given the sequence {1,2,3,4} and we sort evens before odds and then in ascending order of value, we produce the sequence {2,4,1,3}.
	 * @param <T> The iterable type.
	 * @param source The source sequence.
	 * @param cmp The means why which we compare {@code T} types.
	 * @return Returns a new iterable object which produces the elements of {@code source} in ascending order.
	 * @throws NullPointerException Thrown if {@code source} or {@code cmp} is null.
	 */
	public static <T> Iterable<T> Sort(Iterable<? extends T> source, Comparator<T> cmp)
	{
		if(source == null || cmp == null)
			throw new NullPointerException();
		
		//convert source to an array, sort it, and convert it back into an iterable
		T[] arr = ToArray(source);
		Arrays.sort(arr, cmp);
		return ToIterable(arr);
	}
	
	/**
	 * Computes the 'sum' of a sequence.
	 * Despite the name, the operation specified by {@code operation} can be any binary operation.
	 * This function applies {@code operation} to each successive element of {@code source} with the result of the previous invocation.
	 * The lefthand side of the first invocation of {@code operation} is null.
	 * <br><br>
	 * For example, given the sequence {1,2,3,4} and the transformation of [previous] * [input], we produce the Double sequence {null * 1 = 1,2,6,24}.
	 * @param <T> The iterable type.
	 * @param source The source sequence.
	 * @param operation The operation to perform.
	 * @return Returns the 'sum' of the sequence {@code source}. In the degenerate 'sum' when {@code source} is empty, this will return null.
	 * @throws NullPointerException Thrown if {@code source} or {@code operation} is null.
	 */
	public static <T> T Sum(Iterable<? extends T> source, DoubleInputTransformation<T,T> operation)
	{
		if(source == null || operation == null)
			throw new NullPointerException();
		
		T prevResult = null;
		Iterator<? extends T> iterator = source.iterator();
		
		//while source has a next, perform the provided operation on the next and previous
		while(iterator.hasNext())
		{
			prevResult = operation.Evaluate(iterator.next(), prevResult);
		}
		return prevResult;
	}
	
	/**
	 * Transforms {@code source} into an array.
	 * The returned array is exactly long enough to hold the elements of {@code source}.
	 * Further, the array is 'safe' in the sense that this class and method does not keep a reference to it.
	 * The user is free to modify or discard it as desired.
	 * @param <T> The iterable type.
	 * @param source The source sequence.
	 * @return Returns an array containing the elements of {@code source} in the order the appear in the sequence.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> T[] ToArray(Iterable<? extends T> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		
		Iterator<? extends T> iter = source.iterator();
		int count = 0;
		
		//determine the length of source and create an array of that length
		for(T t : source)
		{count++;}
		T[] arr = (T[])new Object[count];
		
		//reset counter
		count = 0;
		
		//step back through iterator and load in each element into the array
		while(iter.hasNext())
		{
			arr[count] = iter.next();
			count++;
		}
		return arr;
	}
	
	/**
	 * Iterates over an array.
	 * @param <T> The type of the array.
	 * @param src The source array to iterate over.
	 * @return Returns an iterable object that iterates over the provided array.
	 * @throws NullPointerException Thrown if {@code src} is null.
	 */
	public static <T> Iterable<T> ToIterable(T[] src)
	{
		if(src == null)
			throw new NullPointerException();
	
		return new Iterable<T>() {
			
			public Iterator<T> iterator() {
				
				return new Iterator<T>()
				{
					//if currentIndex is less than the length of src, there's a next
					public boolean hasNext()
					{return currentIndex < src.length;}
					
					//next is the element at the next index in src
					public T next()
					{return src[currentIndex++];}
					
					protected int currentIndex = 0;
				};
			}
		};
	}
	
	/**
	 * Computes the union of {@code source_a} minus {@code source_b}.
	 * The order of the output sequence is all elements of {@code source_a} in the order they appear and then all elements of {@code source_b} in the order they appear that are not in {@code source_a}.
	 * <br><br>
	 * For example, given the sequences {1,2,3} and {3,4,5}, we produce the sequence {1,2,3,4,5}.
	 * @param <T> The iterable type.
	 * @param source_a The first set.
	 * @param source_b The second set.
	 * @return Returns a new iterable object which contains all elements in either {@code source_a} or {@code source_b}.
	 * @throws NullPointerException Thrown if {@code source_a} or {@code source_b} is null.
	 */
	public static <T> Iterable<T> Union(Iterable<? extends T> source_a, Iterable<? extends T> source_b)
	{
		if(source_a == null || source_b == null)
			throw new NullPointerException();
		
		Iterator<? extends T> iterb = source_b.iterator();
		
		while(iterb.hasNext())
		{
			T temp = iterb.next();
			if(!(Contains(source_a, temp)))
				Append(source_a, temp);
		}
		return (Iterable<T>) source_a;
	}
	
	/**
	 * Filters an iterable object by iterating only values which satisfy {@code predicate}.
	 * The output order of elements is the same as they appear in {@code source}.
	 * <br><br>
	 * For example, given the sequence {1,2,3,4,5,6} and the predicate IsEven, we produce the sequence {2,4,6}.
	 * @param <T> The type of values to iterate.
	 * @param source The iterable object to filter.
	 * @param predicate The predicate which decides whether to include or exclude iterated values.
	 * @return Returns a new iterable object which iterates only values which satisfy {@code predicate}.
	 * @throws NullPointerException Thrown if {@code source} or {@code predicate} is null.
	 */
	public static <T> Iterable<T> Where(Iterable<? extends T> source, SingleInputPredicate<T> predicate)
	{
		if(source == null || predicate == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					public boolean hasNext()
					{
						if(nextItem != null)
							return true;
					
						while(nextItem == null)
						{
							T temp = iter.next();
							if(predicate.Evaluate(temp))
							{
								nextItem = temp;
								return true;
							}
						}
						
						return false;
					}
					
					public T next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						T ret = nextItem;
						nextItem = null;
						return ret;
					}
					
					protected T nextItem = null;
					protected HashSet<T> seen = new HashSet<T>();
					protected Iterator<? extends T> iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Zips two sequences together into one.
	 * This method will take the ith element from each sequence, provide them as a Pair to {@code zip}, and produce the ith element of the output sequence.
	 * The output sequence's length will be equal to the minimum length of the sequences {@code source_a} and {@code source_b}.
	 * <br><br>
	 * For example, given the Integer sequences {1,2,3} and {4,5,6} and the transformation (a + b) / 2.0, we produce the Double sequence {2.5,3.5,4.5}.
	 * @see {@code Pair(Iterable,Iterable)}
	 * @param <A> The first sequence's iterated type.
	 * @param <B> The second sequence's iterated type.
	 * @param <O> The output sequence's iterated type.
	 * @param source_a The first input sequence.
	 * @param source_b The second input sequence.
	 * @param zip The function which will zip elements from {@code source_a} and {@code source_b} together.
	 * @return Returns a new sequence which zips the elements of {@code source_a} and {@code source_b} together until one sequence runs out of elements.
	 * @throws NullPointerException Thrown if {@code source_a}, {@code source_b}, or {@code zip} is null.
	 */
	public static <A,B,O> Iterable<O> Zip(Iterable<? extends A> source_a, Iterable<? extends B> source_b, SingleInputTransformation<Pair<A,B>,O> zip)
	{
		if(source_a == null || source_b == null || zip == null)
			throw new NullPointerException();
		Iterator<? extends A> itera = source_a.iterator();
		Iterator<? extends B> iterb = source_b.iterator();
		return new Iterable<O>()
			{
				public Iterator<O> iterator()
				{
					return new Iterator<O>()
					{
						public boolean hasNext()
						{return not_done;}
						
						public O next()
						{
							if(!hasNext())
								throw new NoSuchElementException();
							
							if(itera.hasNext() && iterb.hasNext())
								return (O) zip.Evaluate(new Pair(itera.next(), iterb.next()));
							
							
							not_done = false;
							return null;
						}
						protected boolean not_done = true;
					};
				}
			};
	}
	
	/**
	 * An immutable pair class.
	 * @author Dawn Nye
	 * @param <S> The first type of item to store.
	 * @param <T> The second type of item to store.
	 */
	public static class Pair<S,T>
	{
		/**
		 * Pairs two elements together.
		 * @param s The first item.
		 * @param t The second item.
		 */
		public Pair(S s, T t)
		{
			Item1 = s;
			Item2 = t;
			
			return;
		}
		
		/**
		 * Creates a shallow copy of {@code p}.
		 * @param p The pair to duplicate.
		 */
		public Pair(Pair<? extends S,? extends T> p)
		{
			Item1 = p.Item1;
			Item2 = p.Item2;
			
			return;
		}
		
		@Override public boolean equals(Object obj)
		{
			if(obj == null)
				return false;
			
			if(this == obj)
				return true;
			
			if(obj instanceof Pair)
			{
				Pair p = (Pair)obj;
				return (Item1 == p.Item1 || Item1 != null && Item1.equals(p.Item1)) && (Item2 == p.Item2 || Item2 != null && Item2.equals(p.Item2));
			}
			
			return false;
		}
		
		@Override public String toString()
		{return "(" + Item1 + ", " + Item2 + ")";}
		
		@Override public int hashCode()
		{return Item1 == null ? (Item2 == null ? 0 : Item2.hashCode()) : (Item1.hashCode() + (Item2 == null ? 0 : (Item2.hashCode() << 5) - Item2.hashCode()));}
		
		/**
		 * The first item of this pair.
		 */
		public final S Item1;
		
		/**
		 * The second item of this pair.
		 */
		public final T Item2;
	}
	
	/**
	 * Determines the value of a single input predicate.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface SingleInputPredicate<T>
	{
		/**
		 * Determines the value of a single input predicate.
		 */
		public abstract boolean Evaluate(T input);
	}
	
	/**
	 * Transforms a single input into a new form.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface SingleInputTransformation<I,O>
	{
		/**
		 * Transforms {@code input} into a new form.
		 */
		public abstract O Evaluate(I input);
	}
	
	/**
	 * Transforms a single input into a new form.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface DoubleInputTransformation<I,O>
	{
		/**
		 * Transforms {@code input} into a new form.
		 * {@code previous} is provided to give context.
		 * The value provided is null when there is no previous output value.
		 */
		public abstract O Evaluate(I input, O previous);
	}
}
