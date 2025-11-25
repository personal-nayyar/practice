package DSimpl;

import java.util.Arrays;

interface ArrayListCustom<T> {
    void add(T element);
    void remove(T element);
    T get(int index);
    int size();
}

class ArrayListCustomImpl<T> implements ArrayListCustom<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private T[] array;
    private int size;

    public ArrayListCustomImpl() {
        array = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    @Override
    public void add(T element) {
        ensureCapacity();
        array[size++] = element; // main addition but how validate if size available
    }

    // Ensure array has enough capacity, resize if full
    private void ensureCapacity() {
        if (size == array.length) {
            int newLength = array.length * 2; // double the capacity
            array = Arrays.copyOf(array, newLength);  // Arrays.copyOf(original, newLength)
        }
    }

    @Override
    public void remove(T element) {
        for (int i = 0; i < size; i++) {
            if (array[i].equals(element)) {
                System.arraycopy(array, i + 1, array, i, size - i - 1);
                // arraycopy(Object src,  int  srcPos, Object dest, int destPos, int length);
                size--;
                return;
            }
        }
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    @Override
    public int size() {
        return size;
    }
}
