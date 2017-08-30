package repositorio;


/**
* repositorio/IRepositorioSeqHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Wednesday, August 30, 2017 4:08:42 PM CEST
*/

abstract public class IRepositorioSeqHelper
{
  private static String  _id = "IDL:repositorio/IRepositorioSeq:1.0";

  public static void insert (org.omg.CORBA.Any a, repositorio.IRepositorio[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static repositorio.IRepositorio[] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = repositorio.IRepositorioHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (repositorio.IRepositorioSeqHelper.id (), "IRepositorioSeq", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static repositorio.IRepositorio[] read (org.omg.CORBA.portable.InputStream istream)
  {
    repositorio.IRepositorio value[] = null;
    int _len0 = istream.read_long ();
    value = new repositorio.IRepositorio[_len0];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = repositorio.IRepositorioHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, repositorio.IRepositorio[] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      repositorio.IRepositorioHelper.write (ostream, value[_i0]);
  }

}
