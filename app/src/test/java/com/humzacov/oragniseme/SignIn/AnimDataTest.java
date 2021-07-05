package com.humzacov.oragniseme.SignIn;

import com.humzacov.oragniseme.AddReminder.Reminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimDataTest {

    private AnimData animData;

    @BeforeEach
    void setUp() {
        //create new blank reminder object before each test
        //(in arrange part of every test)
        animData = new AnimData();
    }

    @Test
    void getTodayMonday() {
        /* test that first result is for monday */
        //arrange is done before each test
        //Act
        String[] today = animData.getToday();
        //Assert
        assertEquals(today[0], "Monday");
    }

    @Test
    void getTodayTuesday() {
        /* test that second result is for tuesday */
        //Arrange
        animData.getToday(); //monday
        //Act
        String[] today = animData.getToday(); //should be tuesday
        //Assert
        assertEquals(today[0], "Tuesday");
    }

    @Test
    void getTodayWednesday() {
        /* test that second result is for tuesday */
        //Arrange
        animData.getToday(); //monday
        animData.getToday(); //tuesday
        //Act
        String[] today = animData.getToday(); //should be wednesday
        //Assert
        assertEquals(today[0], "Wednesday");
    }

    @Test
    void getTodayThursday() {
        /* test that second result is for tuesday */
        //Arrange
        animData.getToday(); //monday
        animData.getToday(); //tuesday
        animData.getToday(); //wednesday
        //Act
        String[] today = animData.getToday(); //should be thursday
        //Assert
        assertEquals(today[0], "Thursday");
    }

    @Test
    void getTodayFriday() {
        /* test that second result is for tuesday */
        //Arrange
        animData.getToday(); //monday
        animData.getToday(); //tuesday
        animData.getToday(); //wednesday
        animData.getToday(); //thursday
        //Act
        String[] today = animData.getToday(); //should be friday
        //Assert
        assertEquals(today[0], "Friday");
    }

    @Test
    void getTodayMondayRestart() {
        /* test that second result is for tuesday */
        //Arrange
        animData.getToday(); //monday
        animData.getToday(); //tuesday
        animData.getToday(); //wednesday
        animData.getToday(); //thursday
        animData.getToday(); //friday
        //Act
        String[] today = animData.getToday(); //should be monday again
        //Assert
        assertEquals(today[0], "Monday");
    }
}