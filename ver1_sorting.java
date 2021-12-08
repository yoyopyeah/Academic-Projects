package finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry; // You may need it to implement fastSort
//todo: the most basic quicksort
public class ver1_sorting {
    public static <K, V extends Comparable> ArrayList<K> fastSort(HashMap<K, V> results) {
        // todo: ADD YOUR CODE HERE
        // quick sort: chose a pivot, switch elements with the pivot until the pivot itself is at the right place
        // improvement: maybe later try pivot at the middle and two walls on the sides
        
        ArrayList<K> sortedUrls = new ArrayList<K>();
        sortedUrls.addAll(results.keySet());    //Start with unsorted list of urls
        
        int N = sortedUrls.size();
        quickSort(results, sortedUrls, 0, N - 1);
        return sortedUrls;
    }
    
    private static <K, V extends Comparable> void quickSort(HashMap<K, V> results, ArrayList<K> list, int leftIndex, int rightIndex) {
        if (leftIndex < rightIndex) {
            int i = placeAndDivide(results, list, leftIndex, rightIndex);
            quickSort(results, list, leftIndex, i - 1);
            quickSort(results, list, i + 1, rightIndex);
        }
    }
    
    private static <K, V extends Comparable> int placeAndDivide(HashMap<K, V> results, ArrayList<K> list, int leftIndex, int rightIndex) {
        // First go down the list until prev < cur, then have the cur as the pivot, compare with the rest only.
        V pivot = results.get(list.get(rightIndex)); //set pivot to be the value of the right most key's value
        int wall = leftIndex - 1;
        for (int i = leftIndex; i < rightIndex; i++) {
            if (results.get(list.get(i)).compareTo(pivot) > 0) {
                //!!! ASCENDING order
                // if Key at i is greater than pivot, move the key behind the wall by a switch
                // if the key is smaller, then do nothing
                wall++;
                K temp = list.get(i);
                list.set(i, list.get(wall));
                list.set(wall, temp);
            }
        }
        // at the end, move the pivot right behind the wall
        K temp = list.get(rightIndex);
        list.set(rightIndex, list.get(wall + 1));
        list.set(wall + 1, temp);
        return wall + 1;
    }
}
    
    //*******************************************************************************************************
    /*
    public static <K, V extends Comparable> ArrayList<K> fastSort(HashMap<K, V> results) {
        // todo: ADD YOUR CODE HERE
        // quick sort: chose a pivot, switch elements with the pivot until the pivot itself is at the right place
        // improvement: maybe later try pivot at the middle and two walls on the sides
        ArrayList<Entry<K, V>> sortedUrls = new ArrayList<Entry<K,V>>();
        //have a list of entries, so we can get values directly
        sortedUrls.addAll(results.entrySet());	//Start with unsorted list of urls
        
        int N = sortedUrls.size();
        quickSort(sortedUrls, 0, N-1);
        
        ArrayList<K> answer = new ArrayList<K>();
        for (Entry entry : sortedUrls) {
            answer.add((K) entry.getKey());
        }
        
        return answer;
    }
    
    private static <K, V extends Comparable> void quickSort(ArrayList<Entry<K,V>> list, int leftIndex, int rightIndex) {
        // idk,,, maybe when there are just two Keys, then just compare and no need to go though the hustle?
        while (leftIndex < rightIndex) {
            if (rightIndex - leftIndex < 10) {
                insertionSort(list, leftIndex, rightIndex);
                return;
            } else {
                int pivot = placeAndDivide(list, leftIndex, rightIndex);
                if (pivot - leftIndex < rightIndex - pivot) {
                    quickSort(list, leftIndex, pivot-1);
                    leftIndex = pivot +  1;
                } else {
                    quickSort(list, pivot+1, rightIndex);
                    rightIndex = pivot - 1;
                }
            }
        }
    }
    
    private static <K, V extends Comparable> int placeAndDivide(ArrayList<Entry<K,V>> list, int leftIndex, int rightIndex) {
        // First go down the list until prev < cur, then have the cur as the pivot, compare with the rest only.
        V pivot = list.get(rightIndex).getValue(); //set pivot to be the value of the right most key's value
        int wall = leftIndex - 1;
        for (int i = leftIndex; i < rightIndex; i++) {
            if (list.get(i).getValue().compareTo(pivot) > 0) {
                //!!! DESCENDING order
                // if Key at i is greater than pivot, move the key behind the wall by a switch
                // if the key is smaller, then do nothing
                wall++;
                Entry temp = list.get(i);
                list.set(i, list.get(wall));
                list.set(wall, temp);
            }
        }
        // at the end, move the pivot right behind the wall
        Entry temp = list.get(rightIndex);
        list.set(rightIndex, list.get(wall+1));
        list.set(wall+1, temp);
        return wall+1;
    }
    
    private static <K, V extends Comparable> void insertionSort(ArrayList<Entry<K,V>> list, int leftIndex, int rightIndex) {
        for (int i = leftIndex; i <= rightIndex; i++) {
            Entry<K,V> element = list.get(i);
            int k = i;
            while (k > 0 && (element.getValue().compareTo(list.get(k-1).getValue()) > 0)) {
                list.set(k, list.get(k-1));
                k--;
            }
            list.set(k, element);
        }
    }
   */


/*
private static <K, V extends Comparable> int placeAndDivide(ArrayList<Entry<K,V>> list, int leftIndex, int rightIndex) {
		// todo: fix this (choosing random pivot)
		int randomIndex = leftIndex + (rightIndex % (rightIndex - leftIndex + 1));
		V pivot = list.get(randomIndex).getValue(); //set pivot to be the value of the right most key's value
		int wall = leftIndex - 1;
		for (int i = leftIndex; i <= rightIndex; i++) {
//			if (i != randomIndex){
				if (list.get(i).getValue().compareTo(pivot) > 0) {
					//!!! DESCENDING order
					// if Key at i is greater than pivot, move the key behind the wall by a switch
					// if the key is smaller, then do nothing
					wall++;
					swap(list, i, wall);
				}
//			}
		}
		// at the end, move the pivot right behind the wall
		swap(list, wall + 1, randomIndex);
		return wall+1;
	}
 */
   