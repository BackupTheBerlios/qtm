

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



public class InsertionSort implements SortedArrayList.Sorter {

	public void sort(ArrayList list) {

		for (int i = 1; i < list.size(); i++) {

			Object item = list.get(i);

			int j = i;

			while ((j > 0) && ((Comparable) list.get(j - 1)).compareTo(item) > 0) {

				list.set(j, list.get(j - 1));

				j--;

			}

			list.set(j, item);

		}

	}

}


