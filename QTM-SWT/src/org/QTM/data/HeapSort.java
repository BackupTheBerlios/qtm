

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



public class HeapSort implements SortedArrayList.Sorter {

	public void sort(ArrayList list) {

		for (int i = (list.size() / 2) - 1; i >= 0; i--) {

			siftDown(list, i, list.size());

		}



		for (int i = list.size() - 1; i >= 1; i--) {

			Object temp = list.get(0);

			list.set(0, list.get(i));

			list.set(i, temp);

			siftDown(list, 0, i - 1);

		}

	}



	private void siftDown(ArrayList list, int root, int bottom) {

		int maxChild;

		boolean done = false;



		while ((root * 2 < bottom) && !done) {

			if (root * 2 == bottom)

				maxChild = root * 2;

			else if (((Comparable) list.get(root * 2)).compareTo(list.get(root * 2 + 1)) > 0)

				maxChild = root * 2;

			else

				maxChild = root * 2 + 1;



			try {

				if (((Comparable) list.get(root)).compareTo(list.get(maxChild)) < 0) {

					Object temp = list.get(root);

					list.set(root, list.get(maxChild));

					list.set(maxChild, temp);

					root = maxChild;

				} else {

					done = true;

				}



			} catch (RuntimeException e) {

				throw e;

			}

		}

	}

}