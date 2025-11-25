package code.heap;

import utils.DSAUtils;

public interface Heap{
    void heapify(int index);
    int getSize();
    int parent(int index);
    int left(int index);
    int right(int index);
    void insert(int key);
    int extract();

}

class MinHeap2 implements  Heap{
    int[] harr;
    int CAPACITY;
    int size;
    MinHeap2(int[] arr){
        this.harr =  arr;
        this.CAPACITY = arr.length;
        this.size =  arr.length;
        int i =  (size-1)/2;
        while (i>=0)
            heapify(i--);
    }

    @Override
    public void heapify(int i){
        int smallest = i;
        int left = 2*i+1, right = 2*i+2;
        if(harr[smallest] < harr[left])
            smallest =  left;
        if (harr[smallest] < harr[right])
            smallest =  right;
        if(i !=  smallest){
            DSAUtils.swap(harr, smallest, i);
            heapify(smallest);
        }
    }

    @Override
    public int getSize(){
        return size;
    }

    @Override
    public int parent(int i){
        return (i-1)/2;
    }

    @Override
    public int left(int i){
        return 2*i+1;
    }

    @Override
    public int right(int i){
        return 2*i+2;
    }

    @Override
    public void insert(int k){
        if (size == CAPACITY)
            throw new StackOverflowError("Out of memory");
        size++;
        int i = size-1;
        harr[i] = k; // insert at last
        while (i != 0 && harr[parent(i)] > harr[i]) { // heapify
            DSAUtils.swap(harr, i, parent(i));
            i = parent(i);
        }
    }

    @Override
    public int extract(){
        int val = harr[0];
        DSAUtils.swap(harr, 0, size-1); // swap first and last
        size--;
        heapify(0);
        return val;
    }

}

class MaxHeap{
    int[] harr;
    int size;
    int capacity;
    MaxHeap(int capacity){
        this.capacity  = capacity;
    }
    MaxHeap(int arr[]){
        this.harr =  arr;
        this.capacity = arr.length;
        this.size =  arr.length;
        int i = size/2-1;
        while (i>=0) // heapify non-leaf
            heapify(arr, i, size);
    }

    public int left(int i){
        return 2*i+1;
    }

    public int right(int i){
        return 2*i+2;
    }

    void heapify(int[] arr, int i, int n) {
        int largest = i;
        if (arr[left(i)] < arr[largest])
            largest = left(i);
        else if (arr[right(i)] < arr[largest])
            largest = right(i);
        if (largest != i) {
            DSAUtils.swap(arr, i, largest);
            heapify(arr, largest, i);
        }
    }
}

class HeapSort{
    public static final void heapSort(int[] arr){
        int n =arr.length;
        // build a max-code.heap O(n)
        MaxHeap heap =  new MaxHeap(arr);

        // extract max-element and put at arr end then heapify the subArray
        // and reduce size of heap by 1
        for (int i = n-1; i >=0 ; i--) {
            DSAUtils.swap(arr, 0, i );
            heap.heapify(arr, 0, i);
        }
    }
}