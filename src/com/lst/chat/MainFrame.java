package com.lst.chat;
/**
 * 1. 完成此段代码；
 * 2. 写注释；
 * 3. 完成在窗体关闭时，关闭Socket的功能（百度）。
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;


public class MainFrame extends JFrame {
	JPanel pnlMain = null;
	JPanel pnlBottom = null;
	JTextPane txtContent = null;
	JList lstUsers = null;
	JTextPane txtSend = null;
	JButton btnSend = null;
	Socket socket = null;
	ServerSocket serverSocket = null;
	List<SocketThread> socketThreads = new ArrayList<SocketThread>();
	JScrollPane sclpContent = null;

	boolean stoped = false;

	public void insertSendPic(ImageIcon imgIc) {
		//jpMsg.setCaretPosition(docChat.getLength()); // 设置插入位置
		txtSend.insertIcon(imgIc); // 插入图片
		System.out.print(imgIc.toString());
		//insert(new FontAttrib()); // 这样做可以换行
	}

	public MainFrame() throws HeadlessException {
		init();
	}

	private void init() {
		this.setSize(new Dimension(400, 300));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// 向所有在线客户端发送强制退出通知
				Timestamp now = new Timestamp(System.currentTimeMillis());	// 时间
				for (SocketThread socketThread : socketThreads) {
					socketThread.writeData("ClientStop:" + now.toString());
				}

				// 关闭监听循环
				stoped = true;
				Socket stopSocket = null;
				try {
					stopSocket = new Socket("127.0.0.1", 9309);
					DataOutputStream dos = new DataOutputStream(stopSocket.getOutputStream());
					// 向服务端发送服务端退出通知，解除ServerSocket挂起状态
					dos.writeUTF("ServerStop");
					dos.flush();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				finally {
					MySocket.closeSocket(stopSocket);
				}
			}
		});

		pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		this.setContentPane(pnlMain);

		txtContent = new JTextPane();
		txtContent.setBackground(new Color(220, 220, 220));
		sclpContent = new JScrollPane();
		sclpContent.setViewportView(txtContent);
		pnlMain.add(sclpContent, BorderLayout.CENTER);

		Object[] objs = new Object[1];
		objs[0] = "全部";
		lstUsers = new JList(objs);
		pnlMain.add(lstUsers, BorderLayout.EAST);

		pnlBottom = new JPanel();
		pnlBottom.setLayout(new BorderLayout());

		txtSend = new JTextPane();

		btnSend = new JButton("发送");
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DataOutputStream dos = null;
				if (socket != null) {
					try {
						dos = new DataOutputStream(socket.getOutputStream());
						dos.writeUTF(txtSend.getText());
						dos.flush();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		pnlBottom.add(txtSend, BorderLayout.CENTER);
		pnlBottom.add(btnSend, BorderLayout.EAST);
		pnlMain.add(pnlBottom, BorderLayout.SOUTH);

		this.setVisible(true);

		try {
			serverSocket = new ServerSocket(9309);

			while (!stoped) {
				socket = serverSocket.accept();	// 线程挂起

				SocketThread socketThread = new SocketThread(socket, txtContent, socketThreads, this);
				socketThreads.add(socketThread);

				Thread thread = new Thread(socketThread);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void showUsers() {
		List users = new ArrayList();
		users.add("全部");
		for (SocketThread socketThread : socketThreads) {
			users.add(socketThread.getName());
		}
		lstUsers.setListData(users.toArray());
		pnlMain.add(lstUsers, BorderLayout.EAST);
		lstUsers.repaint();
		pnlMain.repaint();
	}


}
