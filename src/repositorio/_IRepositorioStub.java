package repositorio;


/**
* repositorio/_IRepositorioStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Wednesday, August 30, 2017 12:59:48 AM CEST
*/

public class _IRepositorioStub extends org.omg.CORBA.portable.ObjectImpl implements repositorio.IRepositorio
{


  ///////////////////////////////////
  public repositorio.IRepositorio padre ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_padre", true);
                $in = _invoke ($out);
                repositorio.IRepositorio $result = repositorio.IRepositorioHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return padre (        );
            } finally {
                _releaseReply ($in);
            }
  } // padre


  ///////////////////////////////////
  public void padre (repositorio.IRepositorio newPadre)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_padre", true);
                repositorio.IRepositorioHelper.write ($out, newPadre);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                padre (newPadre        );
            } finally {
                _releaseReply ($in);
            }
  } // padre

  public repositorio.IRepositorio[] subordinados ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_subordinados", true);
                $in = _invoke ($out);
                repositorio.IRepositorio $result[] = repositorio.IRepositorioSeqHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return subordinados (        );
            } finally {
                _releaseReply ($in);
            }
  } // subordinados

  public String nombre ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_get_nombre", true);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return nombre (        );
            } finally {
                _releaseReply ($in);
            }
  } // nombre

  public void nombre (String newNombre)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("_set_nombre", true);
                $out.write_string (newNombre);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                nombre (newNombre        );
            } finally {
                _releaseReply ($in);
            }
  } // nombre


  // B�squeda por palabras clave
  public repositorio.Coincidencia[] buscar (String palabraClave)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("buscar", true);
                $out.write_string (palabraClave);
                $in = _invoke ($out);
                repositorio.Coincidencia $result[] = repositorio.ResultadosHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return buscar (palabraClave        );
            } finally {
                _releaseReply ($in);
            }
  } // buscar


  // TODO Recuperar documentos
  public repositorio.ITransferencia iniciarDescarga (String nombre) throws repositorio.ArchivoNoEncontradoException
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("iniciarDescarga", true);
                $out.write_string (nombre);
                $in = _invoke ($out);
                repositorio.ITransferencia $result = repositorio.ITransferenciaHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:repositorio/ArchivoNoEncontradoException:1.0"))
                    throw repositorio.ArchivoNoEncontradoExceptionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return iniciarDescarga (nombre        );
            } finally {
                _releaseReply ($in);
            }
  } // iniciarDescarga


  // Registrar repositorios subordinados
  public String registrar (repositorio.IRepositorio referencia)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("registrar", true);
                repositorio.IRepositorioHelper.write ($out, referencia);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return registrar (referencia        );
            } finally {
                _releaseReply ($in);
            }
  } // registrar

  public String registrarConNombre (repositorio.IRepositorio referencia, String nombre)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("registrarConNombre", true);
                repositorio.IRepositorioHelper.write ($out, referencia);
                $out.write_string (nombre);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return registrarConNombre (referencia, nombre        );
            } finally {
                _releaseReply ($in);
            }
  } // registrarConNombre


  // Dar de baja un repositorio
  public void baja (String nombre)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("baja", true);
                $out.write_string (nombre);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                baja (nombre        );
            } finally {
                _releaseReply ($in);
            }
  } // baja

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:repositorio/IRepositorio:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _IRepositorioStub
