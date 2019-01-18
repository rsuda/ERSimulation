// COURSE: CSCI1620
// TERM: FALL 2018
// 
// NAME: Robin Suda and Tyler Labreck
// RESOURCES: We used the javaDoc, lecture slides, and the book.
package hospital.model;

import java.io.File;
import java.util.Scanner;

import hospital.exceptions.InvalidConditionException;
import hospital.exceptions.InvalidPriorityException;

/**
 * This class stores and provides information about various emergency 
 * conditions recognized by the hospital's ER. Conditions and their triage 
 * ratings are loaded from a configuration file at the time the 
 * EmergeyConditions object is created.
 * @author tlabreck and rsuda
 *
 */
public class EmergencyConditions 
{
	/**
	 * The time threshold for priority 1.
	 */
	private static final int ZERO = 0;
	
	/**
	 * The time threshold for priority 2.
	 */
	private static final int TEN = 10;
	
	/**
	 * The time threshold for priority 3.
	 */
	private static final int HOUR = 60;
	
	/**
	 * The time threshold for priority 4.
	 */
	private static final int TWOHOUR = 120;
	
	/**
	 * The time threshold for priority 5.
	 */
	private static final int FOURHOUR = 240;
	
	/**
	 * The priority level of the patient.
	 */
	private static final int PRIORITY_ONE = 1;
	
	/**
	 * The priority level of the patient.
	 */
	private static final int PRIORITY_TWO = 2;
	
	/**
	 * The priority level of the patient.
	 */
	private static final int PRIORITY_THREE = 3;
	
	/**
	 * The priority level of the patient.
	 */
	private static final int PRIORITY_FOUR = 4;
	
	/**
	 * The priority level of the patient.
	 */
	private static final int PRIORITY_FIVE = 5;
	
	/**
	 * Scanner for the lookup method.
	 */
	private Scanner test;
	
	/**
	 * Creates a new EmergencyConditions object based on comma separated 
	 * dataprovided in a specified configuration file. This method assumes 
	 * that thefile, if present, is valid and contains a series of 
	 * records formatted as: Condition Name,#
	 * 
	 * Where "Condition Name" is a string of potentially several words 
	 * (including spaces andspecial characters like hypens) and # is a 
	 * single integer value in the range 1-5 (inclusive)corresponding to 
	 * the associated triage level.
	 * @param fileName The name of the configuration file to be read.
	 * @throws java.io.FileNotFoundException occurs when the specified 
	 * file does not exist or cannotbe opened for reading.
	 */
	public EmergencyConditions(java.lang.String fileName) throws java.io.FileNotFoundException
	{
		File newFile = new File(fileName);
		test = new Scanner(newFile);
	}
	
	/**
	 * This method retrieves triage compliance thresholds (in minutes) per 
	 * fixedhospital policy. The rules are: 
	 * Priority 1: Immediate attention required and should be seen immediately 
	 * with a 0 minute wait. 
	 * Priority 2: Very urgent attention needed and should be seen within 10 
	 * minutes of arriving. 
	 * Priority 3: Urgent attention needed and should be seen within 60 
	 * minutes of arriving. 
	 * Priority 4: Standard attention. Should be seen within 2 hours. 
	 * Priority 5: Not an emergency. Should be seen within 4 hours of arriving.
	 * @param priority The priority level of interest.
	 * @return The wait threshold corresponding to the specified priority.
	 * @throws InvalidPriorityException - when the priority specified lies outside 
	 * of the range 1-5, inclusive.
	 */
	public static int timeThreshold(int priority)
	{
		int waitTime = 0;
		
		if (priority == PRIORITY_ONE)
		{
			waitTime = ZERO;
		}
		else if (priority == PRIORITY_TWO)
		{
			waitTime = TEN;
		}
		else if (priority == PRIORITY_THREE)
		{
			waitTime = HOUR;
		}
		else if (priority == PRIORITY_FOUR)
		{
			waitTime = TWOHOUR;
		}
		else if (priority == PRIORITY_FIVE)
		{
			waitTime = FOURHOUR;
		}
		else
		{
			throw new InvalidPriorityException();
		}
		return waitTime;
	}
	
	/**
	 * This method looks up the priority level for a specified condition per 
	 * thehospital's current configuration file. String matches are considered 
	 * in acase-sensitive manner, including spacing and punctuation.
	 * @param conditionName A string containing the condition of interest.
	 * @return A priority value 1-5 corresponding to the provided condition.
	 * @throws InvalidConditionException - if a condition is specified that does not existin the hospital's 
	 * configuration file.
	 */
	public int lookup(java.lang.String conditionName) throws InvalidConditionException
	{
		boolean flag = false;
			
		//While there are still lines to read
		while (test.hasNextLine() && !flag)
		{
			String record = test.nextLine();
				
			@SuppressWarnings("resource")
			Scanner lineParser = new Scanner(record);
			lineParser.useDelimiter(",");
				
			String condition = lineParser.next();
			int priorityLookup = lineParser.nextInt();
				
			if (condition.equals(conditionName))
			{
				flag = true;
				lineParser.close();
				return priorityLookup;
			}
			lineParser.close();
		}
		if (!flag)
		{
			test.close();
			throw new InvalidConditionException(conditionName);
		}
		test.close();
		return 1;
	}
}
