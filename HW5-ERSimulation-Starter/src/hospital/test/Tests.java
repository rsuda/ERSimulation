package hospital.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

import hospital.exceptions.EmptyLogException;
import hospital.exceptions.EmptyWaitingRoomException;
import hospital.exceptions.InvalidConditionException;
import hospital.exceptions.InvalidPriorityException;
import hospital.model.EmergencyConditions;
import hospital.model.EmergencyRoomLog;
import hospital.model.Patient;
import hospital.model.WaitingRoom;

public class Tests 
{

	//
	//PATIENT TESTS
	//
	Patient trevor = new Patient("Dark, Trevor", "Open Chest Wound", 23, 1);
	Patient charles = new Patient("Web, Charles", "Minor Burn", 25, 4);
	Patient kimberly = new Patient("Nark, Kimberly", "Not Breathing", 10, 1);
	Patient nikoli = new Patient("Petrov, Nikoli", "Cardiac Arrest", 23, 1);
	
	@Test
	public void testTimeArrived() 
	{
		assertEquals(23, trevor.getTimeArrived());
	}

	@Test
	public void testName() 
	{
		assertEquals("Dark, Trevor", trevor.getName());
	}
	
	@Test
	public void testCondition() 
	{
		assertEquals("Open Chest Wound", trevor.getCondition());
	}
	
	@Test
	public void testPriority() 
	{
		assertEquals(1, trevor.getPriority());
	}
	
	/**
	 * Retrieves a string representation of this Patient's details suitable for print
	 * ingor reporting. Results take the format shown below, with values in [ALL CAPS] 
	 * or [#] showingfields that would be replaced with the actual Patient's data. 
	 * Patient names and conditions are displayedin a 25 character wide, left aligned 
	 * fields. Priorities are displayed in a 3 character wide, left aligned field.Wait 
	 * times should be displayed left aligned in a variable width field based on the 
	 * number of digits in the number.One additional space should separate all fields 
	 * and labels. 
	 * 
	 * Patient: [LAST, FIRST            ] Condition: [COMPLAINT              ] Priority: [#] Waiting Since: [#]
	 * 
	 * For example:
	 * 
	 * Patient: Darr, Dexter              Condition: Minor Bleeding            Priority: 4   Waiting Since: 9
	 */
	@Test
	public void testToString() 
	{
		String s = "Patient: Dark, Trevor              Condition: Open Chest Wound          Priority: 1   Waiting Since: 23";
		assertEquals(s, trevor.toString());
	}
	
	@Test
	public void testCompareTo() 
	{
		assertEquals(-1, trevor.compareTo(charles));
		assertEquals(1, charles.compareTo(trevor));
		assertEquals(0, nikoli.compareTo(trevor));
		assertEquals(-1, kimberly.compareTo(trevor));
		assertEquals(1, trevor.compareTo(kimberly));
	}
	
	//
	//EMERGENCYCONDITIONS TESTS
	//
	
	
	@Test (expected = InvalidPriorityException.class)
	public void testTimeThreshold() throws FileNotFoundException 
	{
		EmergencyConditions condition = new EmergencyConditions("conditions.cfg");
		assertEquals(0, condition.timeThreshold(1));
		assertEquals(10, condition.timeThreshold(2));
		assertEquals(60, condition.timeThreshold(3));
		assertEquals(120, condition.timeThreshold(4));
		assertEquals(240, condition.timeThreshold(5));
		assertEquals(InvalidPriorityException.class, condition.timeThreshold(6));
	}
	
	@Test (expected = InvalidConditionException.class)
	public void testLookUpInvalidCondition() throws FileNotFoundException
	{
		EmergencyConditions condition = new EmergencyConditions("conditions.cfg");
		assertEquals(InvalidConditionException.class, condition.lookup("Parasytic Worm"));
	}
	
	@Test
	public void testLookUp() throws FileNotFoundException
	{
		EmergencyConditions condition = new EmergencyConditions("conditions.cfg");
		assertEquals(1, condition.lookup("Open Chest Wound"));
		assertEquals(2, condition.lookup("Shock"));
		assertEquals(3, condition.lookup("Stomach Pain"));
		assertEquals(4, condition.lookup("Minor Burn"));
		assertEquals(5, condition.lookup("Removal of Stitches"));
	}
	 
	@Test 
	(expected = FileNotFoundException.class)
	public void testLookUpFileNotFound() throws FileNotFoundException
	{
		EmergencyConditions condition = new EmergencyConditions("brokenconditions.cfg");
		assertEquals(FileNotFoundException.class, condition.lookup("Minor Burn"));
	}
	//
	//WAITINGROOM TESTS
	//
	
	@Test (expected = EmptyWaitingRoomException.class)
	public void testAddAndGetPatient()
	{
		WaitingRoom room = new WaitingRoom();
		room.addPatient(kimberly);
		room.addPatient(nikoli);
		room.addPatient(charles);
		room.addPatient(trevor);
		assertEquals(kimberly, room.getNextPatient());
		assertEquals(nikoli, room.getNextPatient());
		assertEquals(trevor, room.getNextPatient());
		assertEquals(charles, room.getNextPatient());
		assertEquals(EmptyWaitingRoomException.class, room.getNextPatient());
	}
	
	@Test 
	public void testNumWaiting()
	{
		WaitingRoom room = new WaitingRoom();
		room.addPatient(kimberly);
		room.addPatient(nikoli);
		room.addPatient(charles);
		room.addPatient(trevor);
		assertEquals(4, room.getNumWaiting());
		room.getNextPatient();
		room.getNextPatient();
		room.getNextPatient();
		room.getNextPatient();
		assertEquals(0, room.getNumWaiting());
	}
	
