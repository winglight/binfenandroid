/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cc.binfen.android.customview.wheel;

/**
 * The simple Array wheel adapter
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> implements WheelAdapter {
	
	/** The default items length */
	public static final int DEFAULT_LENGTH = -1;
	
	// items
	private T items[];
	// length
	private int length;

	/**
	 * Constructor
	 * @param items the items
	 * @param length the max items length
	 */
	public ArrayWheelAdapter(T items[], int length) {
		this.items = items;
		this.length = length;
	}
	
	/**
	 * Contructor
	 * @param items the items
	 */
	public ArrayWheelAdapter(T items[]) {
		this(items, DEFAULT_LENGTH);
	}
	
	/**
	 * Contructor
	 * @param items the items
	 * @param defaultItem default Item
	 */
	public ArrayWheelAdapter(T items[],String defaultItem) {
		//如果默认选项不为空，加上默认选项
		if(defaultItem!=null && !"".equals(defaultItem)){
			String[] newItems=new String[items.length+1];
			newItems[0]=defaultItem;
			int i=1;
			for (int j = 0; j < items.length; j++) {
				newItems[i] = (String) items[j];
				i++;
			}
			this.items = (T[]) newItems;
		}else{
			this.items = items;
		}
		
		this.length = DEFAULT_LENGTH;
		
	}

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < items.length) {
			return items[index].toString();
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return items.length;
	}

	@Override
	public int getMaximumLength() {
		return length;
	}

}