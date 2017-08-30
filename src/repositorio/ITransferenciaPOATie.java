package repositorio;


/**
* repositorio/ITransferenciaPOATie.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Wednesday, August 30, 2017 12:59:48 AM CEST
*/


///////////////////////////////////
public class ITransferenciaPOATie extends ITransferenciaPOA
{

  // Constructors

  public ITransferenciaPOATie ( repositorio.ITransferenciaOperations delegate ) {
      this._impl = delegate;
  }
  public ITransferenciaPOATie ( repositorio.ITransferenciaOperations delegate , org.omg.PortableServer.POA poa ) {
      this._impl = delegate;
      this._poa      = poa;
  }
  public repositorio.ITransferenciaOperations _delegate() {
      return this._impl;
  }
  public void _delegate (repositorio.ITransferenciaOperations delegate ) {
      this._impl = delegate;
  }
  public org.omg.PortableServer.POA _default_POA() {
      if(_poa != null) {
          return _poa;
      }
      else {
          return super._default_POA();
      }
  }
  public byte[] solicitarBloque () throws repositorio.ArchivoNoEncontradoException
  {
    return _impl.solicitarBloque();
  } // solicitarBloque

  private repositorio.ITransferenciaOperations _impl;
  private org.omg.PortableServer.POA _poa;

} // class ITransferenciaPOATie