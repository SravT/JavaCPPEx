/*
 * huffmanNode.h
 */

#ifndef HUFFMANNODE_H
#define HUFFMANNODE_H

using namespace std;

class huffmanNode {
    public:
        char elem;
        huffmanNode *left;
        huffmanNode *right;

        huffmanNode( char c, huffmanNode *l, huffmanNode *r) : elem( c ), left( l ), right( r ) { } 
};
#endif
