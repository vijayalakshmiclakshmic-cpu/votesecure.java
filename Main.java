import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    // DATA STORAGE
    static ArrayList<Voter> voters = new ArrayList<>();
    static ArrayList<LocalDateTime> votingTimes = new ArrayList<>();

    static LinkedHashMap<String, Integer> candidateVotes =
            new LinkedHashMap<>();

    // CANDIDATES
    static {
    candidateVotes.put("Candidate A", 0);
    candidateVotes.put("Candidate B", 0);
    candidateVotes.put("Candidate C", 0);
}

    // FEATURE 1: LOAD VOTER LIST FROM CSV
    public static void loadVoterList() {

        // Do not reload after voting has started
        for (Voter voter : voters) {
            if (voter.hasVoted) {
                System.out.println(
                    "Cannot reload voter list after voting has started!"
                );
                return;
            }
        }

        ArrayList<Voter> loadedVoters = new ArrayList<>();
        HashSet<String> voterIds = new HashSet<>();

        String filePath = "data/voters.csv";

        try (BufferedReader br = Files.newBufferedReader(
                Paths.get(filePath), StandardCharsets.UTF_8)) {

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                // Skip CSV header
                if (firstLine) {
                    firstLine = false;

                    if (line.toLowerCase().startsWith("voterid,")) {
                        continue;
                    }
                }

                String[] data = line.split(",", -1);

                if (data.length != 4) {
                    System.out.println("Skipping invalid CSV row: " + line);
                    continue;
                }

                String voterId = data[0].trim();
                String name = data[1].trim();
                String gender = data[3].trim();

                try {
                    int age = Integer.parseInt(data[2].trim());

                    if (voterId.isEmpty() || name.isEmpty()
                            || gender.isEmpty() || age <= 0) {
                        System.out.println("Skipping invalid voter: " + line);
                        continue;
                    }

                    if (!voterIds.add(voterId)) {
                        System.out.println(
                            "Duplicate voter ID skipped: " + voterId
                        );
                        continue;
                    }

                    loadedVoters.add(
                        new Voter(voterId, name, age, gender)
                    );

                } catch (NumberFormatException e) {
                    System.out.println("Invalid age in row: " + line);
                }
            }

            if (loadedVoters.isEmpty()) {
                System.out.println("No valid voter records found!");
                return;
            }

            voters.clear();
            voters.addAll(loadedVoters);

            votingTimes.clear();

            candidateVotes.replaceAll((candidate, votes) -> 0);

            System.out.println("\nVoter list loaded successfully!");
            System.out.println(
                "Total registered voters: " + voters.size()
            );

            System.out.println("\nRegistered Voters:");

            for (Voter voter : voters) {
                System.out.println(
                    voter.voterId + " | " +
                    voter.name + " | " +
                    voter.age + " | " +
                    voter.gender + " | Not Voted"
                );
            }

        } catch (IOException e) {
            System.out.println("Unable to read voter file!");
            System.out.println(
                "Please check whether data/voters.csv exists."
            );
        }
    }

    // FEATURE 2: VERIFY VOTER
    public static void verifyVoter(Scanner sc) {

        if (voters.isEmpty()) {
            System.out.println("Please load the voter list first!");
            return;
        }

        System.out.print("Enter Voter ID: ");
        String id = sc.nextLine().trim();

        for (Voter voter : voters) {

            if (voter.voterId.equalsIgnoreCase(id)) {

                System.out.println("\nVoter Found!");
                System.out.println("Name: " + voter.name);
                System.out.println("Age: " + voter.age);
                System.out.println("Gender: " + voter.gender);

                if (voter.hasVoted) {
                    System.out.println("Status: Already Voted");
                } else {
                    System.out.println("Status: Not Voted");
                }

                return;
            }
        }

        System.out.println("Voter ID not found!");
    }

    // FEATURE 3: MARK VOTER AS VOTED AND CAST VOTE
    public static void markVoterAsVoted(Scanner sc) {

        if (voters.isEmpty()) {
            System.out.println("Please load the voter list first!");
            return;
        }

        System.out.print("Enter Voter ID: ");
        String id = sc.nextLine().trim();

        Voter selectedVoter = null;

        for (Voter voter : voters) {

            if (voter.voterId.equalsIgnoreCase(id)) {
                selectedVoter = voter;
                break;
            }
        }

        if (selectedVoter == null) {
            System.out.println("Voter ID not found!");
            return;
        }

        if (selectedVoter.hasVoted) {
            System.out.println("Sorry! This voter has already voted.");
            return;
        }

        System.out.println("\nWelcome, " + selectedVoter.name + "!");
       System.out.println("\nChoose your candidate:");
System.out.println("1. Candidate A");
System.out.println("2. Candidate B");
System.out.println("3. Candidate C");

System.out.print("Enter your choice: ");
String candidateChoice = sc.nextLine();

String selectedCandidate;

switch (candidateChoice) {
    case "1":
        selectedCandidate = "Candidate A";
        break;
    case "2":
        selectedCandidate = "Candidate B";
        break;
    case "3":
        selectedCandidate = "Candidate C";
        break;
    default:
        System.out.println("Invalid candidate choice!");
        return;
}


        // Store only the candidate's total, not the voter's selection
        candidateVotes.put(
            selectedCandidate,
            candidateVotes.get(selectedCandidate) + 1
        );

        selectedVoter.hasVoted = true;
        votingTimes.add(LocalDateTime.now());

        System.out.println("\nVote cast successfully!");
        
System.out.println("Vote recorded successfully!");
        System.out.println("Voter status updated to: Already Voted");
        System.out.println("Thank you for voting!");
    }

    // FEATURE 4: SHOW HOURLY TURNOUT
    public static void showHourlyTurnout() {

        int totalVoters = voters.size();
        int totalVotes = votingTimes.size();

        System.out.println("\n========== HOURLY TURNOUT ==========");

        if (totalVoters == 0) {
            System.out.println("Please load the voter list first!");
            return;
        }

        double percentage =
                (totalVotes * 100.0) / totalVoters;

        System.out.println(
            "Total Registered Voters: " + totalVoters
        );

        System.out.println("Total Votes Cast: " + totalVotes);

        System.out.printf(
            "Overall Turnout: %.2f%%%n", percentage
        );

        System.out.println("\nHourly Voting Details:");

        for (int hour = 0; hour < 24; hour++) {

            int count = 0;

            for (LocalDateTime time : votingTimes) {
                if (time.getHour() == hour) {
                    count++;
                }
            }

            if (count > 0) {

                double hourlyPercentage =
                        (count * 100.0) / totalVoters;

                System.out.printf(
                    "%02d:00 - %02d:59 : %d votes (%.2f%%)%n",
                    hour, hour, count, hourlyPercentage
                );
            }
        }

        System.out.println("====================================");
    }
    static void showDemographicTurnout() {

    int age18to25 = 0;
    int age26to40 = 0;
    int above40 = 0;

    int maleVotes = 0;
    int femaleVotes = 0;
    int otherVotes = 0;

    for (Voter voter : voters) {

        if (voter.hasVoted) {

            // Count by age group
            if (voter.age >= 18 && voter.age <= 25) {
                age18to25++;
            } 
            else if (voter.age >= 26 && voter.age <= 40) {
                age26to40++;
            } 
            else if (voter.age > 40) {
                above40++;
            }

            // Count by gender
            if (voter.gender.equalsIgnoreCase("Male")) {
                maleVotes++;
            } 
            else if (voter.gender.equalsIgnoreCase("Female")) {
                femaleVotes++;
            } 
            else {
                otherVotes++;
            }
        }
    }

    System.out.println("\n===== DEMOGRAPHIC TURNOUT =====");

    System.out.println("\nVoting by Age Group:");
    System.out.println("18-25 years: " + age18to25);
    System.out.println("26-40 years: " + age26to40);
    System.out.println("Above 40 years: " + above40);

    System.out.println("\nVoting by Gender:");
    System.out.println("Male: " + maleVotes);
    System.out.println("Female: " + femaleVotes);
    System.out.println("Other/Unspecified: " + otherVotes);
}

    // FEATURE 5: COUNT VOTES AND DECLARE RESULT
    public static void viewElectionResults() {

        System.out.println("\n========== ELECTION RESULTS ==========");

        int highestVotes = 0;
        int totalVotes = 0;

        for (String candidate : candidateVotes.keySet()) {

            int votes = candidateVotes.get(candidate);

            System.out.println(
                candidate + " : " + votes + " votes"
            );

            totalVotes += votes;

            if (votes > highestVotes) {
                highestVotes = votes;
            }
        }

        System.out.println("--------------------------------------");
        System.out.println("Total Votes: " + totalVotes);

        if (totalVotes == 0) {
            System.out.println("No votes have been cast yet!");
            System.out.println("======================================");
            return;
        }

        int winnerCount = 0;

        for (String candidate : candidateVotes.keySet()) {

            if (candidateVotes.get(candidate) == highestVotes) {
                winnerCount++;
            }
        }

        if (winnerCount > 1) {

            System.out.println("\nRESULT: Election Tied!");
            System.out.println("Candidates with highest votes:");

            for (String candidate : candidateVotes.keySet()) {

                if (candidateVotes.get(candidate) == highestVotes) {
                    System.out.println(candidate);
                }
            }

        } else {

            for (String candidate : candidateVotes.keySet()) {

                if (candidateVotes.get(candidate) == highestVotes) {

                    System.out.println(
                        "\nWINNER: " + candidate
                    );

                    System.out.println(
                        "Total Votes: " + highestVotes
                    );

                    break;
                }
            }
        }

        System.out.println("======================================");
    }

    // MAIN METHOD
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int choice = 0;

        do {

            System.out.println("\n================================");
            System.out.println("          VOTESECURE");
            System.out.println("  Polling Booth Verification");
            System.out.println("================================");

            System.out.println("1. Load Voter List");
            System.out.println("2. Verify Voter");
            System.out.println("3. Cast Vote");
            System.out.println("4. Show Hourly Turnout");
            System.out.println("5. View Election Results");
            System.out.println("6. Exit");
            System.out.println("7. View Demographic Turnout");

            System.out.print("\nEnter your choice: ");

            String input = sc.nextLine().trim();

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                    "Please enter a number between 1 and 7."
                );
                choice = 0;
                continue;
            }

            switch (choice) {

                case 1:
                    loadVoterList();
                    break;

                case 2:
                    verifyVoter(sc);
                    break;

                case 3:
                    markVoterAsVoted(sc);
                    break;

                case 4:
                    showHourlyTurnout();
                    break;

                case 5:
                    viewElectionResults();
                    break;

                case 6:
                    System.out.println(
                        "Thank you for using VoteSecure!"
                    );
                    break;
                    case 7:
    showDemographicTurnout();
    break;

                default:
                    System.out.println(
                        "Invalid choice! Please try again."
                    );
            }

        } while (choice != 6);

        sc.close();
    }
}