	@Test
	public void testWaitingNoneToString()
	{
		WaitingRoom room = new WaitingRoom();
		assertEquals("Waiting Room Status: EMPTY!", room.toString());
		room.addPatient(kimberly);
		room.addPatient(nikoli);
		room.addPatient(charles);
		room.addPatient(trevor);
		String s = "Waiting Room Status:\n" + 
				"    1) Patient: Nark, Kimberly            Condition: Not Breathing             Priority: 1   Waiting Since: 10\n" + 
				"    2) Patient: Petrov, Nikoli            Condition: Cardiac Arrest            Priority: 1   Waiting Since: 23\n" + 
				"    3) Patient: Dark, Trevor              Condition: Open Chest Wound          Priority: 1   Waiting Since: 23\n" + 
				"    4) Patient: Web, Charles              Condition: Minor Burn                Priority: 4   Waiting Since: 25";
		assertEquals(s, room.toString());
	}
	
	@Test
	public void testEmergencyRoomClock()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		assertEquals(0, emergencyRoom.getERClock());
		emergencyRoom.incrementERClock();
		emergencyRoom.incrementERClock();
		emergencyRoom.incrementERClock();
		assertEquals(3, emergencyRoom.getERClock());
	}
	
	@Test
	public void testTreatPatientSeenByDoctor()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(3, emergencyRoom.numPatientsSeenBy("Dr. Hyde"));
		assertEquals(0, emergencyRoom.numPatientsSeenBy("Dr. Sheldon"));
		assertEquals(6, emergencyRoom.numPatientsSeenBy(null));
	}
	
	@Test (expected = EmptyLogException.class)
	public void testTreatPatientSeenByPriorityEmptyLog()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		assertEquals(EmptyLogException.class, emergencyRoom.numPatientsSeen(2));
	}
	
	@Test (expected = InvalidPriorityException.class)
	public void testTreatPatientSeenByPriorityInvalidPriorityBelow()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(InvalidPriorityException.class, emergencyRoom.numPatientsSeen(-5));
	}
	
	@Test (expected = InvalidPriorityException.class)
	public void testTreatPatientSeenByPriorityInvalidPriorityAbove()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(InvalidPriorityException.class, emergencyRoom.numPatientsSeen(6));
	}
	
	@Test
	public void testTreatPatientSeenByPriority()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(4, emergencyRoom.numPatientsSeen(1));
		assertEquals(6, emergencyRoom.numPatientsSeen(0));
	}
	
	@Test (expected = EmptyLogException.class)
	public void testAverageWaitTimeEmptyLog()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		assertEquals(EmptyLogException.class, emergencyRoom.getAverageWaitTime(2));
	}
	
	@Test (expected = InvalidPriorityException.class)
	public void testAverageWaitTimeInvalidPrioriyBelow()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(InvalidPriorityException.class, emergencyRoom.getAverageWaitTime(-5));
	}
	
	@Test (expected = InvalidPriorityException.class)
	public void testAverageWaitTimeInvalidPrioriyAbove()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(InvalidPriorityException.class, emergencyRoom.getAverageWaitTime(6));
	}
	
	/**
	 * Patient trevor = new Patient("Dark, Trevor", "Open Chest Wound", 23, 1);
	Patient charles = new Patient("Web, Charles", "Minor Burn", 25, 4);
	Patient kimberly = new Patient("Nark, Kimberly", "Not Breathing", 10, 1);
	Patient nikoli = new Patient("Petrov, Nikoli", "Cardiac Arrest", 23, 1);
	 */
	@Test
	public void testAverageWaitTime()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		for (int i = 0; i < 30; i++)
		{
			emergencyRoom.incrementERClock();
		}
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.incrementERClock();
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.incrementERClock();
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(14.5, emergencyRoom.getAverageWaitTime(1), 0.001);
		assertEquals(11.833, emergencyRoom.getAverageWaitTime(0), 0.001);
	}
	
	@Test (expected = EmptyLogException.class)
	public void testPatientsOverThresholdEmptyLog()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		assertEquals(EmptyLogException.class, emergencyRoom.numPatientsOverThreshold(2));
	}
	
	@Test (expected = InvalidPriorityException.class)
	public void testPatientsOverThresholdBelow()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		emergencyRoom.incrementERClock();
		assertEquals(InvalidPriorityException.class, emergencyRoom.numPatientsOverThreshold(-5));
	}
	
	@Test (expected = InvalidPriorityException.class)
	public void testPatientsOverThresholdAbove()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		emergencyRoom.incrementERClock();
		assertEquals(InvalidPriorityException.class, emergencyRoom.numPatientsOverThreshold(6));
	}
	
	@Test
	public void testPatientsOverThreshold()
	{
		EmergencyRoomLog emergencyRoom = new EmergencyRoomLog();
		for (int i = 0; i < 60; i++)
		{
			emergencyRoom.incrementERClock();
		}
		emergencyRoom.treatPatient(trevor, "Dr. Hyde");
		emergencyRoom.treatPatient(kimberly, "Dr. Hyde");
		emergencyRoom.treatPatient(charles, "Dr. Hyde");
		emergencyRoom.treatPatient(trevor, "Dr. Bortles");
		emergencyRoom.treatPatient(kimberly, "Dr. Bortles");
		emergencyRoom.treatPatient(charles, "Dr. Bortles");
		assertEquals(4, emergencyRoom.numPatientsOverThreshold(1));
		assertEquals(4, emergencyRoom.numPatientsOverThreshold(0));
	}
}
