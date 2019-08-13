package com.divya.greetings.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.agent.model.NewService;
import com.gojek.ApplicationConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class GrpcServiceDiscovery {

    protected final ApplicationConfiguration config;
    NewService service;
    private String consulClientHost;
    private int consulClientPort;
    final String consulHealthcheckInterval;
    final String consulDeregisterCriticalServiceAfter;
    private static final Logger logger = Logger.getLogger(GrpcServiceDiscovery.class.getName());

    public GrpcServiceDiscovery(ApplicationConfiguration config, Integer servicePort) {
        this.config = config;
        this.consulDeregisterCriticalServiceAfter = config.getValueAsString("CONSUL_DEREGISTER_CRITICAL_AFTER",
                "30s");
        this.consulClientHost = config.getValueAsString("CONSUL_CLIENT_HOST", "localhost");
        this.consulClientPort = config.getValueAsInt("CONSUL_CLIENT_PORT", 8500);
        this.consulHealthcheckInterval = config.getValueAsString("CONSUL_HEALTHCHECK_INTERVAL", "30s");
        service = new NewService();
        service.setName(config.getValueAsString("APP_NAME"));
        service.setPort(servicePort);
        service.setId(config.getValueAsString("APP_NODE_ID", hostname()));
        service.setAddress(config.getValueAsString("APP_NODE_IP", "localhost"));
        service.setTags(getAppTags());

    }

    public void register() {
        ConsulClient consul = new ConsulClient(consulClientHost, consulClientPort);
            consul.agentServiceRegister(service);
        registerGrpcCheck(consul);
        logger.info("register");
    }

      private void registerGrpcCheck(ConsulClient consul) {
        NewCheck newCheck = new NewCheck();
        newCheck.setServiceId(service.getId());
        newCheck.setGrpc(service.getAddress() + ":" + service.getPort());
          logger.info("register grpc check address and port"+ service.getAddress() + service.getPort());
            newCheck.setName(service.getName() + "-grpc-check");
        newCheck.setId(service.getId() + "-grpc-check");
        newCheck.setInterval(consulHealthcheckInterval);
        newCheck.setTimeout(consulHealthcheckInterval);
        newCheck.setGrpcUseTLS(false);
        newCheck.setDeregisterCriticalServiceAfter(consulDeregisterCriticalServiceAfter);
        consul.agentCheckRegister(newCheck);
        logger.info("register grpc check");
    }


    private String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    private List<String> getAppTags() {
        List<String> tags = new ArrayList<>();
        String appTags = config.getValueAsString("APP_TAGS");
        tags.addAll(Arrays.asList(appTags.split(",")));
        if (config.getValueAsBoolean("ENABLE_APP_NODE_ID_IN_CONSUL_TAGS", false)) {
            tags.add(config.getValueAsString("APP_NODE_ID", hostname()));
        }
        return tags;
    }

}
