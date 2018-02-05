package entity;

/**
 * This enum represents the participation of a user at an appointment. He or
 * she can take part, decline or tell the system that it's not sure if he or
 * she will participate.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
public enum StatisticParticipationAnswer {

	// index 0 - User takes part in an appointment
	YES,
	// index 1 - User does not take part in an appointment
	NO,
	// index 2 - User is not sure about taking part in an appointment
	MAYBE
}
