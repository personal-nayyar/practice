package DSimpl;

interface CustomSort<E> {
    void selectionSort(E[] array);
    void bubbleSort(E[] array);
    void insertionSort(E[] array);
    void mergeSort(E[] array, int l, int h);
    void quickSort(E[] array, int l, int h);
    void heapSort(E[] array);
}

public class CustomSortImpl<E> implements CustomSort<E>, Comparable<E> {
    @Override
    public void selectionSort(E[] array) {
        // sort using selection sort
        for(int i=0; i<array.length-1; i++){
            int minIndex = i;
            for(int j=i+1; j<array.length; j++){
                if(((Comparable<E>)array[minIndex]).compareTo(array[j]) > 0){
                    minIndex = j;
                }
            }
            E temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
        }
    }

    @Override
    public void bubbleSort(E[] array) {
        // sort using bubble sort
        for(int i=0; i<array.length-1; i++){
            for(int j=0; j<array.length-i-1; j++){
                if(((Comparable<E>)array[j]).compareTo(array[j+1]) > 0){
                    E temp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = temp;
                }
            }
        }
    }

    @Override
    public void insertionSort(E[] array) {
        // sort using insertion sort
        for(int i=1; i<array.length; i++){
            E key = array[i];
            int j = i-1;
            while(j>=0 && ((Comparable<E>)array[j]).compareTo(key) > 0){
                array[j+1] = array[j];
                j--;
            }
            array[j+1] = key;
        }
    }


    @Override
    public void mergeSort(E[] array, int l, int h) {
        // sort using merge sort
        if(l<h){
            int m = l + (h-l)/2;
            mergeSort(array, l, m);
            mergeSort(array, m+1, h);
            merge(array, l, m, h);
        }
    }

    private void merge(E arr[], int l, int m, int h) {
        int n1 = m - l + 1;
        int n2 = h - m;
        E[] arr1 = (E[]) new Object[n1];
        E[] arr2 = (E[]) new Object[n2];
        for (int i = 0; i < n1; i++) {
            arr1[i] = arr[l + i];
        }
    }

    @Override
    public void quickSort(E[] array, int l, int h) {
        // sort using quick sort
        if (l < h) {
            int pi = partition(array, l, h);
            quickSort(array, l, pi - 1);
            quickSort(array, pi + 1, h);
        }
    }

    private int partition(E[] array, int l, int h) {
        E pivot = array[h];
        int i = l - 1;
        for (int j = l; j < h; j++) {
            if (((Comparable<E>) array[j]).compareTo(pivot) < 0) {
                i++;
                E temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        E temp = array[i + 1];
        array[i + 1] = array[h];
        array[h] = temp;
        return i + 1;
    }

    @Override
    public void heapSort(E[] array) {
        // sort using heap sort
        int n = array.length;
        // build heap O(n)
        for(int i=n/2-1; i>=0; i--){
            heapify(array, n, i);
        }
    }

    private void heapify(E[] array, int n, int i) {
        int largest = i;
        int l = 2*i+1;
        int r = 2*i+2;
        if(l<n && ((Comparable<E>)array[l]).compareTo(array[largest]) > 0){
            largest = l;
        }
        if(r<n && ((Comparable<E>)array[r]).compareTo(array[largest]) > 0){
            largest = r;
        }
        if(largest != i){
            E temp = array[i];
            array[i] = array[largest];
            array[largest] = temp;
            heapify(array, n, largest);
        }
    }

    @Override
    public int compareTo(E o) {
        return 0;
    }
}
