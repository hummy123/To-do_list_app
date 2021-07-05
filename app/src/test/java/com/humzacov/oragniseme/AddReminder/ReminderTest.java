package com.humzacov.oragniseme.AddReminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReminderTest {

    private Reminder reminder;

    @BeforeEach
    void setUp() {
        //create new blank reminder object before each test
        reminder = new Reminder();
    }

    @Test
    void setTitle() {
        //Arrange (declare title string and then get from object)
        String testTitle = "title";
        //act (invoke setTitle method)
        reminder.setTitle(testTitle);
        //assert (is the title the same as expected?)
        String reminderTitle = reminder.getTitle(); //get result of act
        assertEquals(testTitle, reminderTitle); //should be the same
    }

    @Test
    void setDescription() {
        //arrange
        String testDesc = "description";
        //act
        reminder.setDescription(testDesc);
        //assert
        String reminderDesc = reminder.getDescription(); //get result of act
        assertEquals(testDesc, reminderDesc); //should be the same
    }

    @Test
    void setDate() {
        //arrange
        String testDate = "2021-08-12";
        //act
        reminder.setDate(testDate);
        //assert
        String reminderDate = reminder.getDate(); //get result of act
        assertEquals(testDate, reminderDate); //should be the same
    }

    @Test
    void setTime() {
        //arrange
        String testTime = "06:00";
        //act
        reminder.setTime(testTime);
        //assert
        String reminderTime = reminder.getTime(); //get result of act
        assertEquals(testTime, reminderTime); //should be the same
    }

    @Test
    void setOwner() {
        //arrange
        String testOwner = "shahidh7@uni.coventry.ac.uk";
        //act
        reminder.setOwner(testOwner);
        //assert
        String reminderOwner = reminder.getOwner(); //get result of act
        assertEquals(testOwner, reminderOwner); //should be the same
    }

    @Test
    void setPrivacySettings() {
        //arrange
        String testPrivacy = "Private";
        //act
        reminder.setPrivacySettings(testPrivacy);
        //assert
        String reminderPrivacy = reminder.getPrivacySettings(); //get result of act
        assertEquals(testPrivacy, reminderPrivacy); //should be the same
    }

    @Test
    void setShareCode() {
        //arrange
        String testCode = "00-00-00-00";
        //act
        reminder.setShareCode(testCode);
        //assert
        String reminderCode = reminder.getShareCode(); //get result of act
        assertEquals(testCode, reminderCode); //should be the same
    }

    @Test
    void setRepeatSettings() {
        //arrange
        String testRepeat = "Never";
        //act
        reminder.setRepeatSettings(testRepeat);
        //assert
        String reminderRepeat = reminder.getRepeatSettings(); //get result of act
        assertEquals(testRepeat, reminderRepeat); //should be the same
    }

    @Test
    void setPhoto() {
        //arrange
        String testPhoto = "https://firebasestorage.googleapis.com/v0/b/organise-me-1623844702321.appspot.com/o/photos%2F-MdpHllLQCwF4JTNtUZ9.jpg?alt=media&token=5fee0636-74e7-4c76-97fc-240bc2e931e3";
        //act
        reminder.setPhoto(testPhoto);
        //assert
        String reminderPhoto = reminder.getPhoto(); //get result of act
        assertEquals(testPhoto, reminderPhoto); //should be the same
    }

    @Test
    void setRecordings() {
        //arrange
        String testRecording = "https://firebasestorage.googleapis.com/v0/b/organise-me-1623844702321.appspot.com/o/recordings%2F-MdpPOxwDWM2qkJe1rHb.3gp?alt=media&token=abd16e23-0899-4cd6-8386-17d072884f16";
        //act
        reminder.setRecordings(testRecording);
        //assert
        String reminderRecording = reminder.getRecordings(); //get result of act
        assertEquals(testRecording, reminderRecording); //should be the same
    }

    @Test
    void setLocation() {
        //arrange
        String testLocation = "Coventry";
        //act
        reminder.setLocation(testLocation);
        //assert
        String reminderLocation = reminder.getLocation(); //get result of act
        assertEquals(testLocation, reminderLocation); //should be the same
    }
}