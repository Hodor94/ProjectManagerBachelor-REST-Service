package entity;

/**
 * This enum represents the role of a user in the system. As an admin he or
 * she will manage a team. As a project manager he or she will rule a project.
 * The last role is the standard user role.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
public enum UserRole {

	// id = 0 - User has just standard rights
	USER,
	// id = 1 - User is an manager of a project
	PROJECT_OWNER,
	// id = 2 - User is a team administrator
	ADMINISTRATOR

}

