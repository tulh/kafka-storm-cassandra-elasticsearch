package com.gem.hocvalam.storm.integration;

import com.datastax.driver.core.*;

/**
 * Created by tulh on 08/08/2016.
 */
public class CassandraConnection
{
    private Cluster cluster;
    private Session session;

    private int maxRequestPerConnection = 128;
    private int maxConnectionLocalPerHost = 8;
    private int maxConnectionRemotePerHost = 2;
    private int coreConnectionLocalPerHost = 2;
    private int coreConnectionRemotePerHost = 1;

    public Session getSession()
    {
        return this.session;
    }

    public CassandraConnection(String node, String keyspace, String username, String password)
    {

        PoolingOptions pools = new PoolingOptions();
        pools.setMaxRequestsPerConnection(HostDistance.LOCAL, maxRequestPerConnection);
        pools.setCoreConnectionsPerHost(HostDistance.LOCAL, coreConnectionLocalPerHost);
        pools.setMaxConnectionsPerHost(HostDistance.LOCAL, maxConnectionLocalPerHost);
        pools.setCoreConnectionsPerHost(HostDistance.REMOTE, coreConnectionRemotePerHost);
        pools.setMaxConnectionsPerHost(HostDistance.REMOTE, maxConnectionRemotePerHost);


        cluster = Cluster.builder()
                .addContactPoint(node)
                .withPoolingOptions(pools)
                .withCredentials(username, password)
                .withSocketOptions(new SocketOptions().setTcpNoDelay(true))
                .build();

//        Metadata metadata = cluster.getMetadata();
//        System.out.printf("Connected to cluster: %s\n",
//                metadata.getClusterName());
//        for ( Host host : metadata.getAllHosts() ) {
//            System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n",
//                    host.getDatacenter(), host.getAddress(), host.getRack());
//        }
        session = cluster.connect(keyspace);
    }

    public void close()
    {
        session.close();
        cluster.close();
    }
}
