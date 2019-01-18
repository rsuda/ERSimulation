// COURSE: CSCI1620
// TERM: FALL 2018
// 
// NAME: Robin Suda and Tyler Labreck
// RESOURCES: We used the javaDoc, lecture slides, and the book.
package hospital.model;

import java.io.Serializable;
import java.util.ArrayList;

import hospital.exceptions.EmptyWaitingRoomException;

/**
 * This class abstracts details of an ER waiting room consisting of a list of 
 * Patients.WaitingRoom objects provide an urgency-ordered view of the Patients 
 * consistent withmedical triage principles. More urgent patients will be 
 * placed in the list aheadof less urgent patients.
 * @author tlabreck	and rsuda
 *
 */
public class WaitingRoom implements Serializable
{
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ArrayList of patients.
	 */
	private ArrayList<Patient> patients;
	
	/**
	 * Builds a new empty waiting room with no Patients.
	 */
	public WaitingRoom()
	{
		patients = new ArrayList<Patient>();
	}
	
	/**
	 * Adds a specified Patient to the waiting room while preserving the 
	 * urgency-orderingof Patients based on the triage rules described by 
	 * Patient.compareTo.
	 * @param sickPerson The newly arrived Patient to place in this WaitingRoom.
	 */
	public void addPatient(Patient sickPerson)
	{
		if (patients.size() == 0)
		{
			patients.add(sickPerson);
		}
		else
		{
			boolean flag = false;
			for (int i = 0; i < patients.size(); i++)
			{
				if (sickPerson.compareTo(patients.get(i)) == -1)
				{
					flag = true;
					patients.add(i, sickPerson);
					break;
				}
			}
			if (!flag)
			{
				patients.add(sickPerson);
			}
		}
	}
	
	/**
	 * Retrieves the next most-urgent person from the WaitingRoom. This method 
	 * also removes thePatient from the WaitingRoom as they are now being seen 
	 * by a physician.
	 * @return The most-urgent Patient object that was in the WaitingRoom.
	 * @throws EmptyWaitingRoomException - when there are no patients waiting 
	 * to be seen.
	 */
	public Patient getNextPatient()
	{
		if (patients.size() == 0)
		{
			throw new EmptyWaitingRoomException();
		}
		else
		{
			return patients.remove(0);
		}
	}
	/**
	 * Retrieves the current total number of patients waiting in the WaitingRoom.
	 * @return The number of patients waiting.
	 */
	public int getNumWaiting()
	{
		return patients.size();
	}

	/**
	 * Produces a String containing details about the current status of this 
	 * WaitingRoom. Data is formattedas shown below. Fields to be replaced with 
	 * actual values are shown in [VALUE] notation. 
	 * 
	 * When no patients are waiting 
	 * 
	 * Waiting Room Status: EMPTY! 
	 * 
	 * When one or more patients are waiting 
	 * 
	 * Waiting Room Status: 
	 * [  #]) [FIRST PATIENT INFORMATION]
	 * [  #]) [SECOND PATIENT INFOMRATION]
	 * 
	 * Waiting room positions should be displayed in a 5 character wide, right 
	 * aligned field followed by a right parenthesis and asingle space 
	 * character. Patient information is then presented per the toString format 
	 * described in the Patient class. For example: 
	 * 
	 * Waiting Room Status: 
	 * 1) Patient: Levy, Thomasine           Condition: Asthma                    Priority: 3   Waiting Since: 5
	 * 2) Patient: Figueroa, Francesco       Condition: High Fever                Priority: 3   Waiting Since: 8
	 * 3) Patient: Rakes, Raye               Condition: Minor Allergic Reaction   Priority: 3   Waiting Since: 9
	 * 4) Patient: Noles, Oliver             Condition: Low-grade Fever           Priority: 4   Waiting Since: 4
	 * 
	 * When one or more patients are shown in the status, the patient listed as 
	 * #1 corresponds to thePatient that would be served next (assuming no 
	 * additional Patients were added subsequently).
	 * @return the whole waiting room full of patients. 
	 */
	public java.lang.String toString()
	{
		String finalWaitList = "";
		if (patients.size() == 0)
		{
			finalWaitList = String.format("Waiting Room Status: EMPTY!");
		}
		else
		{
			finalWaitList = "Waiting Room Status:\n";
			int count = 1;
			for (int i = 0; i < patients.size(); i++)
			{
				String patientInfo;
				if (i < patients.size() - 1)
				{
					patientInfo = String.format(" %4d) %s\n", count, patients.get(i).toString());
					finalWaitList += patientInfo;
					count++;
				}
				else if (i == patients.size() - 1)
				{
					patientInfo = String.format(" %4d) %s", count, patients.get(i).toString());
					finalWaitList += patientInfo;
				}
			}
		}
		return finalWaitList;
	}
}
