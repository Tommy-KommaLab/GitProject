import java.io.*;
import java.net.*;
import java.util.*;

//�`���b�g�T�[�o�X���b�h
public class ChatServerThread extends Thread {
	private static List<ChatServerThread> threads = new ArrayList<ChatServerThread>();// �X���b�h�S
	private Socket socket;// �\�P�b�g

	// �R���X�g���N�^
	public ChatServerThread(Socket socket) {
		super();
		this.socket = socket;
		threads.add(this);		
	}
	// ����
	public void run() {
		InputStream in = null;
		String message;
		int size;
		byte[] w = new byte[10240];
		try {
			// �X�g���[��
			in = socket.getInputStream();
			while (true) {
				try {
					// ��M�҂�
					size = in.read(w);
					// �ؒf
					if (size <= 0)
						throw new IOException();
					// �ǂݍ���
					message = new String(w, 0, size, "UTF8");
					// �S���Ƀ��b�Z�[�W���M
					sendMessageAll(message);
				} catch (IOException e) {
					socket.close();
					threads.remove(this);
					return;
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	// �S���Ƀ��b�Z�[�W���M
	public void sendMessageAll(String message) {
		ChatServerThread thread;
		for (int i = 0; i < threads.size(); i++) {
			thread = (ChatServerThread) threads.get(i);
			if (thread.isAlive())
				thread.sendMessage(this, message);
		}
	}

	// ���b�Z�[�W���M
	public void sendMessage(ChatServerThread talker, String message) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			out.println(message);
			out.flush();
		} catch (IOException e) {
		}
	}
}
