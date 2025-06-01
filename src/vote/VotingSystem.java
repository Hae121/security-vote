package vote;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import dto.UserDTO;
import manager.UserManager;
import manager.VoteManager;

public class VotingSystem {
    private static final String USER_DATA_DIR = "user_data";
    private static final String VOTE_DATA_DIR = "vote_data";
    private static final String[] CANDIDATES = {"후보자1", "후보자2", "후보자3"};
    
    private UserManager userManager;
    private VoteManager voteManager;
    private Scanner scanner;
    
    public VotingSystem() {
        this.userManager = new UserManager();
        this.voteManager = new VoteManager();
        this.scanner = new Scanner(System.in);
        initializeDirectories();
        initializeDefaultUsers();
    }
    
    private void initializeDirectories() {
        new File(USER_DATA_DIR).mkdirs();
        new File(VOTE_DATA_DIR).mkdirs();
        System.out.println("📁 디렉토리 초기화 완료");
    }
    
    private void initializeDefaultUsers() {
        try {
            // 기본 사용자 생성
            userManager.createUser("hae", "1234", false);
            userManager.createUser("admin", "admin123", true);
            System.out.println("👥 기본 사용자 계정 생성 완료");
        } catch (Exception e) {
            System.err.println("❌ 기본 사용자 생성 실패: " + e.getMessage());
        }
    }
    
    public void start() {
        System.out.println("🗳️  전자봉투 투표 시스템에 오신 것을 환영합니다!");
        System.out.println("==========================================");
        
        while (true) {
            try {
                showMainMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // 개행 문자 소비
                
                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        System.out.println("👋 시스템을 종료합니다.");
                        return;
                    case 3:
                        resetVoteData();
                        break;
                    default:
                        System.out.println("❌ 잘못된 선택입니다.");
                }
            } catch (Exception e) {
                System.err.println("❌ 오류 발생: " + e.getMessage());
                scanner.nextLine(); // 잘못된 입력 제거
            }
        }
    }

    private void resetVoteData() {
        System.out.print("⚠️  모든 투표 데이터를 초기화하시겠습니까? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            try {
                voteManager.resetAllVoteData();
            } catch (Exception e) {
                System.err.println("❌ 투표 데이터 초기화 오류: " + e.getMessage());
            }
        } else {
            System.out.println("❌ 초기화가 취소되었습니다.");
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n📋 메인 메뉴");
        System.out.println("1. 로그인");
        System.out.println("2. 종료");
        System.out.println("3. 투표 데이터 초기화");
        System.out.print("선택: ");
    }
    private void login() {
        System.out.print("🔐 사용자 ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("🔐 비밀번호: ");
        String password = scanner.nextLine().trim();
        
        try {
            UserDTO user = userManager.authenticate(id, password);
            if (user != null) {
                System.out.println("✅ 로그인 성공! 환영합니다, " + user.getId() + "님");
                
                if (user.isAdmin()) {
                    adminMenu(user);
                } else {
                    userMenu(user);
                }
            } else {
                System.out.println("❌ 로그인 실패: ID 또는 비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            System.err.println("❌ 인증 오류: " + e.getMessage());
        }
    }
    
    private void userMenu(UserDTO user) {
        while (true) {
            System.out.println("\n👤 사용자 메뉴 - " + user.getId());
            System.out.println("1. 투표하기");
            System.out.println("2. 로그아웃");
            System.out.print("선택: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        vote(user);
                        break;
                    case 2:
                        System.out.println("👋 로그아웃 되었습니다.");
                        return;
                    default:
                        System.out.println("❌ 잘못된 선택입니다.");
                }
            } catch (Exception e) {
                System.err.println("❌ 오류 발생: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }
    
    private void adminMenu(UserDTO user) {
        while (true) {
            System.out.println("\n👨‍💼 관리자 메뉴 - " + user.getId());
            System.out.println("1. 투표 결과 확인");
            System.out.println("2. 투표 데이터 검증");
            System.out.println("3. 로그아웃");
            System.out.print("선택: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        showVoteResults();
                        break;
                    case 2:
                        verifyVoteData();
                        break;
                    case 3:
                        System.out.println("👋 로그아웃 되었습니다.");
                        return;
                    default:
                        System.out.println("❌ 잘못된 선택입니다.");
                }
            } catch (Exception e) {
                System.err.println("❌ 오류 발생: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }
    
    private void vote(UserDTO user) {
        try {
            // 이미 투표했는지 확인
            if (voteManager.hasVoted(user.getId())) {
                System.out.println("⚠️  이미 투표하셨습니다. 중복 투표는 불가능합니다.");
                return;
            }
            
            System.out.println("\n🗳️  투표 후보자 목록:");
            for (int i = 0; i < CANDIDATES.length; i++) {
                System.out.println((i + 1) + ". " + CANDIDATES[i]);
            }
            
            System.out.print("투표할 후보자 번호를 선택하세요: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice < 1 || choice > CANDIDATES.length) {
                System.out.println("❌ 잘못된 후보자 번호입니다.");
                return;
            }
            
            String selectedCandidate = CANDIDATES[choice - 1];
            System.out.println("🗳️  선택한 후보자: " + selectedCandidate);
            System.out.print("투표를 확정하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                voteManager.castVote(user.getId(), selectedCandidate);
                System.out.println("✅ 투표가 완료되었습니다!");
            } else {
                System.out.println("❌ 투표가 취소되었습니다.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 투표 오류: " + e.getMessage());
            scanner.nextLine();
        }
    }
    
    private void showVoteResults() {
        try {
            Map<String, Integer> results = voteManager.countVotes();
            int totalVotes = results.values().stream().mapToInt(i -> i).sum();
            
            System.out.println("\n📊 투표 결과:");
            System.out.println("==================");
            
            for (String candidate : CANDIDATES) {
                int votes = results.getOrDefault(candidate, 0);
                double percentage = totalVotes > 0 ? (votes * 100.0 / totalVotes) : 0.0;
                System.out.printf("%-10s: %3d표 (%.1f%%)\n", candidate, votes, percentage);
            }
            
            System.out.println("==================");
            System.out.println("총 투표수: " + totalVotes + "표");
            
        } catch (Exception e) {
            System.err.println("❌ 결과 조회 오류: " + e.getMessage());
        }
    }
    
    private void verifyVoteData() {
        try {
            boolean isValid = voteManager.verifyAllVotes();
            if (isValid) {
                System.out.println("✅ 모든 투표 데이터가 유효합니다.");
            } else {
                System.out.println("⚠️  일부 투표 데이터에 문제가 있습니다.");
            }
        } catch (Exception e) {
            System.err.println("❌ 검증 오류: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        VotingSystem system = new VotingSystem();
        system.start();
    }
}