

/*

 * Beryl - A web platform based on XML, XSLT and Java

 *

 * Copyright (C) 2003 Wenzel Jakob <root@wazlaf.org>

 *

 * This program is free software; you can redistribute it and/or

 * modify it under the terms of the GNU Library General Public License

 * as published by the Free Software Foundation; either version 2 of

 * the License, or (at your option) any later version.



 * This program is distributed in the hope that it will be useful,

 * but WITHOUT ANY WARRANTY; without even the implied warranty of

 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 * GNU Library General Public License for more details.

 *

 * You should have received a copy of the GNU Library General Public

 * License along with this program; if not, write to the Free Software

 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 */



package org.QTM.data;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;



/**

 * Simple sorting API - Sorted array list. Like its parent class,

 * this class is not thread-safe!

 * 

 * You should only add objects which implement the

 * Comparable interface!

 *

 * Big-Oh complexities, example applications and sample run times (n=1000, 1ms per operation):

 * O(1)					-	constant time (lookup in a hash table)		-	constant

 * O(log2 M)		-   logarithmic time (lookup in a tree)				-	0.00001 s

 * O(N)				-   linear time (lookup in an array)					-	0.001 s

 * O(N log2(N))	-	good sorting methods								-	0.01 s

 * O(N^2)				-	quadratic time (slow sorting methods)		-	1 s

 * O(N^3)				-	cubic time (bad idea..)								-	17 minutes

 *

 * Benchmarks: (Each benchmark was run 10000 times and averaged on a P4 with 1,6Ghz)

 * 

 * Sorting a random integer list with 20 entries:

 * - Bubble sort		0.0280 ms

 * - Heap sort			0.0219 ms

 * - Selection sort	0.0166 ms

 * - QuickSort 			0.0134 ms

 * - Insertion sort		0.0110 ms

 * Conclusion: Insertion sort is the way to go for small data sets (Especially

 * if you need to dynamically add data).

 * 

 * Sorting a random integer list with 200 entries:

 * - Bubble sort		2.7646 ms

 * - Selection sort 	1.2412 ms

 * - Insertion sort 	0.9486 ms

 * - Heap sort			0.3785 ms

 * - Quick sort			0.1832 ms

 * Conclusion: Quick sort already makes sense. If you have changing data

 * sets involving automatic sorting, choose insertion sort.

 * 

 * 

 * Sorting a random integer list with 20000 entries:

 * - Bubble sort		27170.0 ms

 * - Selection sort 	11336.0 ms

 * - Insertion sort 	9198.5 ms

 * - Heap sort			93.5 ms

 * - Quick sort			54.0 ms

 * Conclusion: In order to sort lists of this size, O(N^2) algorithms are of

 * no use. If your data sets tend to be much larger than this, use heap sort

 * since it involves no recursion. Otherwise quick sort should be preferred.

 *

 * @see Sorter

 * @since Beryl 1.0

 * @author Wenzel Jakob

 */



public class SortedArrayList extends ArrayList {

	/**

	 * Basic sorting algorithm interface

	 */

	public static interface Sorter extends Serializable {

		void sort(ArrayList list);

	}

	

	/**

	 * Insertion Sort

	 * Best case: O(N) comparisons, 0 moves (already sorted)

	 * Worst case: O(N^2/2) comparisons, O(N^2/2) moves (sorted in reverse order)

	 * Average: O(N^2/4) comparisons, O(N^2/4) moves

	 * Stable: yes

	 *	Strategy: Insert and keep sorted

	 * Notes: Efficient when the input is almost in order or when there

	 * are only 5-50 values

	 */

	public static final Sorter INSERTION_SORT = new InsertionSort();



	/**

	 * Heap Sort

	 * All cases: O(N*log2(N)) comparisons, O(N*log2(N)) moves

	 * Stable: No

	 *	Strategy: Priority queue (Select largest value)

	 * Notes: Heap sort has guaranteed O(N*log2(N)) complexity.

	 * However, Quick sort is usually faster. This sort type is also preferrable

	 * for extremely large data sets with several million values since it does not

	 * require massive recursion like quick sort. Performs badly on already sorted

	 * lists.

	 */

	public static final Sorter HEAP_SORT = new HeapSort();



	/**

	 * Quick Sort

	 * Worst case: O(N^2) comparisons, O(N^2) moves

	 * Average: O(N*log2(N)) comparisons, O(N*log2(N)) moves

	 * Stable: No

	 *	Strategy: Divide and Conquer

	 * Notes: This is known to be the fastest common search algorithm, especially

	 * for large data sets. It is massively recursive and performs badly on

	 * lists which are already sorted or contain less than 50 values

	 */

