package com.lst.chat;

import com.lst.business.LoginManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class SocketThread implements Runnable {

	Socket socket = null;
	JTextPane txtContent = null;
	List<SocketThread> socketThreads = null;
	String name = "";

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public SocketThread(Socket socket, JTextPane txtContent,
						List<SocketThread> socketThreads) {
		super();
		this.socket = socket;
		this.txtContent = txtContent;
		this.socketThreads = socketThreads;
	}

	MainFrame mainFrame = null;
	public SocketThread(Socket socket, JTextPane txtContent,
						List<SocketThread> socketThreads, MainFrame mainFrame) {
		super();
		this.socket = socket;
		this.txtContent = txtContent;
		this.socketThreads = socketThreads;
		this.mainFrame = mainFrame;
	}
	@Override
	public void run() {
		DataInputStream dis = null;
		if (socket != null) {	// 参数 Socket
			try {
				dis = new DataInputStream(socket.getInputStream());
				while (true) {
					String str = dis.readUTF();	// 线程挂起
					System.out.println("Read--" + name + "--" + str);

					Timestamp now = new Timestamp(System.currentTimeMillis());	// 时间
					txtContent.setText(str + ":" + now.toString() + "\n" + txtContent.getText());	// 参数 txtContent

					String [] strs = str.split(":");

					if (strs[0].compareToIgnoreCase("Login") == 0) {
						if (new LoginManager().isLogin(strs[1], strs[2]) != null) {
							// 判读同名用户是否已登录
							boolean canLogin = true;
							for (SocketThread socketThread : socketThreads) {
								if (socketThread.getName() != null
										&& socketThread.getName().compareTo(strs[1]) == 0)
									canLogin = false;
							}
							// 同名用户未登录，则可登录
							if (canLogin) {
								// 记录登录名，用来标识服务器端线程对象
								name = strs[1];
								// 告知客户端登录成功
								writeData("Successed");
								// 组合在线列表中的用户名，告知客户端当前在线用户
								String listStr = "UserList";
								for (int i = 0; i < socketThreads.size(); i++)
									listStr += ":" + socketThreads.get(i).getName();
								writeData(listStr);
								// 通知在线用户增加新登录用户的用户名
								for (SocketThread socketThread : socketThreads) {
									if (socketThread.getName() != null
											&& socketThread.getName().compareTo(name) != 0) {
										socketThread.writeData("AddUser:" + strs[1]);
									}
								}
								// 更新服务器端在线用户名
								mainFrame.showUsers();
							}
							else {
								// 同名用户已登录，拒绝登录
								writeData("Refused");
							}
						}
						else {
							// 告知客户端登录失败
							writeData("Failed");
						}
					}
					// 收到群聊消息
					else if (strs[0].compareToIgnoreCase("ChatAll") == 0) {
						// 将群聊消息发送给所有客户端
						for (SocketThread socketThread : socketThreads) {
							socketThread.writeData("Chat:" + name + ":" + strs[1] + ":" + now.toString());
						}
					}
					// 收到私聊消息
					else if (strs[0].compareToIgnoreCase("Chat") == 0) {
						// 将群聊消息发送给发送者和接收者
						for (SocketThread socketThread : socketThreads) {
							if (socketThread.getName() != null
									&& (socketThread.getName().compareTo(name) == 0
									|| socketThread.getName().compareTo(strs[1]) == 0)) {
								socketThread.writeData("Chat:" + name + ":" + strs[2] + ":" + now.toString());
							}
						}
					}
					// 收到客户端主动退出请求
					else if (strs[0].compareToIgnoreCase("Exit") == 0) {
						// 告知所有在线用户移除退出用户名
						for (SocketThread socketThread : socketThreads) {
							if (socketThread.getName() != null
									&& socketThread.getName().compareTo(name) != 0) {
								socketThread.writeData("RemoveUser:" + name + ":" + now.toString());
							}
						}
						// 告知客户端可以退出，并将线程从线程列表中移除
						for (SocketThread socketThread : socketThreads) {
							if (socketThread.getName() != null
									&& socketThread.getName().compareTo(name) == 0) {
								socketThread.writeData("Exit");
								socketThreads.remove(socketThread);
								break;
							}
						}
						// 更新服务器端在线用户名
						mainFrame.showUsers();
						// 结束当前线程
						break;
					}
					// 收到客户端被动退出答复
					else if (strs[0].compareToIgnoreCase("ClientStop") == 0) {
						// 结束当前线程
						break;
					}
					// 收到服务端主动退出答复
					else if (strs[0].compareToIgnoreCase("ServerStop") == 0) {
						// 结束当前线程
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				MySocket.closeSocket(socket);
			}
		}
	}

	public void writeData(String str) {
		DataOutputStream dos = null;
		if (socket != null) {	// 参数 Socket
			try {
				dos = new DataOutputStream(socket.getOutputStream());

				dos.writeUTF(str);
				dos.flush();

				System.out.println("Write--" + name + "--" + str);
			} catch (IOException e) {
				e.printStackTrace();
			} finally { }
		}
	}

}
