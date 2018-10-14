/* Starter code for LP2 */

// Change this to netid of any member of team
package EHP170000;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

// Skeleton for skip list implementation.

public class SkipList<T extends Comparable<? super T>> {
    static final int PossibleLevels = 33;
    Entry<T> head,tail;//dummy nodes
    int size, maxLevel;
    Entry[] last; //used by find
    Random random;




    static class Entry<E> {
        E element;
        Entry[] next;
        Entry prev;
        int[] span; //for indexing

        public Entry(E x, int lev) {
            element = x;
            next = new Entry[lev];
            span=new int[lev];
        }

        public E getElement() {
            return element;
        }
    }

    // Constructor
    public SkipList() {
        head=new Entry(null,33);
        tail=new Entry(null,33);
        size=0;
        maxLevel=1;
        last=new Entry[33];
        random=new Random();
        //initialize next and span arrays of head
        for(int i=0;i<33;i++)
        {
            head.span[i]=0;
            head.next[i]=tail;
            last[i]=head;
        }

    }

    // Add x to list. If x already exists, reject it. Returns true if new node is added to list
    public boolean add(T x) {
        int lev=chooseLevel();
        Entry<T> ent=new Entry<T>(x,lev);
        int[] steps_span=new int[maxLevel];
        //if element already exists don't add it
        if(containsWithSpan(x,steps_span))
            return false;

        int steps=0;

        //update pointers and spans of elements in last array as well as current element
        for(int i=0;i<ent.next.length;i++)
        {
            ent.next[i]=last[i].next[i];
            last[i].next[i]=ent;
            ent.span[i]=last[i].span[i]-steps;
            last[0].span[0]=steps+1;
            steps+=steps_span[i];
        }

        //update spans for the rest of the next pointers
        for(int i=ent.next.length;i<maxLevel;i++)
        {
            last[i].span[i]=last[i].span[i]+1;
        }

        ent.next[0].prev=ent;
        ent.prev=last[0];
        size+=1;
        return true;
    }

    // Find smallest element that is greater or equal to x
    public T ceiling(T x) {
        //in case of empty list
        if(isEmpty())
        {
           throw new NoSuchElementException();
        }
        if(!contains(x))//for the ceiling of the last element not present in the list e.g.(1,2,3,4) :ceiling(6)->exception
        {
            if (last[0].next[0] == tail)
            {
                throw  new NoSuchElementException();
            }

        }
        return (T) last[0].next[0].element;
    }

    //Does list contains x?
    public boolean contains(T x)
    {
        return containsWithSpan(x,new int[maxLevel]);
    }

    // Does list contain x?
    public boolean containsWithSpan(T x,int[] steps_span) {
        //return false in case of empty list
        if (isEmpty())
        {
            return false;
        }
        //to initialize last array call find
        findWithSpan(x,steps_span);
        if(last[0].next[0].element!=null && ((T)last[0].next[0].element).compareTo(x)==0)
        {
            return true;
        }
        return false;
    }

    //it will give the prev node of the node we want to find and it will fill up an array with the steps taken to reach current node
    public void findWithSpan(T x,int[] steps_span)
    {

            Entry<T> p=head;
            for (int i=maxLevel-1;i>=0;i--)
            {
                while(p.next[i].element!=null && ((T)p.next[i].element).compareTo(x)<0)//if next is lesser then x
                {
                    steps_span[i]+=p.span[i];
                    p=p.next[i];

                }
                //if next is greater or equal x
                last[i]=p;
            }
            //so when it comes down by one level-we will be having the number exactly same or greater hen the number we want to find
            //so put it in last array
        }



    //it will give the prev node of the node we want to find
    public void find(T x)
    {
       findWithSpan(x,new int[maxLevel]);
    }

