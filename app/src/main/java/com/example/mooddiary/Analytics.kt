package com.example.mooddiary

class Analytics {
    var weekIndex : Int? = 0
    var overall_analytics : Emotions? = null
    var weekly_analytics : Array<Emotions>? = null
    // Monday = 0 ... Sunday = 6

    constructor(weekIndex: Int) : super() {
        this.weekIndex = weekIndex
        this.overall_analytics = Emotions()
        this.weekly_analytics = arrayOf(Emotions(), Emotions(), Emotions(), Emotions(), Emotions(), Emotions(), Emotions())
    }

    fun resetWeek (weekIndex : Int) {
        this.weekIndex = weekIndex
        this.weekly_analytics = arrayOf(Emotions(), Emotions(), Emotions(), Emotions(), Emotions(), Emotions(), Emotions())
    }
}

class Emotions {
    var happy : Int = 0
    var delight : Int = 0
    var excite : Int = 0
    var sad : Int = 0
    var awful : Int = 0
    var exhausted : Int = 0
    var anger : Int = 0
    var outrage : Int = 0
    var scared : Int = 0

    constructor() : super() {}
}