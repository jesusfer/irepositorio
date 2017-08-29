package repositorio;


/**
* repositorio/ArchivoDetallesHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Tuesday, August 29, 2017 6:17:14 PM CEST
*/

abstract public class ArchivoDetallesHelper
{
  private static String  _id = "IDL:repositorio/ArchivoDetalles:1.0";

  public static void insert (org.omg.CORBA.Any a, repositorio.ArchivoDetalles that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static repositorio.ArchivoDetalles extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [5];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "nombre",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "archivo",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "directorio",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (repositorio.StringSeqHelper.id (), "StringSeq", _tcOf_members0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "palabrasClave",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[4] = new org.omg.CORBA.StructMember (
            "comentario",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (repositorio.ArchivoDetallesHelper.id (), "ArchivoDetalles", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static repositorio.ArchivoDetalles read (org.omg.CORBA.portable.InputStream istream)
  {
    repositorio.ArchivoDetalles value = new repositorio.ArchivoDetalles ();
    value.nombre = istream.read_string ();
    value.archivo = istream.read_string ();
    value.directorio = istream.read_string ();
    value.palabrasClave = repositorio.StringSeqHelper.read (istream);
    value.comentario = istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, repositorio.ArchivoDetalles value)
  {
    ostream.write_string (value.nombre);
    ostream.write_string (value.archivo);
    ostream.write_string (value.directorio);
    repositorio.StringSeqHelper.write (ostream, value.palabrasClave);
    ostream.write_string (value.comentario);
  }

}