    //choose the level of the new node randomly-fast method
    public int chooseLevel()
    {
        int lev=1+Integer.numberOfTrailingZeros(random.nextInt());
        lev=Math.min(lev,maxLevel+1);//increase the maxLevel gradually
        if(lev>maxLevel)
        {
            for(int i=maxLevel-1;i<lev;i++)
            {
                head.span[i]=size;
            }
            maxLevel=lev;
        }

        return lev;
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


    // Return first element of list
    public T first() {
        if(isEmpty())//in case of empty list
        {
            throw new NoSuchElementException();
        }
        else
        {
            return (T)head.next[0].element;
        }
    }

    // Find largest element that is less than or equal to x
    public T floor(T x) {
        //in case of empty list
        if(isEmpty())
        {
            throw new NoSuchElementException();
        }
        //for the floor of the first element not present in the list e.g.(4,5,6,7) :floor(3)->exception
        if (!contains(x)) {
            if (last[0] == head) {
                throw new NoSuchElementException();

            }
        }
        return (T)last[0].element;
    }

    // Return element at index n of list.  First element is at index 0.
    public T get(int n) {
        //in case of empty list
        if(isEmpty())
        {
            throw new NoSuchElementException();
        }
        return getLinear(n);
    }

    // O(n) algorithm for get(n)
    public T getLinear(int n) {
        if (n<0 || n>size-1)
        {
           throw new NoSuchElementException();
        }
        Entry<T> p=head;
        for (int i =0;i<n;i++)
        {
            p=p.next[0];
        }
        return (T)p.next[0].element;
    }

    // Optional operation: Eligible for EC.
    // O(log n) expected time for get(n). Requires maintenance of spans, as discussed in class.
    public T getLog(int n) {
        if (n<0 || n>size-1)
        {
            throw new NoSuchElementException();
        }
        Entry<T> p=head;
        int count=n;
        for (int i=maxLevel-1;i>=0;i--)
        {
            if(count>=p.span[i])
            {
                count-=p.span[i];
                p=p.next[i];
            }
            if (count==0)
                break;

        }
        return p.element;

    }

    // Is the list empty?
    public boolean isEmpty() {
        if(head.next[0]==tail)
            return true;
        return false;
    }

    // Iterate through the elements of list in sorted order

    public Iterator<T> iterator() {
        return (new SkipListIterator(this));
    }

    private class SkipListIterator implements Iterator<T> {
        private  SkipList<T> list = null;
        Entry<T> next = null;
        Entry<T> cursor, prev;
        boolean ready;

        SkipListIterator(SkipList<T> list) {
            this.list = list;
            this.cursor = list.head;
            ready = false;
        }

        public boolean hasNext() {
            if (cursor.next[0]!=this.list.tail && cursor.next[0]!=null)
                return true;
            return false;

        }

        public T next() {
            prev = cursor;
            cursor =cursor.next[0];
            ready = true;
            return (T) cursor.element;
        }

        /*public void add(T ent) {
            this.list.add((T) ent);
        }*/
        public void remove() {
            if(!ready) {
                throw new NoSuchElementException();
            }
            prev.next[0] = cursor.next[0];
            // Handle case when tail of a list is deleted
            if(cursor == list.tail) {
                list.tail = prev;
            }
            cursor = prev;
            ready = false;  // Calling remove again without calling next will result in exception thrown
            list.size--;
        }
    }

    // Return last element of list
    public T last() {
        if(head.next[0]==tail)//in case of empty list
        {
            throw new NoSuchElementException();
        }
        return getLinear(size-1);
    }



    // Optional operation: Reorganize the elements of the list into a perfect skip list
    // Not a standard operation in skip lists. Eligible for EC.
    public void rebuild() {

    }

    // Remove x from list.  Removed element is returned. Return null if x not in list
    public T remove(T x) {
        if (!contains(x))
        {
            return null;
        }
        Entry<T> ent=last[0].next[0];//element to be deleted

        //till the height of the next
        //put span=(span of last + span of the element to be deleted -1)
        int i;
        for (i=0;i<=ent.next.length-1;i++)
        {
            last[i].span[i]=(last[i].span[i]+ent.next[i].span[i]-1);//update span
            last[i].next[i]=ent.next[i];//bypass ent at level i
        }

        //for the elements heigher then height of next of ent just set their span to - 1
        for (i=ent.next.length;i<maxLevel;i++)
        {
           last[i].span[i]=last[i].span[i]-1;//update span
        }

        size=size-1;
        return ent.element;

    }

    // Return the number of elements in the list
    public int size() {
        return size;
    }
}



