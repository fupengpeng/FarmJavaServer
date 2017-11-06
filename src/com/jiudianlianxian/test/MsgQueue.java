package com.jiudianlianxian.test;

import java.util.Vector;

public class MsgQueue {

	private Vector<Object> queue = null;

	public MsgQueue() {
		queue = new Vector<Object>();
	}

	// 添加消息
	public synchronized void addMsg(Object o) {
		queue.add(o);
	}

	// 遍历消息
	public synchronized void ergodicMsg() {
		for (Object string : queue) {

		}
	}
	
	//处理消息

	// 删除消息
	public synchronized Object recv() {
		if (queue.size() == 0)
			return null;
		Object o = queue.firstElement();
		queue.removeElementAt(0);// or queue[0] = null can also work
		return o;
	}
}
