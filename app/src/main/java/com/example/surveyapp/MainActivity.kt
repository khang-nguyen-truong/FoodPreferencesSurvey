package com.example.surveyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.example.surveyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.surveyTypesGroup.removeAllViews()

        for ((index, survey) in resources.getStringArray(R.array.survey_types).withIndex()) {
            val radioButton = RadioButton(this)
            radioButton.text = survey
            radioButton.id = index
            binding.surveyTypesGroup.addView(radioButton)
        }
    }

    fun startSurveyButtonOnClick(view: View) {
        val selectedSurveyTypeId : Int = binding.surveyTypesGroup.checkedRadioButtonId
        if (selectedSurveyTypeId != -1) {
            val intent = Intent(this, SurveyQuestionsActivity::class.java).apply {
                putExtra("surveyTypeId", selectedSurveyTypeId)
            }
            startActivity(intent)

        } else {
            Toast.makeText(this, "Error: Please select a survey type.", Toast.LENGTH_LONG).show()
        }
    }
}