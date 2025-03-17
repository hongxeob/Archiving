package practice.behavioral.templatemethod.network;

import java.util.logging.Logger;

public class Facebook extends Network {
    private Logger log = Logger.getLogger(Facebook.class.getName());

    public Facebook(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public boolean logIn(String userName, String password) {
        log.info("=== 유저 로그인 정보 체크 ===");
        log.info("=== 유저 이름 : " + this.userName);
        System.out.print("Password: ");
        for (int i = 0; i < this.password.length(); i++) {
            System.out.print("*");
        }
        simulateNetworkLatency();
        log.info("=== 페이스북 로그인 성공 ===");
        return true;
    }

    public boolean sendData(byte[] data) {
        boolean messagePosted = true;
        if (messagePosted) {
            System.out.println("메시지: '" + new String(data) + "' 페이스북에 게시 성공");
            return true;
        } else {
            return false;
        }
    }

    public void logOut() {
        System.out.println("User: '" + userName + "' was logged out from FaceBook");
    }

    private void simulateNetworkLatency() {
        try {
            int i = 0;
            System.out.println();
            while (i < 10) {
                System.out.print(".");
                Thread.sleep(500);
                i++;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
