package yatraa.model; // model package holds all data classes (User, Bus, Route, etc.)

// User class - represents one person using the Yaatri app
// A user can be an admin, a driver, or a passenger
public class User {

    private String id;            // unique ID for this user, e.g. "U001"
    private String fullName;      // the user's full name, e.g. "Ram Shrestha"
    private String phoneNumber;   // phone number used to login, e.g. "9800000001"
    private String password;      // password used to login
    private String role;          // what type of user: "admin", "driver", or "passenger"
    private int age;              // age of the user
    private String sex;           // gender: "Male", "Female", etc.
    private String contactDetail; // extra contact info like email or address

    // first constructor - used when we only need the basic 5 fields
    public User(String id, String fullName, String phoneNumber, String password, String role) {
        this.id = id;                   // set the unique ID
        this.fullName = fullName;       // set the full name
        this.phoneNumber = phoneNumber; // set the phone number
        this.password = password;       // set the password
        this.role = role;               // set the role (admin/driver/passenger)
    }

    // second constructor - used when we also need age, sex and contact info
    // it calls the first constructor above, then adds the extra fields
    public User(String id, String fullName, String phoneNumber, String password,
                String role, int age, String sex, String contactDetail) {
        this(id, fullName, phoneNumber, password, role); // call the first constructor
        this.age = age;                       // set the age
        this.sex = sex;                       // set the sex/gender
        this.contactDetail = contactDetail;   // set the contact detail
    }

    // getters - these methods let other classes READ the values of this user

    public String getId()            { return id; }            // returns the user ID
    public String getFullName()      { return fullName; }      // returns the full name
    public String getPhoneNumber()   { return phoneNumber; }   // returns the phone number
    public String getPassword()      { return password; }      // returns the password
    public String getRole()          { return role; }          // returns the role
    public int getAge()              { return age; }           // returns the age
    public String getSex()           { return sex; }           // returns the sex/gender
    public String getContactDetail() { return contactDetail; } // returns contact detail

    // setters - these methods let other classes UPDATE certain values

    public void setFullName(String fullName)         { this.fullName = fullName; }         // update name
    public void setAge(int age)                      { this.age = age; }                   // update age
    public void setSex(String sex)                   { this.sex = sex; }                   // update sex
    public void setContactDetail(String contactDetail) { this.contactDetail = contactDetail; } // update contact
}
