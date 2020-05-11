package com.team4.walletwatch

import org.w3c.dom.Document
import org.w3c.dom.Element
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.xml.xpath.XPathFactory
import kotlin.collections.ArrayList

object DataManager {
    /* Purpose: Retrieve a value from an element in the local repo using an id.
    *
    * Parameters: doc represents the Document of the local repo XML file.
    * id represents the numerical id of the target element.
    *
    * Returns: A string representing the value of the target element or
    * null if the target element id was not found. */
    fun getValueByID(doc: Document, id: String) : String? {
        val element = doc.getElementById(id)

        if (element != null) {
            return element.textContent
        }
        return null
    }

    /* Purpose: Retrieve a value from an element in the local repo using an XPath expression.
    *
    * Parameters: doc represents the Document of the local repo XML file.
    * xpath represents the XPath expression query.
    *
    * Returns: A string representing the value of the target element or
    * null if the target element id was not found. */
    fun getValueByXPath(doc: Document, xpath: String) : String {
        return XPathFactory.newInstance().newXPath().evaluate(xpath, doc)
    }

    /* Purpose: Retrieves the labels of each of the three categories.
    *
    * Parameters: doc represents the Document of the local repo XML file.
    *
    * Returns: categories represents a list of the category labels. */
    fun getCategories(doc: Document) : MutableList<String?> {
        val categories : MutableList<String?> = arrayListOf("All")
        categories.add(getValueByID(doc, "c-1-l"))
        categories.add(getValueByID(doc, "c-2-l"))
        categories.add(getValueByID(doc, "c-3-l"))

        return categories
    }

