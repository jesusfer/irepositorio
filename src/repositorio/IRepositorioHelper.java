package repositorio;


/**
* repositorio/IRepositorioHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Tuesday, August 29, 2017 6:17:14 PM CEST
*/

abstract public class IRepositorioHelper
{
  private static String  _id = "IDL:repositorio/IRepositorio:1.0";

  public static void insert (org.omg.CORBA.Any a, repositorio.IRepositorio that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static repositorio.IRepositorio extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (repositorio.IRepositorioHelper.id (), "IRepositorio");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static repositorio.IRepositorio read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_IRepositorioStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, repositorio.IRepositorio value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static repositorio.IRepositorio narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof repositorio.IRepositorio)
      return (repositorio.IRepositorio)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      repositorio._IRepositorioStub stub = new repositorio._IRepositorioStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static repositorio.IRepositorio unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof repositorio.IRepositorio)
      return (repositorio.IRepositorio)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      repositorio._IRepositorioStub stub = new repositorio._IRepositorioStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
