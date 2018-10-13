/* Starter code for LP2 */

// Change this to netid of any member of team
package krt170130;

import sun.awt.X11.XConstants;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

// Skeleton for skip list implementation.

public class SkipList<T extends Comparable<? super T>> {
    static final int PossibleLevels = 33;
    Entry <T> head, tail;
    int size, maxLevel;
    Entry <T> last[];
    Random random;

    static class Entry<E> {
        E element;
        Entry[] next;
        Entry prev;
        int[] span; // for indexing

        public Entry(E x, int lev) {
            element = x;
            next = new Entry[lev];
            span = new int[lev]; // span array stores the width as in count of the nodes that it jumps at level i. This is used for searching at a position.
            // add more code if needed
        }

        public E getElement() {
            return element;
        }
    }

    // Constructor
    public SkipList() {

        this.head = new Entry<>(null, 33);
        this.tail = new Entry<>(null, 33);
        this.size = 0;
        this.maxLevel = 1;
        this.last = new Entry[33];
        this.random = new Random();

        // Initialize last of each level at head so that in case in add method when we call chooselevel after add it won't throw any exception if maxLevel increases by chooselevel.
        setup();
    }
    private void setup() {
        for (int i=0;i<33;i++) {
            this.last[i] = this.head;
            this.head.next[i] = this.tail;
        }
    }

    private void find(T x) {
        Entry curr = this.head; // Start from head and go down until lowest level filling up the last array such that the jump at last[i] doesn't cross x
        for (int i = this.maxLevel -1;i>=0;i--) {
            while(curr.next[i].element!= null && x.compareTo((T)curr.next[i].element) > 0) { // checking if the next element is tail or not
                curr = curr.next[i];
            }
            this.last[i] = curr;
        }
    }

    public void printSpanArray(int n) {
        int index = 0;
        Entry curr = this.head;
        while(index++ < n) {
            curr = curr.next[0];
        }
        System.out.println(curr.element);
        int arr[] = curr.span;
        for (int i=0;i<curr.span.length;i++) {
            System.out.print(curr.span[i] + " ");
        }
        System.out.println();
    }

    private int[] findWithSpan(T x, int level) {
        int[] spanLevel = new int[level];
        Entry curr = this.head;
        for (int i=level - 1;i>=0;i--) {
            while(curr.next[i].element != null && x.compareTo((T)curr.next[i].element) > 0) {
                spanLevel[i] += curr.span[i];
                curr = curr.next[i];
            }
        }
        return spanLevel;
    }

    // Add x to list. If x already exists, reject it. Returns true if new node is added to list
    public boolean add(T x) {
        if(contains(x)) return false;
        int level = chooseLevel();
//        System.out.println(x + " " + level);
        int[] levelSpan = findWithSpan(x, level);
        int currSpan = 0;
        Entry ent = new Entry(x, level);
        for (int i=0;i<level;i++) {
            ent.next[i] = this.last[i].next[i];
            this.last[i].next[i] = ent;
            ent.span[i] = this.last[i].span[i] - currSpan;
            this.last[i].span[i] = currSpan + 1;
            currSpan += levelSpan[i];
        }

        for(int i = level; i<this.maxLevel;i++) {
            this.last[i].span[i]++;
        }
        /*System.out.println(this.maxLevel);
        for (int i=0;i<this.maxLevel;i++) {
            System.out.print(this.head.span[i] +  " ");
        }
        System.out.println();*/
        ent.next[0].prev = ent;
        ent.prev = this.last[0];
        this.size++;
        return true;
    }

    private int chooseLevel() {
        int level =1;
        level = 1 + Integer.numberOfTrailingZeros(random.nextInt());
        level = Math.min(level, this.maxLevel + 1);
        if(level > this.maxLevel){
            for (int i=this.maxLevel;i<level;i++) {
                this.head.span[i] = this.size;
            }
            this.maxLevel = level;
        }
        return level;
    }

    // Find smallest element that is greater or equal to x
    public T ceiling(T x) {

        find(x);
        if(this.last[0].next[0] == this.tail) {
            throw new NoSuchElementException();
        }
        return (T) this.last[0].next[0].element;
    }

    // Does list contain x?
    public boolean contains(T x) {
        find(x);
        if(this.last[0].next[0].element == null) return false;
        return x.compareTo((T)this.last[0].next[0].element) == 0;
    }

    // Return first element of list
    public T first() {
        return null;
    }

