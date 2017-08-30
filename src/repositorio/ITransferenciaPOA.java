package repositorio;


/**
* repositorio/ITransferenciaPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Wednesday, August 30, 2017 12:59:48 AM CEST
*/


///////////////////////////////////
public abstract class ITransferenciaPOA extends org.omg.PortableServer.Servant
 implements repositorio.ITransferenciaOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("solicitarBloque", new java.lang.Integer (0));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // repositorio/ITransferencia/solicitarBloque
       {
         try {
           byte $result[] = null;
           $result = this.solicitarBloque ();
           out = $rh.createReply();
           repositorio.BytesHelper.write (out, $result);
         } catch (repositorio.ArchivoNoEncontradoException $ex) {
           out = $rh.createExceptionReply ();
           repositorio.ArchivoNoEncontradoExceptionHelper.write (out, $ex);
         }
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:repositorio/ITransferencia:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ITransferencia _this() 
  {
    return ITransferenciaHelper.narrow(
    super._this_object());
  }

  public ITransferencia _this(org.omg.CORBA.ORB orb) 
  {
    return ITransferenciaHelper.narrow(
    super._this_object(orb));
  }


} // class ITransferenciaPOA
