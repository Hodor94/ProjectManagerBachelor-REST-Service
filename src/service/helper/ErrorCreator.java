package service.helper;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class is used to create error messages of the system in JSON format.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since version 1.0
 */
public class ErrorCreator {

	/**
	 * Creates an error message if a data set already exists and cannot be
	 * created in
	 * the system.
	 *
	 * @return A JSONObject with the error and it's reason saved in it.
	 */
	public static JSONObject returnExistingError() {
		try {
			return new JSONObject("{\"success\": \"false\", \"reason\": "
					+ "\"Die von Ihnen angeforderte Aktion konnte nicht " +
					"ausgeführt werden, da die Daten schon existieren. " +
					"Versuchen Sie einen anderen Bezeichner!\"}");
		} catch (JSONException exc) {
			return null;
		}
	}

	/**
	 * Creates a error message if requested data does not exist in the system.
	 *
	 * @return A JSONObject with the error message and it's reason saved in it.
	 */
	public static JSONObject returnEmptyResult() {
		try {
			return new JSONObject("{\"success\": " +
					"\"false\"," +
					" \"reason\": \"Die angefragten Daten existieren " +
					"nicht!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Creates a error message if the user does not have the permission to
	 * fulfil the action he or she wanted to perform.
	 *
	 * @return A JSONObject with the error message and it's reason saved in it.
	 */
	public static JSONObject returnNoRightsError() {
		try {
			JSONObject result = new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Die Berechtigung für diese Aktion ist " +
					"nicht gewährleistet!\n" +
					"Entweder Ihre Session ist abgelaufen oder Sie haben " +
					"nicht die nötigen Rechte für die Aktion!\n" +
					"Bitte loggen Sie sich erneut ein und versuchen Sie es" +
					".\"}");
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Creates a error message in case that the client did some wrong actions
	 * or sent wrong data.
	 *
	 * @return A JSONObject with the error message an it's reason saved in it.
	 */
	public static JSONObject returnClientError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Falsche Angaben im Request! Client " +
					"zeigt falsches Verhalten!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Creates a error message in case that on the server side an action went
	 * wrong.
	 *
	 * @return A JSONObject with the error message and it's reason saved in it.
	 */
	public static JSONObject returnInternalError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Interner Fehler! Die Aktion konnte nicht " +
					"beendet werden!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Creates a error message if there was a failure during a server action.
	 *
	 * @return A JSONObject with the error message and it's reason saved in it.
	 */
	public static JSONObject returnServerError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Der Server zeigt falsches Verhalten! " +
					"Bitte melden Sie dies an den Administrator unter " +
					"grum02@gw.uni-passau.de.\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Creates a error message if a user wants to join another team although
	 * he or she is already in a team.
	 *
	 * @return A JSONObject with the information that the user is already in
	 * a team and the join action did fail.
	 */
	public static JSONObject returnUserAlreadyInTeam() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Der eingeladene Nutzer ist bereits in " +
					"einem Team.\"}");
		} catch (JSONException e) {
			return null;
		}
	}
}
