
public class Voter {

    String voterId;
    String name;
    int age;
    String gender;
    boolean hasVoted;

    // Constructor
    public Voter(String voterId, String name, int age, String gender) {

        this.voterId = voterId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hasVoted = false;
    }
}
