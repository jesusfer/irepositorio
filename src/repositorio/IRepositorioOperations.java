package repositorio;


/**
* repositorio/IRepositorioOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Dev/workspace352/OrgRepos/idl/IRepositorio.idl
* Wednesday, August 30, 2017 4:08:42 PM CEST
*/

public interface IRepositorioOperations 
{

  ///////////////////////////////////
  repositorio.IRepositorio padre ();

  ///////////////////////////////////
  void padre (repositorio.IRepositorio newPadre);
  repositorio.IRepositorio[] subordinados ();
  String nombre ();
  void nombre (String newNombre);

  // B�squeda por palabras clave
  repositorio.Coincidencia[] buscar (String palabraClave);

  // TODO Recuperar documentos
  repositorio.ITransferencia iniciarDescarga (String nombre) throws repositorio.ArchivoNoEncontradoException;

  // Registrar repositorios subordinados
  String registrar (repositorio.IRepositorio referencia) throws repositorio.RegistroNoPosibleException;
  String registrarConNombre (repositorio.IRepositorio referencia, String nombre) throws repositorio.RegistroNoPosibleException;

  // Dar de baja un repositorio
  void baja (String nombre);
} // interface IRepositorioOperations
