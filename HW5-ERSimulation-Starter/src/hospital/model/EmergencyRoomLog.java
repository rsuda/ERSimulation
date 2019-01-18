// COURSE: CSCI1620
// TERM: FALL 2018
// 
// NAME: Robin Suda and Tyler Labreck
// RESOURCES: We used the javaDoc, lecture slides, and the book.
package hospital.model;

import java.io.Serializable;
import java.util.ArrayList;

import hospital.exceptions.EmptyLogException;
import hospital.exceptions.InvalidPriorityException;

/**
 * This class stores log information about Patients served by the EmergencyRoom 
 * doctors. The log maintains state information about the current timein the 
 * hospital and records timestamped patient activity. This class alsoprovides 
 * methods to compute performance data about the ER's wait times andoverall 
 * compliance with hospital-policy thresholds.
 * @author tlabreck and rsuda
 *
 */
public class EmergencyRoomLog implements Serializable
{
	/**
	 * The max priority available.
	 */
	private static final int MAX_PRIORITY = 5;
	
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * ArrayList of treated patients.
	 */
	private ArrayList<Patient> treatedPatients;
	
	/**
	 * ArrayList of doctors.
	 */
	private ArrayList<String> doctors;
	
	/**
	 * ArrayList of the time a patient has been seen.
	 */
	private ArrayList<Integer> timeSeen;
	
	/**
	 * The ER clock that increments by 1.
	 */
	private int clock;

	/**
	 * Creates an empty ER Log. The log will initialize the ERclock to 0.
	 */
	public EmergencyRoomLog()
	{
		treatedPatients = new ArrayList<Patient>();
		doctors = new ArrayList<String>();
		timeSeen = new ArrayList<Integer>();
		clock = 0;
	}
	
	/**
	 * Retrieves the current ER clock time, in minutes.
	 * @return The current time in the ER.
	 */
	public int getERClock()
	{
		return clock;
	}
	
	/**
	 * Increments the ER clock by one minute.
	 */
	public void incrementERClock()
	{ 
		clock++;
	}

	/**
	 * Enters a Patient into the EmergencyRoomLog when they have left the 
	 * WaitingRoom.This method records the patient seen, the doctor that 
	 * saw them, and internallyrecords the time when this patient was seen 
	 * based on the current ER clock time.
	 * @param thePatient The Patient being seen by the physician.
	 * @param seenBy The name of the physician treating the Patient in 
	 * "Last, First" format.
	 */
	public void treatPatient(Patient thePatient, java.lang.String seenBy)
	{
		treatedPatients.add(thePatient);
		doctors.add(seenBy);
		timeSeen.add(clock);
	}
	
	/**
	 * Retrieves the number of patients seen by a given physician since this 
	 * EmergencyRoomLogwas created.
	 * @param treatingPhysician The name of the doctor of interest in 
	 * "Last, First" format.
	 * @return The total number of patients seen so far by the treatingPhysican 
	 * in the log.Physicians are identified in a case sensitive manner. 
	 * 
	 * When null is specified as the treatingPhysician the total number of 
	 * patients seen by all doctors will be returned.
	 */
	public int numPatientsSeenBy(java.lang.String treatingPhysician)
	{
		int count = 0;
		for (int i = 0; i < doctors.size(); i++)
		{
			if (treatingPhysician == null || doctors.get(i).equals(treatingPhysician))
			{
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Retrieves the number of patients seen at a given priority level.
	 * @param priority The priority on which to filter results. If 0 the total 
	 * will be forall patients seen. Priorities of 1-5 will retrieve the total 
	 * for onlypatients with conditions matching the specified provided 
	 * priority.
	 * @return The number of patients seen at the specified priority.
	 * @throws EmptyLogException - when no data is present in the ER Log.
	 * @throws InvalidPriorityException - when the specified priority is outside 
	 * the range 0-5, inclusive
	 */
	public int numPatientsSeen(int priority) throws EmptyLogException, InvalidPriorityException
	{
		if (treatedPatients.size() == 0)
		{
			throw new EmptyLogException();
		}
		if (priority > MAX_PRIORITY || priority < 0)
		{
			throw new InvalidPriorityException();
		}
		else
		{
			int count = 0;
			for (int i = 0; i < doctors.size(); i++)
			{
				if (treatedPatients.get(i).getPriority() == priority || priority == 0)
				{
					count++;
				}
			}
			return count;
		}
	}
	
	/**
	 * Retrieves the average wait time patients seen thus far since opening 
	 * the EmergencyRoomLog.The data may be optionally filtered based on the 
	 * triage priority level. 
	 * 
	 * Note: Patients still in a WaitingRoom do not factor into the these
	 * statistics.
	 * @param priority The priority on which to filter results. If 0 the 
	 * average will be forall patients seen. Priorities of 1-5 will retrieve 
	 * the average for only patients with conditions matching the specified 
	 * provided priority.
	 * @return The average wait time for the data specified.
	 * @throws EmptyLogException - when no data is present in the ER Log.
	 * @throws InvalidPriorityException - when the specified priority is outside 
	 * the range 0-5, inclusive
	 */
	public double getAverageWaitTime(int priority) throws EmptyLogException, InvalidPriorityException
	{
		if (treatedPatients.size() == 0)
		{
			throw new EmptyLogException();
		}
		if (priority > MAX_PRIORITY || priority < 0)
		{
			throw new InvalidPriorityException();
		}
		else
		{
			double sum = 0.0;
			int divider = 0;
			for (int i = 0; i < treatedPatients.size(); i++)
			{
				if (priority == 0)
				{
					int patientTime = timeSeen.get(i) - treatedPatients.get(i).getTimeArrived();
					divider++;
					sum += patientTime;
				}
				else if (treatedPatients.get(i).getPriority() == priority)
				{
					int patientTime = timeSeen.get(i) - treatedPatients.get(i).getTimeArrived();
					divider++;
					sum += patientTime;
				}
			}
			double average = sum / divider;
			return average;
		}
	}
	
	/**
	 * Computes the number of patients in the EmergencyRoomLog whose total wait 
	 * time prior tobeing seen exceeds the hospital specified thresholds for their 
	 * priority level.
	 * @param priority The priority level of interest (1-5, inclusive)
	 * @return The total number of patients who waited more than the number of 
	 * minutes specifiedby the hospital's response threshold.
	 * @throws EmptyLogException - when no data is present in the ER Log.
	 * @throws InvalidPriorityException - when the specified priority is outside 
	 * the range 0-5, inclusive
	 */
	public int numPatientsOverThreshold(int priority) throws EmptyLogException, InvalidPriorityException
	{
		if (treatedPatients.size() == 0)
		{
			throw new EmptyLogException();
		}
		if (priority > MAX_PRIORITY || priority < 0)
		{
			throw new InvalidPriorityException();
		}
		else
		{
			int count = 0;
			for (int i = 0; i < treatedPatients.size(); i++)
			{
				if (priority == 0 && EmergencyConditions.timeThreshold(
						treatedPatients.get(i).getPriority()) 
						< timeSeen.get(i) - treatedPatients.get(i).getTimeArrived())
				{
					count++;
				}
				else if ((treatedPatients.get(i).getPriority() == priority) 
						&& EmergencyConditions.timeThreshold(priority) < timeSeen.get(i) 
						- treatedPatients.get(i).getTimeArrived())
				{
					count++;
				}
			}
			return count;
		}
	}
}
