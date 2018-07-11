package cn.fudan.libdb.client;

import cn.fudan.libdb.LibDBConfig;
import cn.fudan.libdb.core.RemoteRepo;
import cn.fudan.libdb.core.RemoteRepoFactory;
import cn.fudan.libdb.thrift.LibDBService;
import cn.fudan.libdb.util.FileUtils;
import com.beust.jcommander.JCommander;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class LibDBServiceClient implements LibDBService.Iface{
    private static Set<LibDBService.Client> clientPool = new HashSet<>();

    private static LibDBService.Client createClient(){
        try{
            String serverIP = LibDBConfig.getConfig(LibDBConfig.PROP_KEY_CLIENT_BIND_IP);
            String serverPort = LibDBConfig.getConfig(LibDBConfig.PROP_KEY_SERVER_BIND_PORT);
            TTransport transport = new TSocket(serverIP, Integer.parseInt(serverPort));
            TProtocol tprotocol = new TBinaryProtocol(transport);
            transport.open();
            return new LibDBService.Client(tprotocol);
        } catch (TTransportException e){
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isClientOpen(LibDBService.Client client) {
        if (! client.getOutputProtocol().getTransport().isOpen() ||
                ! client.getInputProtocol().getTransport().isOpen())
            return false;
        return true;
    }

    public static LibDBServiceClient defaultClient() {
        return new LibDBServiceClient();
    }


    private static LibDBService.Client getAvailableClient() throws TException {
        synchronized (clientPool) {
            if (clientPool.size() == 0) {
                LibDBService.Client client = createClient();
                return client;
            } else {
                Iterator<LibDBService.Client> clientIterator = clientPool.iterator();
                LibDBService.Client client = null;
                while (clientIterator.hasNext()) {
                    client = clientIterator.next();
                    if (isClientOpen(client))
                        break;
                    else {
                        try {
                            client.getOutputProtocol().getTransport().close();
                            client.getInputProtocol().getTransport().close();
                        }
                        catch (Exception ex){}
                        clientIterator.remove();
                    }
                }

                if (client == null) {
                    return createClient();
                }
                else {
                    clientPool.remove(client);
                    return client;
                }
            }
        }
    }


    @Override
    public int ping(int test) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        int result = client.ping(test);
        synchronized (clientPool) {
            clientPool.add(client);
        }
        return result;
    }


    @Override
    public java.lang.String queryLibsByGAV(java.lang.String groupName, java.lang.String artifactId, java.lang.String version, java.lang.String repoType, boolean jsonOutput, int limit) throws org.apache.thrift.TException{
        LibDBService.Client client = getAvailableClient();
        String result = client.queryLibsByGAV(groupName,artifactId,version,repoType,jsonOutput,limit);
        synchronized (clientPool){
            clientPool.add(client);
        }
        return result;
    }



    public static void main(String ... argv) throws TException{
        LibDBArgs libDBArgs = new LibDBArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(libDBArgs)
                .build();
        jCommander.parse(argv);
        if(libDBArgs.isHelp()){
            jCommander.usage();
            return;
        }
        LibDBServiceClient client = LibDBServiceClient.defaultClient();
        client.ping(1);
        if(libDBArgs.isQuery()){
            if(libDBArgs.groupUnset() && libDBArgs.artifactUnset() && libDBArgs.versionUnset()){
                System.out.println("Please set -g or -a or -v for a query!");
                return;
            }
            String outputRes = client.queryLibsByGAV(libDBArgs.getGroupName(), libDBArgs.getArtifactId(), libDBArgs.getVersion(),
                                                    "GENERAL_REPO", libDBArgs.isJsonOutput(), libDBArgs.getLimit());
            if(libDBArgs.outputPathUnset()){
                //command line print
                System.out.println(outputRes);
            }
            else{
                //save to file
                FileUtils.saveStrToFile(outputRes + "\n", libDBArgs.getOutputFilePath());
            }
        }
        else if(libDBArgs.isFetch()){
        }
        else{
            System.err.println("No specified operation, please set -q or -d");
        }


    }
}