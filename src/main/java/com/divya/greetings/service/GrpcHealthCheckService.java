package com.divya.greetings.service;

import grpc.health.v1.GrpcHealth;
import grpc.health.v1.HealthGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcHealthCheckService extends HealthGrpc.HealthImplBase {
    private GrpcHealth.HealthCheckResponse.ServingStatus status;
    private static Logger logger = LoggerFactory.getLogger(GrpcHealthCheckService.class);

    public GrpcHealthCheckService() {
        this.status = GrpcHealth.HealthCheckResponse.ServingStatus.SERVING;
    }

    public void check(grpc.health.v1.GrpcHealth.HealthCheckRequest request,
                      io.grpc.stub.StreamObserver<grpc.health.v1.GrpcHealth.HealthCheckResponse> responseObserver) {
        logger.info("Check method called in GrpcHealthCheckService");
        GrpcHealth.HealthCheckResponse response = GrpcHealth.HealthCheckResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
