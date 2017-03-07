package com.lst.chat;

import java.io.IOException;
import java.net.Socket;

public class MySocket {
	public static void closeSocket(Socket socket) {
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
