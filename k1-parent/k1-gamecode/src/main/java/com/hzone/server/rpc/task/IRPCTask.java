package com.hzone.server.rpc.task;

import com.hzone.util.lambda.TMap;

import java.io.Serializable;

@FunctionalInterface
public interface IRPCTask {

    void execute(int id, TMap maps, Serializable[] objects);

}
