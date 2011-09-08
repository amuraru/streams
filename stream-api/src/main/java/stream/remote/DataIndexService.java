package stream.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import stream.data.Data;
import stream.io.DataStream;

public interface DataIndexService
    extends Remote
{
    public Set<String> listDataSets() throws RemoteException;
    
    public DataStream openStream( String dataSetName ) throws RemoteException;
    
    public Data get( Long id ) throws RemoteException;
    
    public void insert( String dataSetName, Data item ) throws RemoteException;
    
    public void insert( String dataSetName, Data item, String[] indexes ) throws RemoteException;
}