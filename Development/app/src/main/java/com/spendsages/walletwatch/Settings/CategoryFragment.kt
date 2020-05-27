package com.spendsages.walletwatch

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {
    private lateinit var rootView : View
    private lateinit var settings : SettingsActivity
    private lateinit var model : SharedViewModel

    private var categories = arrayOfNulls<String?>(3)
    private var categoryInputs = arrayOfNulls<String?>(3)

    /* Array that holds the new label for each category that the user changes.
    * If there is no change to a category, that index MUST be null. */
    private var changed = arrayOfNulls<String?>(3)

    private lateinit var saveButton : Button

    private val categoryTextboxes = Array<TextInputEditText?>(3) { null }

    private lateinit var success : Toast

    /* Purpose: Controller method that disables and greys-out Save Changes button or
    * enables and reveals the Save Changes button.
    *
    * Parameters: enable represents a Boolean of whether or not
    *             to enable the Save Changes button.
    *
    * Returns: Nothing. */
    private fun toggleSaveButton(enable : Boolean) {
        /* Only enable if not already enabled. */
        if (enable && !saveButton.isEnabled) {
            saveButton.isEnabled = true
            saveButton.isClickable = true
            /* Set opacity to 100 % */
            saveButton.alpha = 1.0F
        }
        /* Only disable if not already disabled. */
        else if (!enable && saveButton.isEnabled) {
            saveButton.isEnabled = false
            saveButton.isClickable = false
            /* Set opacity to 50 % */
            saveButton.alpha = 0.5F
        }
    }

    /* Purpose: Controller method that checks if the user entered in
    * at least one truly new category label, but with no duplicated labels.
    *
    * Parameters: None.
    *
    * Returns: Nothing. */
    private fun checkInputs() {
        /* Reset changed array. */
        changed[0] = ""
        changed[1] = ""
        changed[2] = ""

        /* Grab user input values. */
        for ((index, categoryTextbox) in categoryTextboxes.withIndex()) {
            categoryInputs[index] = categoryTextbox!!.text.toString().split(
                " ").joinToString(" ") { it.capitalize() }
        }

        /* Check for duplicate inputs or if there are no new categories. */
        val duplicatesFound = (categoryInputs.groupingBy { it }.eachCount()
            .filter { it.value > 1 }).isNotEmpty()
        val noNewLabels = categoryInputs.sortedBy { it } == categories.sortedBy { it }

        if (duplicatesFound || noNewLabels) {
            toggleSaveButton(false)
            return
        }

        /* Make temporary, mutable copy of user inputs. */
        val tempCategoryInputs = categoryInputs.clone()

        /* Set any existing categories to null in their same position as to ignore reordering. */
        for ((index, category) in categories.withIndex()) {
            if (category in categoryInputs) {
                changed[index] = null
                tempCategoryInputs[categoryInputs.indexOf(category)] = null
            }
        }

        /* Place the new category label(s) in any open slots,
        * preferring the textbox index if possible */
        for ((index, category) in tempCategoryInputs.withIndex()) {
            if (category != null) {
                if (changed[index] == "") {
                    /* Forcibly capitalize each word in new category label. */
                    changed[index] = category.split(
                        " ").joinToString(" ") { it.capitalize() }
                }
                else {
                    changed[changed.indexOf("")] = category
                }
            }
        }

        /* Enable Save Changes button. */
        toggleSaveButton(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_category, container, false)
        settings = activity as SettingsActivity
        model = settings.model

        /* Grab the labels of the categories as they currently are in the XML file. */
        categories = DataManager.getCategories(model.get()).slice(1..3).toTypedArray()

        saveButton = rootView.findViewById(R.id.saveButton)
        /* Disable and grey-out the Save Changes button,
        * since the user has not made any changes yet. */
        toggleSaveButton(false)

        /* This listener for the Save Change button saves the changed category,
        * displays the Toast message, and disables the Save Changes button. */
        saveButton.setOnClickListener {
            /* A category was changed without any restoration. */
            if (DataManager.changeCategories(settings, model.get(), changed)) {
                success = Toast.makeText(context, R.string.changedCategoryString, Toast.LENGTH_LONG)
                success.setGravity(Gravity.TOP + Gravity.CENTER_HORIZONTAL,
                    0, 0)
            }
            /* A category was restored from the Archive.xml. */
            else {
                success = Toast.makeText(context, R.string.restoredCategoryString, Toast.LENGTH_LONG)
                success.setGravity(Gravity.TOP + Gravity.CENTER_HORIZONTAL,
                    0, 0)
            }

            model.save(settings)
            success.show()
            toggleSaveButton(false)
        }

        /* Populate the array of category textboxes. */
        categoryTextboxes[0] = rootView.findViewById(R.id.category1Edit)
        categoryTextboxes[1] = rootView.findViewById(R.id.category2Edit)
        categoryTextboxes[2] = rootView.findViewById(R.id.category3Edit)

        /* Iterate through each category textbox. */
        for ((index, textbox) in categoryTextboxes.withIndex()) {
            /* Set the label for each category textbox as they currently are in the XML file. */
            textbox!!.setText(categories[index])
            categoryInputs[index] = categories[index]

            /* Listener that checks if the user changed the category textbox content. */
            textbox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    /* Remove all whitespace from user input in category textbox. */
                    val trimmed = s.toString().trim { it <= ' ' }

                    /* Check if the user did not enter any word(s) into the category textbox. */
                    if (trimmed.isEmpty()) {
                        /* Disable and grey-out the Save Changes button. */
                        toggleSaveButton(false)
                    }
                    else {
                        /* Check if at least one new category has been entered.
                        * If so, the Save Changes button will be enabled.*/
                        checkInputs()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }

        rootView.setOnTouchListener { _: View, _: MotionEvent ->
            /* Hide the keyboard. */
            (settings.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(categoryTextboxes[0]!!.windowToken, 0)
        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
