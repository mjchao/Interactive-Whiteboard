package mc.iwclient.structs;

/**
 * Stores information about one user, including username, display name, date
 * of birth, etc.
 * 
 * @author mjchao
 *
 */
public class UserData {

	public String m_username = "";
	
	public String m_displayName = "";
	
	public String m_dob = "";
	
	public String m_hometown = "";
	
	public String m_school = "";

	public UserData() {
		
	}
	
	//TODO add a create user method that is easy to change with changing user
	//properties
}
