/*
 * heap.h: Heap class definition
 */

#ifndef HEAP_H
#define HEAP_H

#include <vector>
#include <iostream>
#include "huffmanTree.h"

using namespace std;

class heap {
    public:
        heap( int capacity );
        ~heap();
        bool isEmpty() const;
        huffmanTree* findMin() const;
        void insert( huffmanTree *x );
        huffmanTree* deleteMin( );
        void makeEmpty();
        int currentSize;

    private:
        vector<huffmanTree*> list;
        void percolateDown( int hole );
};
#endif
