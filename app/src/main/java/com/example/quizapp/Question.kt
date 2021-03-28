package com.example.quizapp

class Question {

    public var id: Int = -1

    public var text: String = "Generic question?"
    public val options: MutableList<Option> = mutableListOf<Option>()

    public var isAnswered = false

    override fun toString(): String {
        return text + '\n' + options.toString() + '\n'
    }

}

class Option {

    public var text: String = "Generic answer"
    public var isCorrect: Boolean = true

    override fun toString(): String {
        return "$text $isCorrect"
    }

}