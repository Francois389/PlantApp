package com.fsp.plantapp

interface Observer {
    fun update()
    fun addListener(listener: () -> Unit)
}