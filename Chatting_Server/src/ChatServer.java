

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//チャットサーバ
public class ChatServer {

	// メイン
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.start(10001);		
	}
	// 開始
	public void start(int port) {
		ServerSocket server;// サーバソケット
		Socket socket;// ソケット
		ChatServerThread thread;// スレッド
		
		try {
			server = new ServerSocket(port);
			System.out.println("チャットサーバ実行開始:" + port);
			while (true) {
				try {
					// 接続待機
					socket = server.accept();

					// チャットサーバスレッド開始
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
