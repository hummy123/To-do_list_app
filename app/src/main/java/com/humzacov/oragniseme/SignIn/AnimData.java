package com.humzacov.oragniseme.SignIn;

public class AnimData {
    //logo-animation strings
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private final String[] firstTask = {"Plan Article", "Write Draft", "Edit Draft", "Finish Article", "Submit Article"};
    private final String[] secondTask = {"Design App", "Develop App", "Test App", "Publish App", "Review Feedback"};
    private final String[] thirdTask = {"Email Colleague", "Call Parents", "Book Meeting", "Visit Doctor", "Attend Lecture"};

    private int dayNum = 0; //which day's tasks to display

    protected String[] getToday() {
        String[] today = {days[dayNum], firstTask[dayNum], secondTask[dayNum], thirdTask[dayNum]};
        dayNum += 1; //increment to next day

        //reset to monday when reached Friday
        if (dayNum == 5) {
            dayNum = 0;
        }

        return today; //return today's data
    }
}
