

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



import java.util.ArrayList;



public class QuickSort implements SortedArrayList.Sorter {

	private void quickSort(ArrayList list, int low0, int high0) {

		int low = low0, high = high0;

		if (low >= high) {

			return;

		} else if (low == high - 1) {

			if (((Comparable)list.get(low)).compareTo(list.get(high)) > 0) {

				Object temp = list.get(low);

				list.set(low, list.get(high));

				list.set(high, temp);

			}

			return;

		}

		

		Object pivot = list.get((low + high) / 2);

		list.set((low + high) / 2, list.get(high));

		list.set(high, pivot);

		

		while (low < high) {

			while (((Comparable)list.get(low)).compareTo(pivot) <= 0 && low < high) {

				low++;

			}

			while (((Comparable)list.get(high)).compareTo(pivot) >= 0 && low < high) {

				high--;

			}

			if (low < high) {

				Object temp = list.get(low);

				list.set(low, list.get(high));

				list.set(high, temp);

			}

		}

		list.set(high0, list.get(high));

		list.set(high, pivot);

		quickSort(list, low0, low - 1);

		quickSort(list, high + 1, high0);

	}

	

	public void sort(ArrayList list) {

		quickSort(list, 0, list.size() - 1);

	}

}