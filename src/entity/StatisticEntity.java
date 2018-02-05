package entity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class is used by the framework Hibernate to work with the database
 * and represents a participation statistic of a user in the system.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "statistic")
public class StatisticEntity extends GenericEntity {

	// Attributes without any relation to other entities
	@Column(name = "numberOfParticipiation")
	private double numberOfParticipiation;

	@Column(name = "percentage")
	private double percentage;

	@Column(name = "numberOfAllAppointments")
	private double numberOfAllAppointments;

	// Attributes related to other entities
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user")
	private UserEntity user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project")
	private ProjectEntity project;

	/**
	 * Creates a StatisticEntity object with all attributes set with the
	 * default values.
	 */
	public StatisticEntity() {
		super();
	}

	/**
	 * Creates a StatisticEntity object with the user and the project set
	 * with parameter values.
	 *
	 * @param user The user the statistic belongs to.
	 * @param project The project the statistic is about.
	 */
	public StatisticEntity(UserEntity user, ProjectEntity project) {
		this.user = user;
		this.project = project;
	}

	/**
	 * Returns the number of the users participations.
	 *
	 * @return The number of appointments the user takes part in.
	 */
	public double getNumberOfParticipiation() {
		return numberOfParticipiation;
	}

	/**
	 * Sets the number of participations of the user in this statistic.
	 *
	 * @param numberOfParticipiation The number of appointments the user is
	 *                                  taking part in.
	 */
	public void setNumberOfParticipiation(int numberOfParticipiation) {
		this.numberOfParticipiation = numberOfParticipiation;
	}

	/**
	 * Calculates the percentage number of participations of the user.
	 *
	 * @return A double percentage number of the participations of the user.
	 */
	public double getPercentage() {
		return numberOfParticipiation / numberOfAllAppointments;
	}

	/**
	 * Saves the calculated participation percentage in the attribute
	 * 'percentage'.
	 */
	public void calculatePercentage() {
		this.percentage = numberOfParticipiation / numberOfAllAppointments;
	}

	/**
	 * Sets the percentage number of the participations of the user.
	 *
	 * @param percentage The percentage number of the participations of the
	 *                      user.
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * Returns the number of appointments the user of this statistic takes
	 * part in.
	 *
	 * @return The number of appointments the user takes part in.
	 */
	public double getNumberOfAllAppointments() {
		return numberOfAllAppointments;
	}

	/**
	 * Sets the number of appointments the user takes part in.
	 *
	 * @param numberOfAllAppointments The new number of appointments the user
	 *                                  takes part.
	 */
	public void setNumberOfAllAppointments(int numberOfAllAppointments) {
		this.numberOfAllAppointments = numberOfAllAppointments;
	}

	/**
	 * Returns the user who belongs to this statistic.
	 *
	 * @return The user of this statistic.
	 */
	public UserEntity getUser() {
		return user;
	}

	/**
	 * Returns the project this statistic belongs to.
	 *
	 * @return The project of this statistic.
	 */
	public ProjectEntity getProject() {
		return project;
	}

	/**
	 * Sets the user of this statistic.
	 *
	 * @param user The user this statistic belongs to.
	 */
	public void setUser(UserEntity user) {
		this.user = user;
	}

	/**
	 * Sets the project this statistic belongs to.
	 *
	 * @param project The project of this statistic.
	 */
	public void setProject(ProjectEntity project) {
		this.project = project;
	}

	/**
	 * Transforms this StatisticEntitiy object into a JSON format String.
	 *
	 * @return This object as a JSON format String.
	 */
	public String toString() {
		String result;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\", ");
		stringBuilder.append("\"numberOfParticipiation\": " + "\""
				+ this.numberOfParticipiation + "\", ");
		stringBuilder.append("\"numberOfAllAppointments\": " + "\""
				+ this.numberOfAllAppointments + "\", ");
		stringBuilder.append("\"percentage\": " + "\"" + this.percentage
				+ "\", ");
		stringBuilder.append(appendJSONUserName(user));
		stringBuilder.append(appendJSONProject(project));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the username of the statistics user as a JSON attribute.
	 */
	private String appendJSONUserName(UserEntity user) {
		if (user != null && user.getUsername() != null && !(user.getUsername().equals(""))) {
			return "\"user\": " + "\""
					+ user.getUsername() + "\", ";
		} else {
			return "\"user\": " + null + ", ";
		}
	}

	/*
	Returns the statistics project as a JSON attribute.
	 */
	private String appendJSONProject(ProjectEntity project) {
		if (project != null) {
			return "\"project\": " + project.toString();
		} else {
			return "\"project\": " + null;
		}
	}

	/**
	 * Increases the attribute 'numberOfAllAppointments' by one.
	 */
	public void increaseNumberOfAllAppointments() {
		numberOfAllAppointments++;
		calculatePercentage();
	}

	/**
	 * Increases the attribute 'numberOfParticipation' by one.
	 */
	public void increaseNumberOfParticipation() {
		numberOfParticipiation++;
		calculatePercentage();
	}

	/**
	 * Decreases the attribute 'numberOfAllAppointments' by one.
	 */
	public void decreaseNumberOfAllAppointments() {
		if (numberOfAllAppointments != 0 && numberOfAllAppointments > 0) {
			numberOfAllAppointments--;
			calculatePercentage();
		}
	}

	/**
	 * Decreases the attribute 'numberOfParticipaton' by one.
	 */
	public void decreaseNumberOfParticipation() {
		if (numberOfParticipiation != 0 && numberOfParticipiation > 0) {
			numberOfParticipiation--;
			calculatePercentage();
		}
	}

}