	public static final Sorter QUICK_SORT = new QuickSort();



	private Sorter sortAlgorithm = null;

	private Sorter autoSortAlgorithm = INSERTION_SORT;

	private boolean autoSort = false;



	/**

	 * Create a list with a projected size

	 */

	public SortedArrayList(int projectedSize) {

		super(projectedSize);

		if (projectedSize <= 50)

			this.sortAlgorithm = INSERTION_SORT;

		else

			this.sortAlgorithm = QUICK_SORT;

	}



	/**

	 * Create a list with the default sort algorithm (quick sort)

	 */

	public SortedArrayList() {

		this(QUICK_SORT);

	}



	/**

	 * Create a list with a given sort algorithm

	 * @param sortAlgorithm The sort algorithm

	 */

	public SortedArrayList(Sorter sortType) {

		this.sortAlgorithm = sortType;

	}



	/**

	 * Create a list with a given sort algorithm and an initial data set. Auto

	 * sorting is turned on when using this constructor

	 * @param sortAlgorithm The sort algorithm

	 * @param collection The initial data set

	 */

	public SortedArrayList(Sorter sortType, Collection collection) {

		super(collection);

		this.sortAlgorithm = sortType;

		autoSort = true;
		
		sort();

	}



	/**

	 * Create a list with an initial data set (quick sort). Auto sorting

	 * is turned on when using this constructor

	 * @param collection The initial data set

	 */

	public SortedArrayList(Collection collection) {

		this(QUICK_SORT, collection);

		autoSort = true;

	}



	/**

	 * Create a list with a given sort algorithm and an initial data set

	 * @param sortAlgorithm The sort algorithm

	 * @param array The initial data set

	 */

	public SortedArrayList(Sorter sortType, Object array[]) {

		super(array.length);

		this.sortAlgorithm = sortType;

		for (int i = 0; i < array.length; i++) {

			super.add(array[i]);

		}

	}



	public boolean getAutoSort() {

		return autoSort;

	}



	public Sorter getAutoSortAlgorithm() {

		return autoSortAlgorithm;

	}



	public Sorter getSortAlgorithm() {

		return sortAlgorithm;

	}



	/**

	 * Turn auto-sorting on or off. It is turned off by default.

	 * @param autoSort True to enable auto sorting

	 */



	public void setAutoSort(boolean autoSort) {

		this.autoSort = autoSort;

	}



	/**

	 * Set the sorting algorithm used for automatic sorting. Insertion sort

	 * is used by default since this is the fastest algorithm when the data

	 * is already ordered.

	 * @param autoSortAlgorithm The sort algorithm implementation

	 */

	public void setAutoSortAlgorithm(Sorter autoSortAlgorithm) {

		this.autoSortAlgorithm = autoSortAlgorithm;

	}



	/**

	 * Set the sorting algorithm

	 * @param sortAlgorithm The sort algorithm implementation

	 */

	public void setSortAlgorithm(Sorter sortAlgorithm) {

		this.sortAlgorithm = sortAlgorithm;

	}



	/**

	 * Sort the table with the given sorting algorithm

	 */

	public void sort() {

		sortAlgorithm.sort(this);

	}



	/**

	 * Bring a partially ordered list back into order using the

	 * auto sorting algorithm (insertion sort by default)

	 */

	public void resort() {

		autoSortAlgorithm.sort(this);

	}



	public boolean addAll(Collection c) {

		boolean result = super.addAll(c);

		if (autoSort)

			resort();

		return result;

	}



	/**

	 * If you intend to add several objects and if you are using

	 * auto sorting, turn it off before adding the objects

	 * and turn it back on afterwards. Make sure you call

	 * <tt>resort</tt>

	 */

	public boolean add(Object o) {

		super.add(o);

		if (autoSort)

			resort();

		return true;

	}



	/**

	 * If you intend to add several objects and if you are using

	 * auto sorting, turn it off before adding the objects

	 * and turn it back on afterwards. Make sure you call

	 * <tt>resort</tt>

	 */

	public void add(int index, Object element) {

		super.add(index, element);

		if (autoSort)

			resort();

	}



	/**

	 * Return an iterator which traverses the list backwards

	 * @return

	 */

	public Iterator inverseIterator() {

		return new InvItr();

	}



	private class InvItr implements Iterator {

		private int cursor = size()-1;

		

		public boolean hasNext() {

			return cursor != -1;

		}



		public Object next() {

			Object value = get(cursor);

			cursor--;

			return value;

		}

		

		public void remove() {

			throw new UnsupportedOperationException();

		}

	}

}


