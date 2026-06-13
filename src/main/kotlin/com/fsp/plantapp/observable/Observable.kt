package com.fsp.plantapp.observable

abstract class Observable<T> {
    val observers: MutableCollection<(T) -> Unit> = mutableSetOf()

    fun addObserver(observer: (T) -> Unit) = observers.add(observer)
    fun removeObserver(observer: (T) -> Unit) = observers.remove(observer)

    protected fun notifyObservers(data: T) = observers.forEach { it(data) }
}