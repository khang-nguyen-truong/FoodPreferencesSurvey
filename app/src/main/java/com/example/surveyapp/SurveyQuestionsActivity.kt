package com.example.surveyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.example.surveyapp.databinding.ActivitySurveyQuestionsBinding
import com.example.surveyapp.databinding.ItemQuestionBinding
import java.lang.StringBuilder

class SurveyQuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurveyQuestionsBinding
    private lateinit var surveyQuestionsArray: Array<Pair<String, Array<String>>>
    private lateinit var selectedAnswers: MutableMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedAnswers = mutableMapOf()
        binding = ActivitySurveyQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getIntExtra("surveyTypeId", -1)) {
            0 -> {
                surveyQuestionsArray =
                    getPairOfQuestionsAndChoices(resources.getStringArray(R.array.food_pref_questions))
                binding.surveyTitle.text = resources.getStringArray(R.array.survey_types)[0]
            }

            1 -> {
                surveyQuestionsArray =
                    getPairOfQuestionsAndChoices(resources.getStringArray(R.array.diet_habits_questions))
                binding.surveyTitle.text = resources.getStringArray(R.array.survey_types)[1]
            }
        }

        binding.surveyListView.adapter = SurveyQuestionAdapter()

        binding.submitButton.setOnClickListener {
            if (surveyQuestionsArray.size != selectedAnswers.size) {
                Toast.makeText(
                    this,
                    getString(R.string.error_submit_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val stringBuilder = StringBuilder()
                selectedAnswers.forEach { (question, answer) ->
                    stringBuilder.append("$question : $answer\n")
                }
                binding.resultsText.text = stringBuilder.toString()
            }
        }
    }

    private fun getPairOfQuestionsAndChoices(questionsArray: Array<String>): Array<Pair<String, Array<String>>> {
        return questionsArray.map { question ->
            val parts = question.split(":")
            val questionText = parts[0]
            val answers = parts.slice(1 until parts.size).toTypedArray()
            questionText to answers
        }.toTypedArray()
    }

    inner class SurveyQuestionAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return surveyQuestionsArray.size
        }

        override fun getItem(position: Int): Any {
            return surveyQuestionsArray[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder: ViewHolder
            val view: View

            if (convertView == null) {
                val inflater = LayoutInflater.from(parent?.context)
                val itemBinding = ItemQuestionBinding.inflate(inflater, parent, false)
                view = itemBinding.root
                viewHolder = ViewHolder(itemBinding)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val (question, choices) = surveyQuestionsArray[position]
            viewHolder.binding.questionText.text = question

            viewHolder.binding.choicesRadioGroup.removeAllViews()
            for ((index, choice) in choices.withIndex()) {
                val radioButton = RadioButton(parent?.context)
                radioButton.text = choice
                radioButton.id = index
                radioButton.isChecked = selectedAnswers[question] == choice
                radioButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedAnswers[question] = choice
                        notifyDataSetChanged()
                    }
                }
                viewHolder.binding.choicesRadioGroup.addView(radioButton)
            }

            return view
        }

        private inner class ViewHolder(val binding: ItemQuestionBinding)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapBundle = Bundle()
        selectedAnswers.forEach { (key, value) ->
            mapBundle.putString(key, value)
        }
        outState.putBundle("selectedAnswers", mapBundle)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val mapBundle = savedInstanceState.getBundle("selectedAnswers")
        selectedAnswers.clear()
        mapBundle?.keySet()?.forEach { key ->
            selectedAnswers[key] = mapBundle.getString(key) ?: ""
        }
    }
}