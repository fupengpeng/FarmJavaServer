package com.jiudianlianxian.test;

import java.util.Vector;

public class MsgQueue {

	private Vector<Object> queue = null;

	public MsgQueue() {
		queue = new Vector<Object>();
	}

	// �����Ϣ
	public synchronized void addMsg(Object o) {
		queue.add(o);
	}

	// ������Ϣ
	public synchronized void ergodicMsg() {
		for (Object string : queue) {

		}
	}
	
	//������Ϣ

	// ɾ����Ϣ
	public synchronized Object recv() {
		if (queue.size() == 0)
			return null;
		Object o = queue.firstElement();
		queue.removeElementAt(0);// or queue[0] = null can also work
		return o;
	}
}
