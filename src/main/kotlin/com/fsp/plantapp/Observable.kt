package com.fsp.plantapp

interface Observable {
    fun update()
    fun addListener(listener: () -> Unit)
}