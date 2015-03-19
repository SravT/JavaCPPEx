//barrierimp.cpp
#include <ctype.h> /* for isspace */
#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <math.h>

class Barrier {
    // How many threads need call wait before releasing all threads
    int _size, _count;
    sem_t mutex;    // the mutex
    sem_t throttle; // Waiters signal the releaser so that there is no over-pumping
    sem_t waitq;    // The semaphore on which the waiters wait
    
public:
    Barrier(int size);
    void wait();
};

Barrier::Barrier(int size) {
    _size=size;
    _count=size;
    sem_init(&mutex,0,1);   // init to 1
    sem_init(&throttle,0,0);    // init to 0
    sem_init(&waitq,0,0);   // init to 0
}

void Barrier::wait(){
    sem_wait(&mutex);   // Make sure only one in at a time
    _count--;
    if (_count==0 ) {
        // Time to wake everyone up
        for (int i=1;i<_size;i++) {
            sem_post(&waitq);   // Wake up another waiter
            sem_wait(&throttle);    // Wait for the waiter to awaken and signal me.
        }
        _count=_size;   // Reset the counter
        sem_post(&mutex);   // Release the mutex
    }
    else {
        // Block myself, but first release mutex
        sem_post(&mutex);   
        sem_wait(&waitq);   // Sleep
        sem_post(&throttle);    // wake up the releaser
    }
}

struct pblock {
    // parameter block for the threads
    int thrID;
    int threads;
    int rounds;
    Barrier *barrier;
}; 
#define MAX_INTS 4096
int inputs[MAX_INTS];

void *handler (void *arg) {
    int myID=0;
    pblock *a = (pblock*) arg;
    myID=a->thrID;
    int pow=1;
    int active_threads=a->threads;
    for (int i=0;i<a->rounds;i++) {
        // if myID < active threads
        if (myID<active_threads) {
            // Do the arithmetic to determine array indexes
            // and find and store the max of two values
            if (inputs[2*myID*pow]<inputs[2*myID*pow+pow])
                inputs[2*myID*pow]=inputs[2*myID*pow+pow];
            pow=pow*2;
        }
        active_threads=active_threads/2;
        // All of the threads wait
        a->barrier->wait();
    }
}
int main() {
    printf("\nHello Threaders!\n");
    int val;
    int count=0;
    int threads=0;
    int rounds=0;
    // Now read the input file. We are assuming it has pow 2 inputs
    while (fscanf(stdin,"%d",&val)!=EOF && (count<MAX_INTS)) {
        inputs[count++]=val;
    }
    threads=count/2;
    fprintf(stdout,"Threads is %d\n", threads);
    // Create a barrier
    Barrier *bp = new Barrier(threads);
    rounds = log2(threads) + 1;
    fprintf(stdout,"Rounds = %d\n", rounds);    
    pthread_t thrds[MAX_INTS/2];
    pblock params[MAX_INTS/2];
    for (int i=0;i<threads;i++) {
        // Set up the thread parameter blocks and start threads
        params[i].thrID=i;
        params[i].threads=threads;
        params[i].barrier=bp;
        params[i].rounds=rounds;
        pthread_create(&thrds[i],NULL, handler, (void*)&params[i]);
    }

    // Wait for the threads to complete
    for (int i=0;i<threads;i++) {
        pthread_join(thrds[i],NULL);
    }

    // Write out the largest which has been placed in position 0
    fprintf(stdout,"Max value is %d\n",inputs[0]);
}
