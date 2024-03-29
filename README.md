# Implementation-of-SkipLists

CS 5V81.001.  Implementation of data structures and algorithms
Fall 2018;  Wed, Sep 26.
Long Project LP2: Skip lists

Ver 1.0: Initial description (Wed, Sep 26).

Due: 11:59 PM, Sun, Oct 14 (1st deadline), Sun, Oct 21 (2nd deadline).

Max excellence credits: 1.0. 

Submit before the first deadline (Sun, Oct 14) to be eligible for excellence credit. 
For each group, only its last submission is kept and earlier submissions are discarded. 
Your code must be of good quality, and pass all test cases within time limits to earn excellence credits.

Project Description
Implement the following operations of skip lists. Starter code is provided. Do not change the signatures of methods declared to be public. You can add additional fields, nested classes, and methods as needed.
* add(x): Add a new element x to the list. If x already exists in the skip list,
  replace it and return false.  Otherwise, insert x into the skip list and return true.

* ceiling(x): Find smallest element that is greater or equal to x.

* contains(x): Does list contain x?

* first(): Return first element of list.

* floor(x): Find largest element that is less than or equal to x.

* get(n): Return element at index n of list.  First element is at index 0.
  Call either getLinear or getLog.

* getLinear(n): O(n) algorithm for get(n).

* getLog(n): O(log n) expected time algorithm for get(n).
  This method is optional, but code it correctly to earn EC.

* isEmpty(): Is the list empty?

* iterator(): Iterator for going through the elements of list in sorted order.

* last(): Return last element of list.

* rebuild(): Reorganize the elements of the list into a perfect skip list.

* remove(x): Remove x from the list. If successful, removed element is returned.  Otherwise, return null.

* size(): Return the number of elements in the list.
