package day2

import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val head: AtomicReference<Node<E>>
    private val tail: AtomicReference<Node<E>>

    init {
        val dummy = Node<E>(null)
        head = AtomicReference(dummy)
        tail = AtomicReference(dummy)
    }

    override fun enqueue(element: E) {
        val newTail = Node(element)
        while (true){
            val curTail = tail.get()
            val nextTail = curTail.next.get()
            if(curTail == tail.get()){
                if(nextTail == null){
                    if(curTail.next.compareAndSet(null, newTail)){
                        tail.compareAndSet(curTail, newTail)
                        return
                    }
                }else{
                    tail.compareAndSet(curTail, nextTail)
                }
            }
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val curHead = head.get()
            val curTail = tail.get()
            val next = curHead.next.get()

            if (curHead == head.get()) {
                if (next == null) {
                    return null
                }
                if (curHead == curTail) {
                    tail.compareAndSet(curTail, next)
                } else {
                    val value = next.element
                    if (head.compareAndSet(curHead, next)) {
                        next.element = null
                        return value
                    }
                }
            }
        }
    }


    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
