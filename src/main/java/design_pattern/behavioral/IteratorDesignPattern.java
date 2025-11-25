package design_pattern.behavioral;

/***
 Iterator Pattern lets you access elements of a collection (list, array, set, etc.) sequentially without exposing its internal structure.
 •	Without iterator → you’d expose the collection details (like index, array, loops).
 •	With iterator → you just say “give me the next item if available.”
 •	Iterator Pattern separates how you traverse a collection from the collection itself.
 •	Why useful? → It makes code cleaner, more flexible, and works even if the internal storage changes.
 */
// Iterator interface with methods hasNext() and next()
interface Iterator {
    // check if there is next element
    boolean hasNext();
    // return next element
    Object next();
}

// Container interface with method getIterator()
interface Collection {
    Iterator getIterator();
}

// Concrete class NameRepository implementing Container
class NameRepository implements Collection {
    public String[] names = {"Robert", "John", "Julie", "Lora"};

    @Override
    public Iterator getIterator() {
        return new NameIterator();
    }

    // Private inner class NameIterator implementing Iterator
    private class NameIterator implements Iterator {
        int index;

        @Override
        public boolean hasNext() {
            return index < names.length;
        }

        @Override
        public Object next() {
            if (this.hasNext()) {
                return names[index++];
            }
            return null;
        }
    }
}

public class IteratorDesignPattern {
    public static void main(String[] args) {
        NameRepository namesRepository = new NameRepository();

        for (Iterator iter = namesRepository.getIterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            System.out.println("Name : " + name);
        }
    }
}