    /* Purpose: Converts a Date object into a LocalDate object.
    *
    * Parameters: dateToConvert represents the Date object.
    *
    * Returns: A LocalDate object. */
    private fun convertToLocalDate(dateToConvert: Date): LocalDate? {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /* Purpose: Retrieves the total amount of expenses from the last seven days.
    *
    * Parameters: doc represents the Document of the local repo XML file.
    * category represents the specific category to filter for.
    *
    * Returns: total represents the total amount of expenses from the last seven days. */
    fun last7DaysTotal(doc: Document, category : String) : Double {
        val cal: Calendar = Calendar.getInstance()

        var amount : String?
        var total = 0.00

        /* Iterate through the last seven calendar days. */
        for (i in 1..7) {
            /* Retrieve the total amount of expenses for each day, if the Day element exists. */
            amount = getValueByID(doc, category + "-" +
                    convertToLocalDate(cal.time).toString() + "-t")
            if (amount != null) {
                total += amount.toDouble()
            }
            /* Determine the day previous to the current day. */
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }

        return total
    }

    /* Purpose: Grabs last 7 days for one or all categories
    *
    * Parameters: doc represents the Document of the local repo XML file.
    * categoryID represents the specific category to filter for.
    *
    * Returns: array holding totals for category/all categories for current day/prior 6 days */
    fun last7Days(doc: Document, categoryID : String) : DoubleArray {
        val cal: Calendar = Calendar.getInstance()

        var amount : String?
        val daysOfWeek = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        if(categoryID == "all") {
            for(i in 0..6) {
                var daySum = 0.0
                for (j in 1..3) {
                    /* Retrieve the total amount of expenses for each day, if the Day element exists. */
                    amount = getValueByID(doc, "c-" + j.toString() + "-" +
                            convertToLocalDate(cal.time).toString() + "-t")
                    if (amount != null) {
                        daySum += amount.toDouble()
                    }
                }
                daysOfWeek[i] = daySum
                /* Determine the day previous to the current day. */
                cal.add(Calendar.DAY_OF_MONTH, -1) // Can change Calender._ to years, ect.
            }
            return daysOfWeek
        }
        else {
            /* Iterate through the last seven calendar days. */
            for (i in 0..6) {
                /* Retrieve the total amount of expenses for each day, if the Day element exists. */
                amount = getValueByID(doc, categoryID + "-" +
                        convertToLocalDate(cal.time).toString() + "-t")
                if (amount != null) {
                    daysOfWeek[i] = amount.toDouble()
                }
                /* Determine the day previous to the current day. */
                cal.add(Calendar.DAY_OF_MONTH, -1)
            }
        }
        return daysOfWeek
    }

    /* Purpose: Retrieves the total amount of expenses from the last twelve months.
    *
    * Parameters: doc represents the Document of the local repo XML file.
    * category represents the specific category to filter for.
    *
    * Returns: total represents the total amount of expenses from the last twelve months. */
    fun last12MonthsTotal(doc: Document, category: String) : Double {
        val cal: Calendar = Calendar.getInstance()

        var amount : String?
        var total = 0.00

        /* Iterate through the last twelve calendar months. */
        for (i in 0..11) {
            /* Retrieve the total amount of expenses for each month, if the Month element exists. */
            amount = getValueByID(doc, category + "-" +
                    convertToLocalDate(cal.time).toString().substring(0, 7) + "-t")
            if (amount != null) {
                total += amount.toDouble()
            }
            /* Determine the month previous to the current month. */
            cal.add(Calendar.MONTH, -1)
        }

        return total
    }

    fun last12Months(doc: Document, categoryID : String) : DoubleArray {
        val cal: Calendar = Calendar.getInstance()

        var amount : String?
        val monthsOfYear = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        if(categoryID == "all") {
            for(i in 0..11) {
                var monthSum = 0.0
                for (j in 1..3) {
                    /* Retrieve the total amount of expenses for each Month, if the Month element exists. */
                    amount = getValueByID(doc, "c-" + j.toString() + "-" +
                            convertToLocalDate(cal.time).toString().substringBeforeLast("-") + "-t") // can get substring of just the year (last 4 characters) (change to substring Before first)
                    if (amount != null) {
                        monthSum += amount.toDouble()
                    }
                }
                monthsOfYear[i] = monthSum
                /* Determine the month previous to the current month. */
                cal.add(Calendar.MONTH, -1) // Can change Calender._ to years, ect.
            }
            return monthsOfYear
        }
        else {
            /* Iterate through the last 12 calendar months. */
            for (i in 0..11) {
                /* Retrieve the total amount of expenses for each month, if the month element exists. */
                amount = getValueByID(doc, categoryID + "-" +
                        convertToLocalDate(cal.time).toString().substringBeforeLast("-") + "-t")
                if (amount != null) {
                    monthsOfYear[i] = amount.toDouble()
                }
                /* Determine the month previous to the current month. */
                cal.add(Calendar.MONTH, -1)
            }
        }
        return monthsOfYear
    }

    fun last10Years(doc: Document, categoryID : String) : DoubleArray {
        val cal: Calendar = Calendar.getInstance()

        var amount : String?
        val yearsOfDecade = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        if(categoryID == "all") {
            for(i in 0..9) {
                var yearSum = 0.0
                for (j in 1..3) {
                    /* Retrieve the total amount of expenses for each year, if the year element exists for the past decade. */
                    amount = getValueByID(doc, "c-" + j.toString() + "-" +
                            convertToLocalDate(cal.time).toString().substringBefore("-") + "-t") // can get substring of just the year (last 4 characters)
                    if (amount != null) {
                        yearSum += amount.toDouble()
                    }
                }
                yearsOfDecade[i] = yearSum
                /* Determine the year previous to the current year. */
                cal.add(Calendar.YEAR, -1) // Can change Calender._ to years, ect.
            }
            return yearsOfDecade
        }
        else {
            /* Iterate through the last 10 calendar years. */
            for (i in 0..9) {
                /* Retrieve the total amount of expenses for each year, if the year element exists. */
                amount = getValueByID(doc, categoryID + "-" +
                        convertToLocalDate(cal.time).toString().substringBefore("-") + "-t")
                if (amount != null) {
                    yearsOfDecade[i] = amount.toDouble()
                }
                /* Determine the year previous to the current year. */
                cal.add(Calendar.YEAR, -1)
            }
        }
        return yearsOfDecade
    }

    /* Purpose: Determine how much of the date XML tree hierarchy already exists.
    *
    * Parameters: doc represents the Document of the local repo XML file.
    * category represents the number of the target category.
    * year represents the numerical year of the target date.
    * month represents the numerical month of the target date.
    * day represents the numerical day of the target date.
    *
    * Returns: dateExists represents how much of the date XML tree hierarchy already exists.
    * A value of 0 means the Year, Month, Day, and Entry elements
    * need to be added as children to the existing Category element.
    * A value of 1 means the Month, Day, and Entry elements
    * * need to be added as children to the existing Year element.
    * A value of 2 means the Day and Entry elements
    * need to be added as children to the existing Month element.
    * A value of 3 or more means the Entry element
    * needs to be added as a child to the existing Day element.
    * The id of the new entry will be this value minus 1.
    * For example, 3 means an id of 2 and 4 means an id of 3. */
    private fun findExistingDateTags(doc : Document, category: String,
                                     year : String, month : String, day : String) : Int {
        var id = "c-$category-$year"
        var xpath = "string(/root/data/category[@id=\"c-$category\"]/year[@id=\"$id\"]/month[@id=\""
        var dateExists = 0

        if (getValueByID(doc, id) != null) {
            dateExists++
            id += "-$month"
            xpath += "$id\"]/day[@id=\""
            if (getValueByID(doc, id) != null) {
                dateExists++
                id += "-$day"
                xpath += "$id\"]/entry[last()]/@id)"
                if (getValueByID(doc, id) != null) {
                    /* XPath to retrieve the id of the last Entry element within the Day element.
                    * New entries are appended, so the most recent entry is last. */
                    dateExists += getValueByXPath(
                        doc, xpath).substringAfterLast('-').toInt()
                }
            }
        }

        return dateExists
    }

    /* Purpose: Increment the amount of a total element in the local repo.
    *
    * Parameters: element represents the total Element to increment.
    * amount represents the amount to increment element by.
    *
    * Returns: Nothing. */
    private fun incrementTotal(element: Element, amount: String) {
        element.textContent = (element.textContent.toDouble() + amount.toDouble()).toString()
    }

    /* Purpose: Increment the amount of a total element in the local repo.
    *
    * Parameters: element represents the total Element to increment.
    * amount represents the amount to increment element by.
    *
    * Returns: Nothing. */
    fun addEntry(doc : Document, amountRaw : String, description : String,
                 date: String, category: String) {
        /* Extract year, month, and day from date string. */
        val year = date.substring(0, 4)
        val month = date.substring(5, 7)
        val day = date.substring(8)
        /* Determine how much of the date XML tree hierarchy already exists. */
        val dateExists = findExistingDateTags(doc, category, year, month, day)
        /* Remove any thousand separator commas. */
        val amount = amountRaw.substring(2).replace(",", "")

        /* Retrieve each of the Total elements for the target date. */
        var id = "c-$category"
        val categoryTotal = doc.getElementById("$id-t")
        id += "-$year"
        var yearTotal = doc.getElementById("$id-t")
        id += "-$month"
        var monthTotal = doc.getElementById("$id-t")
        id += "-$day"
        var dayTotal = doc.getElementById("$id-t")

        /* Retrieve the Category element. */
        val categoryTag = doc.getElementById("c-$category")

        id = "c-$category-$year"
        /* Retrieve the Year element, if it already exists. */
        var yearTag = doc.getElementById(id)

        /* If no part of the date was already in existence,
        * then create the Year element as a child of the Category element. */
        if (dateExists == 0) {
            yearTag = doc.createElement("year")
            yearTag.setAttribute("id", id)

            /* Create the Total element as a child of the Year element. */
            yearTotal = doc.createElement("total")
            yearTotal.setAttribute("id","$id-t")
            yearTotal.textContent = "0"
            yearTag.appendChild(yearTotal)

            categoryTag.appendChild(yearTag)
        }

        id += "-$month"
        /* Retrieve the Month element, if it already exists. */
        var monthTag = doc.getElementById(id)

        /* If only the Year element was already in existence,
        * then create the Month element as a child of the Year element. */
        if (dateExists <= 1) {
            monthTag = doc.createElement("month")
            monthTag.setAttribute("id", id)

            /* Create the Total element as a child of the Month element. */
            monthTotal = doc.createElement("total")
            monthTotal.setAttribute("id","$id-t")
            monthTotal.textContent = "0"
            monthTag.appendChild(monthTotal)

            yearTag.appendChild(monthTag)
        }

        id += "-$day"
        /* Retrieve the Day element, if it already exists. */
        var dayTag = doc.getElementById(id)
        /* Initialize Entry element id to one. */
        var entry = 1

        /* If only the Month element was already in existence,
        * then create the Day element as a child of the Category element. */
        if (dateExists <= 2) {
            dayTag = doc.createElement("day")
            dayTag.setAttribute("id", id)

            /* Create the Total element as a child of the Day element. */
            dayTotal = doc.createElement("total")
            dayTotal.setAttribute("id","$id-t")
            dayTotal.textContent = "0"
            dayTag.appendChild(dayTotal)

            monthTag.appendChild(dayTag)
        }
        /* If the entire date was already in existence,
        * then set the Entry element id to the next sequential number. */
        else {
            entry = dateExists - 1
        }

        id += "-$entry"
        /* Create the Entry element. */
        val entryTag = doc.createElement("entry")
        entryTag.setAttribute("id", id)

        /* Create the Amount element as a child of the Entry element. */
        val amountTag = doc.createElement("amount")
        amountTag.setAttribute("id","$id-a")
        amountTag.textContent = amount
        entryTag.appendChild(amountTag)

        /* Increment all the associated Total elements. */
        incrementTotal(doc.getElementById("t"), amount)
        incrementTotal(categoryTotal, amount)
        incrementTotal(yearTotal, amount)
        incrementTotal(monthTotal, amount)
        incrementTotal(dayTotal, amount)

        /* Create the Description element. */
        val descriptionTag = doc.createElement("description")
        descriptionTag.setAttribute("id","$id-d")
        descriptionTag.textContent = description
        entryTag.appendChild(descriptionTag)

        /* Grab the current timestamp from the device clock in UTC,
        * but replace the date portion with the user's date input. */
        val timestampTag = doc.createElement("timestamp")
        timestampTag.setAttribute("id", "$id-s")
        timestampTag.textContent =
            Instant.now().toString().replaceRange(0, 10, date)
        entryTag.appendChild(timestampTag)

        /* Append the new Entry element right after the last Entry element of the Day element.
        * If there are no Entry elements in this Day yet, it will simply add it right after the
        * Total element for that Day. */
        dayTag.appendChild(entryTag)
    }

    /* Purpose: Completely erase an obsolete category data and replace with new category label.
    *
    * Parameters: doc represents the Document of the local repo XML file.
    * labels represent an array of which categories to overwrite
    *
    * Returns: Nothing. */
    fun overwriteCategories(doc: Document, labels : ArrayList<String?>) {

    }
}