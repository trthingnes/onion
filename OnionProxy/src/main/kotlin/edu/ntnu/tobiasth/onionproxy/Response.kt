package edu.ntnu.tobiasth.onionproxy

interface Response {
    fun toByteList(): List<Int>
}