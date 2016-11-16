

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//�`���b�g�T�[�o
public class ChatServer {

	// ���C��
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.start(6001);		
	}
	// �J�n
	public void start(int port) {
		ServerSocket server;// �T�[�o�\�P�b�g
		Socket socket;// �\�P�b�g
		ChatServerThread thread;// �X���b�h
		
		try {
			server = new ServerSocket(port);
			System.out.println("�`���b�g�T�[�o���s�J�n:" + port);
			while (true) {
				try {
					// �ڑ��ҋ@
					socket = server.accept();

					// �`���b�g�T�[�o�X���b�h�J�n
					thread = new ChatServerThread(socket);
					thread.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			
		}
	}	
}
