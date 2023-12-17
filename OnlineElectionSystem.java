import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class VoteTask implements Runnable {
    private String voterName;
    private String candidateName;
    private ElectionResultRepository resultRepository;

    public VoteTask(String voterName, String candidateName, ElectionResultRepository resultRepository) {
        this.voterName = voterName;
        this.candidateName = candidateName;
        this.resultRepository = resultRepository;
    }

    @Override
    public void run() {
        // Simulate voting process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Processing vote for voter: " + voterName);

        // Record vote in the result repository
        resultRepository.recordVote(candidateName);
        System.out.println("\nVote cast by " + voterName + " for candidate: " + candidateName + "\n");
    }
}

class ElectionResultRepository {
    private Map<String, Integer> voteCounts;

    public ElectionResultRepository() {
        this.voteCounts = new HashMap<>();
    }

    public void recordVote(String candidateName) {
        voteCounts.put(candidateName, voteCounts.getOrDefault(candidateName, 0) + 1);
    }

    public Map<String, Integer> getVoteCounts() {
        return new HashMap<>(voteCounts);
    }
}

class ElectionReport {
    private ElectionResultRepository resultRepository;

    public ElectionReport(ElectionResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public void generateReport() {
        Map<String, Integer> voteCounts = resultRepository.getVoteCounts();

        System.out.println("\nElection Results:");
        System.out.println("-----------------");

        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            System.out.println("Candidate: " + entry.getKey() + ", Votes: " + entry.getValue());
        }

        String winner = findWinner(voteCounts);
        System.out.println("\nWinner: " + winner);
    }

    private String findWinner(Map<String, Integer> voteCounts) {
        String winner = null;
        int maxVotes = 0;

        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }

        return winner;
    }
}

public class OnlineElectionSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of initial voters: ");
        int numberOfVoters = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Create a thread pool with a fixed number of threads
        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        ElectionResultRepository resultRepository = new ElectionResultRepository();

        // Simulate multiple voters casting votes for different candidates
        for (int i = 1; i <= numberOfVoters; i++) {
            System.out.print("Enter the name of Voter " + i + ": ");
            String voterName = scanner.nextLine();

            // Randomly select a candidate for each vote
            System.out.print("Enter the name of the candidate for " + voterName + ": ");
            String candidateName = scanner.nextLine();

            // Submit each vote as a task to the thread pool
            executorService.submit(new VoteTask(voterName, candidateName, resultRepository));
        }

        executorService.shutdown();
        System.out.println("\nVoting process completed.\n");

        ElectionReport electionReport = new ElectionReport(resultRepository);
        electionReport.generateReport();

        // Allow users to manually add votes
        addVotesManually(scanner, resultRepository);

        scanner.close();
    }

    private static void addVotesManually(Scanner scanner, ElectionResultRepository resultRepository) {
        System.out.println("\nManually Add Votes:");

        while (true) {
            System.out.print("Do you want to manually add a vote? (yes/no): ");
            String choice = scanner.nextLine();

            if ("yes".equalsIgnoreCase(choice)) {
                System.out.print("Enter the name of the voter: ");
                String voterName = scanner.nextLine();

                System.out.print("Enter the name of the candidate: ");
                String candidateName = scanner.nextLine();

                // Record the manually added vote in the result repository
                resultRepository.recordVote(candidateName);
                System.out.println("Vote added by " + voterName + " for candidate: " + candidateName);
            } else if ("no".equalsIgnoreCase(choice)) {
                break;
            } else {
                System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
            }
        }

        // Display updated report after manual votes
        ElectionReport updatedReport = new ElectionReport(resultRepository);
        updatedReport.generateReport();
    }
}
