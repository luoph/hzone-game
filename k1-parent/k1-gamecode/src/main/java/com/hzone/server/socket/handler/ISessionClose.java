package com.hzone.server.socket.handler;

import com.hzone.manager.User;

public interface ISessionClose {
	void close(User user);
}
