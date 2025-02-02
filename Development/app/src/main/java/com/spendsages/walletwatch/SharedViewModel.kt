package com.spendsages.walletwatch

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.w3c.dom.Document

/* This class provides support for sharing live data amongst different views. */
class SharedViewModel(context: Context) : ViewModel() {
    /* Instantiate an instance of the Data Repository object. */
    private val repository: DataRepository = DataRepository(context)
    /* Initialize all Tab Fragment booleans to true, which indicates to
    * each respective Tab Fragment that their category labels are outdated. */
    private var tabCategoriesNeedRefresh = Array(3){ true }

    /* Purpose: Retrieves the live DOM object, intended for active observers of the model.
    *
    * Parameters: None.
    *
    * Returns: MutableLiveData<Document> that represents the live DOM data in the XML data file. */
    fun getLive(): MutableLiveData<Document> {
        return repository.doc
    }

    /* Purpose: Retrieves the DOM object at a point in time,
    * intended for passive accessors of the model.
    *
    * Parameters: None.
    *
    * Returns: Document that represents the DOM data in the XML data file at a point in time. */
    fun get(): Document {
        return repository.doc.value!!
    }

    /* Purpose: Retrieves the DOM object as a string at a point in time,
    * intended for passive accessors of the model.
    *
    * Parameters: None.
    *
    * Returns: CharSequence that represents the a string conversion of the DOM data
    * in the XML data file at a point in time. */
    fun getString(doc: Document): CharSequence {
        return repository.docString(doc)
    }

    /* Purpose: Applies changes to the live DOM object and the XML data file.
    * This will trigger active observers of the model.
    *
    * Parameters: None.
    *
    * Returns: Nothing. */
    fun save() {
        repository.save()
    }

    /* Purpose: Rapidly retrieves the category labels from the XML data file.
    * This method does not create, access, or modify the live DOM object "repository.doc".
    *
    * Parameters: None.
    *
    * Returns: MutableList<String> that represents the category labels. */
    fun getCategoryLabels() : MutableList<String> {
        return repository.parseCategoryLabels()
    }

    /* Purpose: Reset all Tab Fragment booleans to true, which indicates to
    * each respective Tab Fragment that their category labels are outdated.
    *
    * Parameters: None.
    *
    * Returns: Nothing. */
    fun notifyTabCategoriesNeedRefresh() {
        tabCategoriesNeedRefresh.fill(true)
    }

    /* Purpose: Retrieve a specific Tab Fragment boolean.
    *
    * Parameters: index represents the zero-based index of the Tab Fragment.
    *
    * Returns: Boolean that is true when the Tab Fragment's category labels are outdated or
    * false when the Tab Fragment's category labels are up to date. */
    fun getTabCategoriesNeedRefresh(index : Int) : Boolean {
        return tabCategoriesNeedRefresh[index]
    }

    /* Purpose: Set a specific Tab Fragment's boolean to false, which indicates that
    * the respective Tab Fragment's category labels are now up to date.
    *
    * Parameters: index represents the zero-based index of the Tab Fragment.
    *
    * Returns: Nothing. */
    fun resetTabCategoriesNeedRefresh(index : Int) {
        tabCategoriesNeedRefresh[index] = false
    }
}