    // Find largest element that is less than or equal to x
    public T floor(T x) {
        return null;
    }

    // Return element at index n of list.  First element is at index 0.
    public T get(int n) {
        return getLog(n);
//        return getLog(n);
    }

    // O(n) algorithm for get(n)
    public T getLinear(int n) {
        if (n < 0 || n >= this.size()) throw new NoSuchElementException();
        Entry curr = this.head;
        while(n>0) {
            curr = curr.next[0];
            n--;
        }
        return (T) curr.element;
    }

    // Optional operation: Eligible for EC.
    // O(log n) expected time for get(n). Requires maintenance of spans, as discussed in class.
    public T getLog(int n) {
        if(n < 0 || n>= this.size()) throw new NoSuchElementException();
        Entry curr = this.head;
        int level = this.maxLevel - 1;
        while(n>0) {
            if(curr.span[level] >= n) {
                n-= curr.span[level];
                level--;
            }
            else
                curr = curr.next[level];
        }
        return (T) curr.getElement();
    }

    // Is the list empty?
    public boolean isEmpty() {

        return size() == 0;
    }

    // Iterate through the elements of list in sorted order
    public Iterator<T> iterator() {
        return null;
    }

    // Return last element of list
    public T last() {
        if(this.tail.prev.element!=null) return (T)this.tail.prev.element;
        return null;
    }

    // Optional operation: Reorganize the elements of the list into a perfect skip list
    // Not a standard operation in skip lists. Eligible for EC.
    public void rebuild() {
        int maxHeight = (int) Math.floor((Math.log(this.size)) / (Math.log(2))) + 1;
        int[] heights = new int[this.size];
        setHeights(heights, 0, this.size - 1, maxHeight);
        Entry[] elements = new Entry[this.size];
        copy(elements);
        reset(heights, elements);
        for (int i=1;i<=maxHeight;i++) {
            build(i, heights, elements);
        }
    }

    private void reset(int[] heights, Entry[] elements) {
        setup();
        this.head.next[0] = elements[0];
        int n = heights.length;
        for (int i=0;i<n;i++) {
            elements[i].next = new Entry[heights[i]];
            elements[i].span = new int[heights[i]];
            if(i != heights.length - 1) {
                elements[i].next[0] = elements[i+1];
                elements[i].span[0] = 1;
            }
        }
        elements[n-1].next[0]= this.tail;
    }

    private void build(int level, int[] heights, Entry[] elements) {
        int n = elements.length;
        Entry prev = this.head;
        int prevIdx = 0;
        System.out.println(level);
        for (int i=0;i<n;i++) {
            if(heights[i] >= level) {
                prev.next[level-1] = elements[i];
                prev.span[level-1] = i - prevIdx;
                prev = elements[i];
                prevIdx = i;
            }
        }
        prev.span[level-1] = n - prevIdx - 1;
        prev.next[level-1] = this.tail;
    }

    private void copy(Entry[] elements) {
        Entry curr = this.head.next[0];
        for (int i=0;i<elements.length;i++) {
            elements[i] = curr;
            curr = curr.next[0];
        }
    }

    private void setHeights(int[] heights, int l, int r, int mh) {
        if(l > r) return;
        if(l == r) {
            heights[l] = mh;
        }
        int mid = (l+r) / 2;
        heights[mid] = mh;
        setHeights(heights, l, mid-1, mh-1);
        setHeights(heights, mid+1, r, mh-1);
    }

    // Remove x from list.  Removed element is returned. Return null if x not in list
    public T remove(T x) {
        if(!contains(x)) return null;
        Entry ent = this.last[0].next[0];
        for (int i=0;i<ent.next.length;i++) {
            this.last[i].next[i] = ent.next[i];
        }
        int i=0;
        for (;i<ent.next.length;i++) {
            this.last[i].span[i] += ent.span[i] - 1;
        }
        for(;i<maxLevel;i++) {
            this.last[i].span[i]--;
        }
        this.size--;
        return (T)ent.getElement();
    }

    // Return the number of elements in the list
    public int size() {
        return this.size;
    }


    // Printing the contents of the list
    public void printList() {
        Entry curr = this.head;
        while(curr.next[0].element != null) {
            System.out.print(curr.next[0].element + " - ");
            for (int i=0;i<curr.next[0].span.length;i++) {
                System.out.print(curr.next[0].span[i]+" ");
            }
            System.out.println();
            curr = curr.next[0];
        }
        System.out.println();
    }
}
