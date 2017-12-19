package service.helper;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by Raphael on 19.12.2017.
 */
public class ErrorCreator {

	public static JSONObject returnExistingError() {
		try {
			return new JSONObject("{\"success\": \"false\", \"reason\": " +
					"\"Die von Ihnen angeforderte Aktion konnte nicht " +
					"ausgeführt werden, da die Daten schon existieren. " +
					"Versuchen Sie einen anderen Bezeichner!\"}");
		} catch (JSONException exc) {
			return null;
		}
	}

	public static JSONObject returnEmptyResult() {
		try {
			JSONObject result = new JSONObject("{\"success\": \"false\"," +
					" \"reason\": \"Die angefragten Daten existieren " +
					"nicht!\"}");
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

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

	public static JSONObject returnClientError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Falsche Angaben im Request! Client " +
					"zeigt falsches Verhalten!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

	public static JSONObject returnInternalError() {
		try {
			return new JSONObject("{\"success\": \"false\", " +
					"\"reason\": \"Interner Fehler! Die Aktion konnte nicht " +
					"beendet werden!\"}");
		} catch (JSONException e) {
			return null;
		}
	}

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
