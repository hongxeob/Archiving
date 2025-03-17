package practice.behavioral.templatemethod.network;

public class Twitter extends Network {
    private java.util.logging.Logger log = java.util.logging.Logger.getLogger(Twitter.class.getName());

    public Twitter(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    boolean logIn(String userName, String password) {
        log.info("=== 유저 로그인 정보 체크 ===");
        log.info("=== 유저 이름 : " + this.userName);
        System.out.print("Password: ");
        for (int i = 0; i < this.password.length(); i++) {
            System.out.print("*");
        }
        simulateNetworkLatency();
        log.info("=== 트위터 로그인 성공 ===");
        return true;
    }

    @Override
    void logOut() {
        System.out.println("=== 로그아웃 성공 ===");
    }

    @Override
    boolean sendData(byte[] data) {
        boolean messagePosted = true;
        if (messagePosted) {
            System.out.println("=== 메시지: '" + new String(data) + "' 트위터에 게시 성공 ===");
            return true;
        }
        return false;
    }

    private void simulateNetworkLatency() {
        try {
            int i = 0;
            System.out.println();
            while (i < 10) {
                System.out.print(".");
                Thread.sleep(100);
                i++;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
