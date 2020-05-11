package com.team4.walletwatch

import org.w3c.dom.Document
import org.w3c.dom.Node
import java.time.Instant

/* This class represents an Entry object,
* which is an expense that has a dollar amount, possibly a description of purchase,
* a timestamp of purchase, and the category the expense entry falls under. */
class Entry(
    var amount: Double,
    var description: String,
    var timestamp: Instant,
    var category: String)

/* Purpose: Static method that sorts a list of Entry objects by date from newest to oldest.
*
* Parameters: entries represents a list of Entry objects.
*
* Returns: list represents the sorted list of Entry objects
* or will return null if entries is null. */
fun sortByDateDescending(entries : MutableList<Entry>?) : MutableList<Entry>? {
    val list = entries?.sortedWith(compareByDescending { it.timestamp })

    if (!list.isNullOrEmpty()) {
        return list as MutableList<Entry>?
    }

    return null
}

/* Purpose: Static method that sorts a list of Entry objects by date from oldest to newest.
*
* Parameters: entries represents a list of Entry objects.
*
* Returns: list represents the sorted list of Entry objects
* or will return null if entries is null. */
fun sortByDateAscending(entries : MutableList<Entry>?) : MutableList<Entry>? {
    val list = entries?.sortedWith(compareBy { it.timestamp })

    if (!list.isNullOrEmpty()) {
        return list as MutableList<Entry>?
    }

    return null
}

/* Purpose: Static method that sorts a list of Entry objects by price from highest to cheapest.
*
* Parameters: entries represents a list of Entry objects.
*
* Returns: list represents the sorted list of Entry objects
* or will return null if entries is null. */
fun sortByPriceDescending(entries : MutableList<Entry>?) : MutableList<Entry>? {
    val list = entries?.sortedWith(compareByDescending { it.amount })

    if (!list.isNullOrEmpty()) {
        return list as MutableList<Entry>?
    }

    return null
}

/* Purpose: Static method that sorts a list of Entry objects by price from cheapest to highest.
*
* Parameters: entries represents a list of Entry objects.
*
* Returns: list represents the sorted list of Entry objects
* or will return null if entries is null. */
fun sortByPriceAscending(entries : MutableList<Entry>?) : MutableList<Entry>? {
    val list = entries?.sortedWith(compareBy { it.amount })

    if (!list.isNullOrEmpty()) {
        return list as MutableList<Entry>?
    }

    return null
}

/* Purpose: Static method that retrieves a list of all entries
* from the local repo XML file as Entry objects.
*
* Parameters: doc represents the Document of the local repo XML file.
*
* Returns: entries represent the list of Entry objects. */
fun getEntries(doc : Document) : MutableList<Entry>? {
    val entryNodes = doc.getElementsByTagName("entry")
    val entries = mutableListOf<Entry>()

    var node: Node
    var amount: Double
    var description: String
    var timestamp : Instant
    var category: String

    /* Iterate through the Entry elements in the XML file  */
    for (index in 0 until entryNodes.length) {
        node = entryNodes.item(index)
        val id = node.attributes.getNamedItem("id").textContent.toString()

        /* Grab the values from the children nodes of the current Entry element. */
        amount = DataManager.getValueByID(doc, "$id-a")!!.toDouble()
        description = DataManager.getValueByID(doc, "$id-d")!!
        /* Parse the timestamp string as a timestamp object known as Instant. */
        timestamp = Instant.parse(DataManager.getValueByID(doc, "$id-s")!!)
        /* Retrieve the category label by using the id "c-x-l". */
        category = DataManager.getValueByID(doc, id.substring(0, 4) + "l")!!

        /* Create an instance of the Entry class and add it to the list of entries. */
        entries.add(Entry(amount, description, timestamp, category))
    }

    /* Return the list of entries, which can safely be empty. */
    return sortByDateDescending(entries)
}
