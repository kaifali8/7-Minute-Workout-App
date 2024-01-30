package com.example.a7minutesworkout

object Constants {
    fun defaultExerciseList():ArrayList<ExerciseModel>{
        val exerciseList=ArrayList<ExerciseModel>()
        val jumpingJacks=ExerciseModel(
            1, "Jumping Jacks", R.drawable.jumping_jacks
        )
        exerciseList.add(jumpingJacks)
        val highKnees=ExerciseModel(
            2, "High Knees", R.drawable.high_knees
        )
        exerciseList.add(highKnees)
        val mountainClimber=ExerciseModel(
            3, "Mountain Climber", R.drawable.mountain_climber
        )
        exerciseList.add(mountainClimber)
        val squats=ExerciseModel(
            4, "Squats", R.drawable.squats
        )
        exerciseList.add(squats)
        val lunges=ExerciseModel(
            5, "Lunges", R.drawable.lunges
        )
        exerciseList.add(lunges)
        val pushups=ExerciseModel(
            6, "Pushups", R.drawable.push_ups
        )
        exerciseList.add(pushups)
        val invertedRows=ExerciseModel(
            7, "Inverted Rows", R.drawable.inverted_rows
        )
        exerciseList.add(invertedRows)
        val chairDips=ExerciseModel(
            8, "Chair Dips", R.drawable.chair_dips
        )
        exerciseList.add(chairDips)
        val plank=ExerciseModel(
            9, "Plank", R.drawable.plank
        )
        exerciseList.add(plank)
        val hollowBody=ExerciseModel(
            10, "Hollow Body", R.drawable.hollow_body
        )
        exerciseList.add(hollowBody)
        return exerciseList
    }
}